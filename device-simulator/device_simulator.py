#!/usr/bin/env python3
"""
Costco Mining - Device Simulator
Simulates network devices sending telemetry data to AWS IoT Core via MQTT,
or directly to the Spring Boot API in local mode.

Usage:
    python device_simulator.py --local                 # Local mode (HTTP to Spring Boot API)
    python device_simulator.py                         # AWS IoT Core mode (MQTT, requires certs)
    python device_simulator.py --config my.yaml        # Custom config
    python device_simulator.py --device DEV-001        # Simulate single device
"""

import argparse
import json
import logging
import random
import signal
import sys
import threading
import time
import urllib.request
import urllib.error
from datetime import datetime, timezone
from pathlib import Path

import yaml

try:
    from awscrt import mqtt
    from awsiot import mqtt_connection_builder
    USE_AWS_IOT_SDK = True
except ImportError:
    try:
        import paho.mqtt.client as paho_mqtt
        USE_AWS_IOT_SDK = False
    except ImportError:
        USE_AWS_IOT_SDK = False

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s [%(levelname)s] %(name)s: %(message)s'
)
logger = logging.getLogger('device-simulator')

shutdown_event = threading.Event()


def signal_handler(sig, frame):
    logger.info("Shutdown signal received, stopping...")
    shutdown_event.set()


signal.signal(signal.SIGINT, signal_handler)
signal.signal(signal.SIGTERM, signal_handler)


class TelemetryGenerator:
    """Generates simulated telemetry data for a device."""

    def __init__(self, device_id: str, machine_id: str, interval: int):
        self.device_id = device_id
        self.machine_id = machine_id
        self.interval = interval
        self.engine_hours = random.uniform(100, 5000)
        self.engine_running = True
        self.fuel_level = random.uniform(30, 100)

    def generate(self) -> dict:
        if random.random() < 0.05:
            self.engine_running = not self.engine_running

        if self.engine_running:
            self.engine_hours += self.interval / 3600.0
            self.fuel_level = max(0, self.fuel_level - random.uniform(0.01, 0.05))

        base_lat = -26.2041 + random.uniform(-0.01, 0.01)
        base_lng = 28.0473 + random.uniform(-0.01, 0.01)

        return {
            "deviceId": self.device_id,
            "machineId": self.machine_id,
            "timestamp": datetime.now(timezone.utc).isoformat(),
            "engineRunning": self.engine_running,
            "engineHours": round(self.engine_hours, 2),
            "fuelLevel": round(self.fuel_level, 2),
            "locationLat": round(base_lat, 6),
            "locationLng": round(base_lng, 6),
            "voltage": round(random.uniform(220, 240), 1),
            "temperature": round(random.uniform(60, 95), 1),
            "vibration": round(random.uniform(0.1, 5.0), 2)
        }


class LocalSimulator:
    """Sends telemetry directly to the Spring Boot API via HTTP (for local dev)."""

    def __init__(self, device_config: dict, api_url: str):
        self.device_id = device_config['device_id']
        self.machine_id = device_config['machine_id']
        self.interval = device_config.get('interval_seconds', 30)
        self.api_url = api_url.rstrip('/')
        self.generator = TelemetryGenerator(self.device_id, self.machine_id, self.interval)

    def publish(self, payload: dict):
        data = json.dumps(payload).encode('utf-8')
        req = urllib.request.Request(
            f"{self.api_url}/api/telemetry/ingest",
            data=data,
            headers={'Content-Type': 'application/json'},
            method='POST'
        )
        try:
            with urllib.request.urlopen(req, timeout=10) as resp:
                logger.info(f"[{self.device_id}] Sent (HTTP {resp.status}): "
                           f"engine={'ON' if payload['engineRunning'] else 'OFF'}, "
                           f"hours={payload['engineHours']}, fuel={payload['fuelLevel']}%")
        except urllib.error.HTTPError as e:
            logger.warning(f"[{self.device_id}] HTTP {e.code}: {e.reason}")
        except urllib.error.URLError as e:
            logger.error(f"[{self.device_id}] Connection failed: {e.reason}")

    def run(self):
        logger.info(f"[{self.device_id}] Starting LOCAL telemetry loop (interval: {self.interval}s)")
        while not shutdown_event.is_set():
            try:
                payload = self.generator.generate()
                self.publish(payload)
            except Exception as e:
                logger.error(f"[{self.device_id}] Error: {e}")
            shutdown_event.wait(timeout=self.interval)
        logger.info(f"[{self.device_id}] Stopped")

    def disconnect(self):
        pass


