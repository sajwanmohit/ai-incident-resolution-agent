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

## Tests

Run:

```bash
./mvnw test
```

The test suite covers producer interaction, controller HTTP behavior, consumer invocation, and Spring context loading.
