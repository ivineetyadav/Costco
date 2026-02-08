# Device Certificates

Place your AWS IoT Core device certificates here:

For each device (e.g., DEV-001):
- `DEV-001-cert.pem` - Device certificate
- `DEV-001-private.key` - Private key
- `AmazonRootCA1.pem` - Amazon Root CA (shared across devices)

## How to get certificates

1. Go to AWS Console -> IoT Core -> Manage -> Things
2. Create a new Thing (e.g., DEV-001)
3. Auto-generate certificate
4. Download all files and place them here
5. Attach the `costco-device-policy` to the certificate

## Download Amazon Root CA

```bash
curl -o AmazonRootCA1.pem https://www.amazontrust.com/repository/AmazonRootCA1.pem
```

**IMPORTANT**: Never commit certificates to git. This directory is gitignored.
