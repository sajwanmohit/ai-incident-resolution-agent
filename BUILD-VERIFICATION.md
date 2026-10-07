# Build verification

The project was source/configuration checked in the packaging environment.

- Java source files and package structure verified.
- Kafka dependency, `KafkaTemplate`, `@KafkaListener`, and `NewTopic` configuration verified.
- Test source files are included.
- Full Maven compilation/test execution was not performed in the packaging environment because Maven is not installed there.

Run locally with Java 21 and Maven:

```bash
mvn test
```
