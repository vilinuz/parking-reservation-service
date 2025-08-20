```markdown
# Parking Reservation Service

A reactive Java (Spring Boot) backend service for reserving parking lot spaces with validation, batch processing, and availability checks.

## Features

- Create single or batch parking reservations
- Query available parking lots for a given time range
- Validate reservations for lot ID, vehicle plate, and date/time
- RESTful API endpoints using Spring WebFlux
- Reactive, non-blocking data access with R2DBC
- Caching for reservation state and availability
- Postgres support via Docker Compose

## API Endpoints

- `POST /reservations` — Create a single reservation
- `POST /reservations/batch` — Create multiple reservations in bulk
- `GET /reservations` — Query reservations by status
- `GET /parking/reservations/lots/available` — Get available lot numbers (by time range)

## Reservation Example

A reservation requires:
- `lotId`: e.g., "A12" (A-D + 1-100)
- `vehiclePlate`: e.g., "AB1234CD" (two letters, four digits, two letters)
- `startTime` and `endTime`: ISO date-times

## Technologies

- Java 17+
- Spring Boot (WebFlux, R2DBC)
- PostgreSQL
- Docker Compose (for local dev)
- Reactive programming (Project Reactor)
- Lombok

## Running the Application

**The DEV profile must be set when running the application.**

### On the Command Line

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```
or
```bash
java -jar -Dspring.profiles.active=dev target/parking-reservation-service.jar
```

### In IntelliJ IDEA

1. Open the Run/Debug Configurations (top right dropdown > "Edit Configurations...").
2. Under "VM options", add:
   ```
   -Dspring.profiles.active=dev
   ```
3. Or, use "Modify options" > "Add VM options" and set the profile as above.

## Getting Started

1. Clone the repo
2. Start Postgres with `docker compose up`
3. Build & run with the DEV profile (see above)

See [`HELP.md`](HELP.md) for more guides and documentation links.

## License

[Specify your license here if needed]
```
