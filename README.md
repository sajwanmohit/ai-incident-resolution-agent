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
- `OPENAI_API_KEY` — optional bearer token for the OpenAI-compatible endpoint; defaults to `docker-model-runner` (no real OpenAI key is needed)
- `OPENAI_MODEL` — chat model; default `docker.io/ai/qwen3:latest`
- `OPENAI_BASE_URL` — OpenAI-compatible API base URL; default `http://localhost:12434/engines/v1`
- `OPENAI_TEMPERATURE` — sampling temperature; default `0.2`

### Use Docker Model Runner for incident analysis

Docker Model Runner must be running locally with `docker.io/ai/qwen3:latest` available. The application uses Spring AI's OpenAI-compatible integration and sends chat completions to `/engines/v1/chat/completions`. Start the application with:

```bash
mvn spring-boot:run
```

The existing `POST /api/v1/incidents` endpoint publishes the incident to Kafka and synchronously sends it to Qwen3 for analysis. If the LLM call fails or returns invalid analysis JSON, the application logs the failure and uses its local rule-based analyzer for that incident. The endpoint returns HTTP `200` with the analysis result.

## Tests

Run:

```bash
./mvnw test
```

The test suite covers producer interaction, controller HTTP behavior, consumer invocation, and Spring context loading.

## Local boot-up

### 1. Start Docker Model Runner (local LLM)

Install and start Docker Model Runner, then ensure the chat model is available. The app defaults to the same model used in this project:

```bash
OPENAI_API_KEY=docker-model-runner
OPENAI_BASE_URL=http://localhost:12434/engines/v1
OPENAI_MODEL=docker.io/ai/qwen3:latest
```

The app will call:

```text
http://localhost:12434/engines/v1/chat/completions
```

### 2. Start Kafka

```bash
docker compose up -d
```

### 3. Start the Spring Boot app

```bash
export OPENAI_API_KEY=docker-model-runner
export OPENAI_BASE_URL=http://localhost:12434/engines/v1
export OPENAI_MODEL=docker.io/ai/qwen3:latest
export OPENAI_TEMPERATURE=0.2
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092
export KAFKA_CONSUMER_GROUP=incident-resolution-group
export PORT=8080

./mvnw spring-boot:run
```

On Windows:

```powershell
$env:OPENAI_API_KEY="docker-model-runner"
$env:OPENAI_BASE_URL="http://localhost:12434/engines/v1"
$env:OPENAI_MODEL="docker.io/ai/qwen3:latest"
$env:OPENAI_TEMPERATURE="0.2"
$env:KAFKA_BOOTSTRAP_SERVERS="localhost:9092"
$env:KAFKA_CONSUMER_GROUP="incident-resolution-group"
$env:PORT="8080"

mvnw.cmd spring-boot:run
```

### 4. Publish an incident

```bash
curl -X POST http://localhost:8080/api/v1/incidents \
  -H "Content-Type: application/json" \
  -d '{"incidentId":"INC-1001","service":"payments","severity":"HIGH","message":"Payment API timeout"}'
```

Expected HTTP status: `202 Accepted`.

### 5. Verify the model call

The app logs a message when it invokes the LLM analysis flow. If the local Docker Model Runner is not available, the request will fail because the app is configured to use the local LLM directly.

Important: `localhost` only works for local development. Render cannot reach your laptop's `localhost`.

## Render deployment boot-up

For Render, set the environment variables explicitly in the Render dashboard and do not use `localhost`.

### Required Render environment variables

```text
OPENAI_API_KEY=<your-openai-compatible-key-or-token>
OPENAI_BASE_URL=<public-url-to-your-llm-endpoint>
OPENAI_MODEL=<model-name>
OPENAI_TEMPERATURE=0.2
KAFKA_BOOTSTRAP_SERVERS=<your-render-kafka-host>
KAFKA_CONSUMER_GROUP=incident-resolution-group
KAFKA_TOPIC=incident-events
KAFKA_PARTITIONS=3
KAFKA_REPLICATION_FACTOR=1
PORT=10000
```

Examples:

```text
OPENAI_API_KEY=sk-xxxxxxxx
OPENAI_BASE_URL=https://api.openai.com/v1
OPENAI_MODEL=gpt-4o-mini
```

or, if you are exposing a local LLM through Cloudflare Tunnel:

```text
OPENAI_BASE_URL=https://mercury-stock-hoped-cowboy.trycloudflare.com/engines/v1
OPENAI_API_KEY=docker-model-runner
OPENAI_MODEL=docker.io/ai/qwen3:latest
```

Notes:

- Render cannot reach your laptop's `localhost` or `127.0.0.1`.
- If your LLM is local, it must be exposed through a reachable public URL such as a Cloudflare tunnel or a hosted inference endpoint.
- The `PORT` value for Render is usually set to `10000` by the platform, but you can also define it explicitly in the service config.

### Render start command

If you are deploying the Spring Boot app directly, use:

```bash
./mvnw spring-boot:run
```

or, for a packaged jar:

```bash
java -jar target/ai-incident-resolution-agent-0.0.1-SNAPSHOT.jar
```

### Deployment checklist

1. Set the environment variables in Render.
2. Ensure the LLM endpoint is reachable from the Render container.
3. Ensure Kafka is reachable from Render.
4. Deploy the project.
5. Test with the same incident POST call, replacing `localhost` with the Render public URL.

## Configuration

Defaults can be overridden with environment variables:

- `KAFKA_BOOTSTRAP_SERVERS` — default `localhost:9092`
- `KAFKA_CONSUMER_GROUP` — default `incident-resolution-group`
- `KAFKA_TOPIC` — default `incident-events`
- `KAFKA_PARTITIONS` — default `3`
- `KAFKA_REPLICATION_FACTOR` — default `1`
- `SERVER_PORT` — default `8080`
- `OPENAI_API_KEY` — optional bearer token for the OpenAI-compatible endpoint; defaults to `docker-model-runner` (no real OpenAI key is needed)
- `OPENAI_MODEL` — chat model; default `docker.io/ai/qwen3:latest`
- `OPENAI_BASE_URL` — OpenAI-compatible API base URL; default `http://localhost:12434/engines/v1`
- `OPENAI_TEMPERATURE` — sampling temperature; default `0.2`

### Use Docker Model Runner for incident analysis

Docker Model Runner must be running locally with `docker.io/ai/qwen3:latest` available. The application uses Spring AI's OpenAI-compatible integration and sends chat completions to `/engines/v1/chat/completions`. Start the application with:

```bash
mvn spring-boot:run
```

The existing `POST /api/v1/incidents` endpoint publishes the incident to Kafka and synchronously sends it to Qwen3 for analysis. If the LLM call fails or returns invalid analysis JSON, the application logs the failure and uses its local rule-based analyzer for that incident. The endpoint returns HTTP `200` with the analysis result.
