#!/bin/bash
echo "Initializing LocalStack resources..."

# Create SQS queues
awslocal sqs create-queue --queue-name costco-telemetry-dlq
awslocal sqs create-queue --queue-name costco-telemetry-queue \
  --attributes '{
    "RedrivePolicy": "{\"deadLetterTargetArn\":\"arn:aws:sqs:ap-south-1:000000000000:costco-telemetry-dlq\",\"maxReceiveCount\":\"3\"}"
  }'

# Create S3 buckets
awslocal s3 mb s3://costco-saas-reports
awslocal s3 mb s3://costco-saas-frontend

echo "LocalStack initialization complete!"
