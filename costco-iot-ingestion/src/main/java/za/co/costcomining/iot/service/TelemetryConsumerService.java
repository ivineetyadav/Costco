package za.co.costcomining.iot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelemetryConsumerService {

    private final SqsClient sqsClient;
    private final DeviceMessageParser messageParser;
    private final DeviceValidationService validationService;
    private final DeviceQueryService deviceQueryService;

    @Value("${aws.sqs.queue-url:}")
    private String queueUrl;

    @Scheduled(fixedDelayString = "${aws.sqs.poll-interval-ms:5000}")
    public void pollMessages() {
        if (queueUrl == null || queueUrl.isBlank()) {
            return;
        }

        try {
            ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(5)
                    .build();

            List<Message> messages = sqsClient.receiveMessage(request).messages();

            for (Message message : messages) {
                processMessage(message);
            }
        } catch (Exception e) {
            log.error("Error polling SQS queue", e);
        }
    }

    private void processMessage(Message message) {
        try {
            DeviceMessageParser.TelemetryMessage telemetryMsg = messageParser.parse(message.body());

            // 3-layer validation (Layer 3 - business level)
            DeviceValidationService.ValidationResult result =
                    validationService.validate(telemetryMsg.getDeviceId(), telemetryMsg.getMachineId());

            if (!result.isAccepted()) {
                log.warn("Rejected telemetry message: {}", result.getRejectionReason());
                deleteMessage(message);
                return;
            }

            if (result.isUnbilledUsage()) {
                log.warn("Unbilled usage detected for device: {}", telemetryMsg.getDeviceId());
            }

            // Persist telemetry
            deviceQueryService.saveTelemetry(telemetryMsg.getTelemetry());

            // Update device last seen
            deviceQueryService.updateLastSeen(result.getDevice().getId());

            log.info("Processed telemetry for device: {}, machine: {}",
                    telemetryMsg.getDeviceId(), telemetryMsg.getMachineId());

            deleteMessage(message);
        } catch (Exception e) {
            log.error("Failed to process message: {}", message.messageId(), e);
        }
    }

    private void deleteMessage(Message message) {
        try {
            sqsClient.deleteMessage(DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(message.receiptHandle())
                    .build());
        } catch (Exception e) {
            log.error("Failed to delete SQS message: {}", message.messageId(), e);
        }
    }
}
