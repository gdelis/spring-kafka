# Spring Kafka Application

A modern Spring Boot application demonstrating integration with Apache Kafka, including producers, consumers, and stream processing capabilities using Kafka Streams. The project uses Avro for schema-based serialization and deserialization of messages.

## Features

- Kafka producers and consumers implementation
- Kafka Streams for stream processing
- Avro schema-based serialization
- RESTful API endpoints for interacting with Kafka
- Comprehensive testing with TestContainers
- Spring Boot Actuator for monitoring and metrics

## Prerequisites

- Java 24 or later
- Docker and Docker Compose (for Kafka and Schema Registry)
- Gradle 8.5+ (or use the included Gradle wrapper)
- IDE with Spring Boot support (IntelliJ IDEA, Eclipse, VS Code, etc.)

## Technologies Used

- Java 24
- Spring Boot 3.5.0
- Spring Kafka
- Apache Kafka
- Kafka Streams
- Apache Avro
- Confluent Schema Registry
- Lombok
- TestContainers
- JUnit 5

## Project Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/spring-kafka.git
   cd spring-kafka
   ```

2. Start the required Docker containers (Kafka, Zookeeper, Schema Registry):
   ```bash
   cd docker
   docker-compose up -d
   ```

3. Build the project:
   ```bash
   ./gradlew build
   ```

## Local Development Guide

This section provides detailed instructions for setting up and working with the local development environment.

### Prerequisites for Local Development

- Docker and Docker Compose installed
- At least 4GB of RAM allocated to Docker
- Port availability: 2181, 8080, 8081, 8083, 9092, 5432 (ensure these ports are not in use)
- Sufficient disk space (~1GB) for Docker images and volumes

### Setting Up the Local Environment

The project includes a complete development environment using Docker Compose with the following services:

1. Start all services:
   ```bash
   cd docker
   docker-compose up -d
   ```

2. Verify all services are running:
   ```bash
   docker-compose ps
   ```

3. To stop the environment:
   ```bash
   docker-compose down
   ```

4. To reset the environment (removes all data):
   ```bash
   docker-compose down -v
   docker-compose up -d
   ```

### Included Services

#### Kafka and Zookeeper
- **Kafka**:
  - Internal URL: `broker:29092` (for services within Docker network)
  - External URL: `localhost:9092` (for connecting from your host)
  - Partitions: 3
  - Replication factor: 1

- **Zookeeper**:
  - URL: `localhost:2181`

#### Schema Registry
- URL: `http://localhost:8081`
- Used for storing and retrieving Avro schemas
- REST API available for schema management

#### Kafka Connect
- URL: `http://localhost:8083`
- Configured with connection to PostgreSQL
- Custom connectors can be added to `./docker/kafka-connect-plugins` directory

#### PostgreSQL Database
- Host: `localhost`
- Port: `5432`
- Database: `users`
- Username: `username`
- Password: `password`

#### Kafka UI
- URL: `http://localhost:8080`
- Features:
  - Topic management (create, delete, view messages)
  - Consumer group monitoring
  - Schema Registry integration
  - Kafka Connect management

### Using Kafka UI

Kafka UI provides a comprehensive interface for managing your Kafka environment:

1. **Browsing Topics**: Navigate to "Topics" to see all available topics
2. **Creating Topics**: Click "Add a Topic" button to create new topics
3. **Viewing Messages**: Select a topic and click "Messages" to view data
4. **Managing Schemas**: Go to "Schema Registry" section to manage Avro schemas
5. **Monitoring**: View broker metrics and consumer lag in the "Monitoring" section

### Connecting to the Services

#### From Spring Boot Application
Configure your application to connect to these services by setting the following properties:

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    properties:
      schema.registry.url: http://localhost:8081
  datasource:
    url: jdbc:postgresql://localhost:5432/users
    username: username
    password: password
