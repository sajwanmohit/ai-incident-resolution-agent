# AI Incident Resolution Agent — Day 1–7 Kafka Integration

Java 21 + Spring Boot + Spring Kafka Maven project representing the Kafka integration state discussed through Day 7.

## What is included

- Spring Boot application bootstrap
- Kafka producer configuration
- Kafka consumer configuration
- `KafkaTemplate<String, String>` producer
- `@KafkaListener` consumer
- `incident-events` topic with 3 partitions / replication factor 1
- REST endpoint to publish an incident event
- Bean validation on the request model
- Actuator health/info endpoints
- Unit tests for producer and consumer
- MockMvc controller test
- Spring context test
- Local Kafka KRaft Docker Compose setup

## Project structure

```text
src/main/java/com/mohit/aiagent
├── config/KafkaConfig.java
├── controller/IncidentController.java
├── kafka/KafkaIncidentConsumer.java
├── model/IncidentEvent.java
├── service/KafkaProducerService.java
└── AiIncidentResolutionAgentApplication.java
```

## Kafka flow

```text
HTTP POST /api/v1/incidents
        |
        v
IncidentController
        |
        v
KafkaProducerService
        |
        v
KafkaTemplate<String,String>
        |
        v
Kafka topic: incident-events
        |
        v
KafkaIncidentConsumer (@KafkaListener)
```

## Run locally

### 1. Start Kafka

```bash
docker compose up -d
```

### 2. Start the application

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
mvnw.cmd spring-boot:run
```

### 3. Publish an incident

```bash
curl -X POST http://localhost:8080/api/v1/incidents \
  -H "Content-Type: application/json" \
  -d '{"incidentId":"INC-1001","service":"payments","severity":"HIGH","message":"Payment API timeout"}'
```

Expected HTTP status: `202 Accepted`.

The application log should contain a message similar to:

```text
Consumed incident event: incidentId=INC-1001,service=payments,severity=HIGH,message=Payment API timeout
```

## Configuration

Defaults can be overridden with environment variables:

- `KAFKA_BOOTSTRAP_SERVERS` — default `localhost:9092`
- `KAFKA_CONSUMER_GROUP` — default `incident-resolution-group`
- `KAFKA_TOPIC` — default `incident-events`
- `KAFKA_PARTITIONS` — default `3`
- `KAFKA_REPLICATION_FACTOR` — default `1`
- `SERVER_PORT` — default `8080`
- `OPENAI_API_KEY` — enables live OpenAI incident analysis; unset uses the local rule-based analyzer
- `OPENAI_MODEL` — OpenAI chat model; default `gpt-4o-mini`
- `OPENAI_BASE_URL` — OpenAI-compatible API base URL; default `https://api.openai.com/v1`

### Enable live LLM analysis on Windows

Set the API key in the same PowerShell window used to start the app, then run it:

```powershell
$env:OPENAI_API_KEY = "your-api-key"
mvnw.cmd spring-boot:run
```

Incidents published to Kafka are sent synchronously to the configured chat model by `OpenAiIncidentAnalyzer`. If the key is unset, the local analyzer is selected. If a live call fails or returns invalid analysis JSON, the application logs the failure and uses the local analyzer for that incident.

## Tests

Run:

```bash
./mvnw test
```

The test suite covers producer interaction, controller HTTP behavior, consumer invocation, and Spring context loading.