class MqttSimulator:
    """Sends telemetry via MQTT to AWS IoT Core (requires X.509 certs)."""

    def __init__(self, device_config: dict, iot_endpoint: str):
        self.device_id = device_config['device_id']
        self.machine_id = device_config['machine_id']
        self.cert_path = device_config['cert_path']
        self.key_path = device_config['key_path']
        self.root_ca_path = device_config['root_ca_path']
        self.interval = device_config.get('interval_seconds', 30)
        self.iot_endpoint = iot_endpoint
        self.topic = f"costco/machines/{self.machine_id}/telemetry"
        self.connection = None
        self.generator = TelemetryGenerator(self.device_id, self.machine_id, self.interval)

    def connect(self):
        if USE_AWS_IOT_SDK:
            self.connection = mqtt_connection_builder.mtls_from_path(
                endpoint=self.iot_endpoint,
                cert_filepath=self.cert_path,
                pri_key_filepath=self.key_path,
                ca_filepath=self.root_ca_path,
                client_id=self.device_id,
                clean_session=False,
                keep_alive_secs=30
            )
            connect_future = self.connection.connect()
            connect_future.result(timeout=10)
            logger.info(f"[{self.device_id}] Connected to IoT Core (AWS SDK)")
        else:
            self.connection = paho_mqtt.Client(client_id=self.device_id)
            self.connection.tls_set(
                ca_certs=self.root_ca_path,
                certfile=self.cert_path,
                keyfile=self.key_path
            )
            self.connection.connect(self.iot_endpoint, 8883, keepalive=30)
            self.connection.loop_start()
            logger.info(f"[{self.device_id}] Connected to IoT Core (paho-mqtt)")

    def publish(self, payload: dict):
        message = json.dumps(payload)
        if USE_AWS_IOT_SDK:
            self.connection.publish(
                topic=self.topic,
                payload=message,
                qos=mqtt.QoS.AT_LEAST_ONCE
            )
        else:
            self.connection.publish(self.topic, message, qos=1)
        logger.info(f"[{self.device_id}] Published: engine={'ON' if payload['engineRunning'] else 'OFF'}, "
                     f"hours={payload['engineHours']}, fuel={payload['fuelLevel']}%")

    def run(self):
        self.connect()
        logger.info(f"[{self.device_id}] Starting MQTT telemetry loop (interval: {self.interval}s)")
        while not shutdown_event.is_set():
            try:
                payload = self.generator.generate()
                self.publish(payload)
            except Exception as e:
                logger.error(f"[{self.device_id}] Error: {e}")
            shutdown_event.wait(timeout=self.interval)
        logger.info(f"[{self.device_id}] Stopped")

    def disconnect(self):
        if self.connection:
            if USE_AWS_IOT_SDK:
                self.connection.disconnect().result(timeout=5)
            else:
                self.connection.loop_stop()
                self.connection.disconnect()


def load_config(config_path: str) -> dict:
    path = Path(config_path)
    if not path.exists():
        logger.error(f"Config file not found: {config_path}")
        sys.exit(1)
    with open(path, 'r') as f:
        return yaml.safe_load(f)


def main():
    parser = argparse.ArgumentParser(description='Costco Mining Device Simulator')
    parser.add_argument('--config', default='config.yaml', help='Path to config file')
    parser.add_argument('--device', help='Simulate only this device ID')
    parser.add_argument('--local', action='store_true',
                       help='Local mode: send telemetry via HTTP to Spring Boot API (no certs needed)')
    parser.add_argument('--api-url', default='http://localhost:8080',
                       help='Spring Boot API URL for local mode (default: http://localhost:8080)')
    args = parser.parse_args()

    config = load_config(args.config)
    devices_config = config['devices']

    if args.device:
        devices_config = [d for d in devices_config if d['device_id'] == args.device]
        if not devices_config:
            logger.error(f"Device {args.device} not found in config")
            sys.exit(1)

    mode = "LOCAL (HTTP)" if args.local else "MQTT (IoT Core)"
    logger.info(f"Starting simulator for {len(devices_config)} device(s) in {mode} mode")

    threads = []
    simulators = []

    for device_cfg in devices_config:
        if args.local:
            sim = LocalSimulator(device_cfg, args.api_url)
        else:
            sim = MqttSimulator(device_cfg, config['iot_endpoint'])
        simulators.append(sim)
        t = threading.Thread(target=sim.run, name=f"device-{device_cfg['device_id']}", daemon=True)
        threads.append(t)
        t.start()

    try:
        while not shutdown_event.is_set():
            shutdown_event.wait(timeout=1)
    except KeyboardInterrupt:
        shutdown_event.set()

    logger.info("Shutting down all simulators...")
    for sim in simulators:
        sim.disconnect()

    for t in threads:
        t.join(timeout=5)

    logger.info("All simulators stopped. Goodbye!")


if __name__ == '__main__':
    main()