```

#### From Command Line

- **Kafka CLI Tools**:
  ```bash
  # List topics
  docker exec kafka kafka-topics --bootstrap-server broker:29092 --list
  
  # Create a topic
  docker exec kafka kafka-topics --bootstrap-server broker:29092 --create --topic my-topic --partitions 3 --replication-factor 1
  
  # Produce messages
  docker exec -it kafka kafka-console-producer --bootstrap-server broker:29092 --topic my-topic
  
  # Consume messages
  docker exec -it kafka kafka-console-consumer --bootstrap-server broker:29092 --topic my-topic --from-beginning
  ```

- **Schema Registry**:
  ```bash
  # List all subjects
  curl -X GET http://localhost:8081/subjects
  
  # Get a specific schema
  curl -X GET http://localhost:8081/subjects/my-topic-value/versions/latest
  ```

### Common Development Tasks

#### Creating and Testing a Producer/Consumer

1. Create Avro schema in `src/main/avro/` directory
2. Generate Java classes with Gradle: `./gradlew generateAvroJava`
3. Implement producer/consumer using Spring Kafka
4. Verify messages using Kafka UI

#### Troubleshooting

- **Connection Issues**:
  - Ensure Docker containers are running: `docker ps`
  - Check logs: `docker-compose logs -f [service-name]`
  - Verify port availability: `netstat -an | grep [port]`

- **Schema Registry Issues**:
  - Check compatibility: `curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" --data '{"schema": "{\"type\": \"string\"}"}' http://localhost:8081/compatibility/subjects/my-topic-value/versions/latest`
  - Restart Schema Registry: `docker-compose restart schema-registry`

- **Kafka Connect Issues**:
  - View connectors: `curl -X GET http://localhost:8083/connectors`
  - Check connector status: `curl -X GET http://localhost:8083/connectors/[connector-name]/status`

### Data Persistence

The Docker setup includes persistent volumes for:
- Kafka data
- Zookeeper data
- PostgreSQL data

Data is preserved across container restarts but will be lost when volumes are removed with `docker-compose down -v`.

## Running the Application

Start the application using Gradle:
```bash
./gradlew bootRun
```

The application will be available at `http://localhost:9090`.

### Application Configuration

The following environment variables and properties can be configured:

- `SPRING_KAFKA_BOOTSTRAP_SERVERS`: Kafka bootstrap servers (default: `localhost:9092`)
- `SPRING_KAFKA_SCHEMA_REGISTRY_URL`: Schema Registry URL (default: `http://localhost:8081`)

#### Kafka Topics Configuration

The application uses the following Kafka topics:

- `users-source`: Source topic for user details (3 partitions)
- `users-sink`: Sink topic for processed user details
- Consumer group ID: `consumer-user-group`
- Polling interval: 100ms

#### Services Configuration

The application integrates with the following services:

- Products Service: `http://localhost:9090`
- Orders Service: `http://localhost:9091`

### Environment Variables

The following environment variables can be configured:

- `SPRING_KAFKA_BOOTSTRAP_SERVERS`: Kafka bootstrap servers (default: `localhost:9092`)
- `SPRING_KAFKA_SCHEMA_REGISTRY_URL`: Schema Registry URL (default: `http://localhost:8081`)

## Testing

The project includes comprehensive tests using JUnit 5 and TestContainers for integration testing with Kafka.

Run tests with:
```bash
./gradlew test
```

### Integration Tests

Integration tests use TestContainers to spin up necessary infrastructure like Kafka brokers during test execution, ensuring tests run in an isolated environment that closely resembles production.

## Application Features

This section describes the key features and components of the application based on the codebase.

### User Management

- **User Details Management**: Create, read, update, and delete user records with persistence through Kafka
- **User Types**: Support for different user types through the `UserTypeEnum`
- **Country Support**: International user management with `CountryEnum` and custom converters
- **Validation**: Phone number validation with custom validators

### Product Management

- REST API for product operations
- Integration with product service
- Product model with complete CRUD operations

### Order Processing

- Order model for representing customer orders
- REST API for order operations
- Integration with external order service

### Kafka Features

- **Kafka Producers**: Configured producers for sending messages to Kafka topics
- **Kafka Consumers**: Configured consumers for receiving and processing messages
- **Kafka Streams**: Stream processing for data transformation and enrichment
- **Kafka Connect**: Integration with external systems
- **Avro Schema**: Schema-based serialization and deserialization of messages
- **Custom Interceptors**:
  - Author header interceptor for tracking message authors
  - Date header interceptor for timestamping messages
  - Headers consumer interceptor for processing custom headers

### Advanced Features

- **Error Handling**: Custom exception handling for client and server errors
- **HTTP Services**: Integration with external HTTP services
- **Repository Pattern**: Clean separation of data access logic
- **Service Layer**: Business logic encapsulation

## API Documentation

API documentation is available when the application is running at:
```
http://localhost:9090/swagger-ui.html
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

