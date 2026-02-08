# Costco Mining - Device Simulator

Simulates network devices sending telemetry data to AWS IoT Core via MQTT.

## Setup

1. Install dependencies:
```bash
pip install -r requirements.txt
```

2. Place device certificates in `certs/` directory (see `certs/README.md`)

3. Update `config.yaml` with your IoT Core endpoint:
   - Find it in AWS Console -> IoT Core -> Settings -> Device data endpoint

## Usage

```bash
# Simulate all devices from config
python device_simulator.py

# Use custom config file
python device_simulator.py --config my-config.yaml

# Simulate a single device
python device_simulator.py --device DEV-001
```

## Telemetry Format

Each message published to `costco/machines/{machine_id}/telemetry`:

```json
{
  "deviceId": "DEV-001",
  "machineId": "MACH-001",
  "timestamp": "2024-03-15T10:30:00Z",
  "engineRunning": true,
  "engineHours": 1234.56,
  "fuelLevel": 75.5,
  "locationLat": -26.2041,
  "locationLng": 28.0473,
  "voltage": 230.5,
  "temperature": 78.3,
  "vibration": 2.1
}
```

## Stop

Press `Ctrl+C` to gracefully stop all simulators.
