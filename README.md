# Weather App

Weather App is a Spring Boot application that fetches and manages weather data from the Open Meteo API. It uses MongoDB for data storage and Kafka for messaging. The application provides a RESTful API for accessing and manipulating temperature data.

This application has been developed as part of a recruitment process.

## Features

- Fetch current temperature data for given coordinates.
- Cache temperature data to reduce API calls.
- Delete temperature data for specific coordinates.
- Publish temperature data to a Kafka topic.
- Swagger UI for API documentation and testing.

## Technologies Used

- Java 21
- Spring Boot 3.4.1
- MongoDB
- Kafka
- Lombok
- Maven
- Swagger (Springdoc OpenAPI)

## Prerequisites

- Java 21
- Docker
- Maven

## Getting Started

### Clone the repository

```sh
git clone https://github.com/dandev237/weatherapp.git
cd weatherapp
```

### Build the project

```sh
mvn clean install
```

### Run the application with Docker

```sh
docker-compose up --build
```

### Access the application

- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Configuration

The application can be configured using the `src/main/resources/application.properties` file:

```ini
spring.application.name=Weather App
spring.data.mongodb.database=weatherapp
spring.data.mongodb.uri=${SPRING_DATA_MONGODB_URI}
server.port=8080
open-meteo.api.base-url=https://api.open-meteo.com/v1/forecast
```

## API Endpoints

### Get Temperature Data

- **URL:** `/temperature`
- **Method:** `GET`
- **Parameters:**
    - `latitude` (required): Latitude of the location.
    - `longitude` (required): Longitude of the location.
- **Response:** `TemperatureResponse` object containing latitude, longitude, and temperature.

### Delete Temperature Data

- **URL:** `/temperature`
- **Method:** `DELETE`
- **Parameters:**
    - `latitude` (required): Latitude of the location.
    - `longitude` (required): Longitude of the location.
- **Response:** `boolean` indicating success or failure.

## Running Tests

To run the tests, use the following command:

```sh
mvn test
```

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.
