# Recommendation Service

A Spring Boot application for managing and analyzing cryptocurrency price data, providing RESTful endpoints for statistics, normalized range calculations, and more. The service supports CSV-based data import, IP-based rate limiting, and is fully documented with OpenAPI/Swagger.

---

## Features

- **REST API for Cryptocurrency Statistics**
    - Get normalized range for all supported cryptocurrencies, sorted descending
    - Retrieve oldest, newest, minimum, and maximum price for a specific symbol
    - Find the cryptocurrency with the highest normalized range for a specific day

- **CSV Data Import**
    - On startup, loads price data from CSV files in `resources/csv/`
    - Validates symbols and logs unsupported entries

- **OpenAPI/Swagger Documentation**
    - Interactive API documentation available at `/swagger-ui/index.html`
    - OpenAPI spec available at `/v3/api-docs`

- **IP-based Rate Limiting**
    - Limits each IP to 60 requests per minute (configurable in `RateLimitFilter`)

- **Custom Exception Handling**
    - Global error handler for invalid date formats, unsupported symbols, and missing data

- **Unit and Integration Tests**
    - Comprehensive test coverage with JaCoCo

---

## API Examples

### Get All Cryptos by Normalized Range

```http
GET /cryptos/normalized-range
```

### Response
```json
[
  { "symbol": "BTC", "normalizedRange": 0.15 },
  { "symbol": "ETH", "normalizedRange": 0.12 }
]
```

### Get Statistics for a Symbol

```http
GET /cryptos/BTC/stats
```

### Response
```json
{
  "symbol": "BTC",
  "oldest": 11000.0,
  "newest": 5000.0,
  "min": 2000.0,
  "max": 85000.0
}
```

### Get Highest Normalized Range for a Day

```http
GET /cryptos/normalized-range/highest?date=2022-01-01
```

### Response
```json
{
  "symbol": "BTC",
  "normalizedRange": 0.15
}
```

## Supported Cryptocurrencies
- BTC (Bitcoin)
- DOGE (Dogecoin)
- ETH (Ethereum)
- LTC (Litecoin)
- XRP (Ripple)

## Building and Running
### Prerequisites
- Java 21+
- Maven 3.6+
- (Optional) Docker

### Build
```
mvn clean install
```

### Run
```
java -jar target/recommendation-service-0.0.1-SNAPSHOT.jar
```

### Run with Docker
```
docker build -t recommendation-service .
docker run -p 8080:8080 recommendation-service
```

### API Documentation
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Architecture Overview

- Controller Layer: Exposes REST endpoints for statistics and normalized range queries.
- Service Layer: Contains business logic for calculations, validation, and data aggregation.
- Repository Layer: JPA repository for accessing and querying price data.
- Entity Layer: JPA entity representing price records.
- DTOs: Data Transfer Objects for API responses.
- Validation: Enum-based symbol validation and custom validator component.
- Exception Handling: Global exception handler for consistent error responses.
- Rate Limiting: Servlet filter using Bucket4j for per-IP request limiting.
- CSV Loader: Loads and validates price data from CSV files at startup.

## Potential Enhancements
- Authentication & Authorization: Integrate OAuth2/JWT for securing endpoints.
- Admin Endpoints: Add endpoints for uploading new CSV files or managing supported symbols.
- Monitoring & Metrics: Integrate with Prometheus/Grafana for real-time monitoring.
- Deployment: Add Kubernetes manifests or Helm charts for cloud-native deployment.

## Shortcuts
- Currently the service is filtering through all the dates which is loaded from the CSV. It is not configurable whether the client wants to get 1 month or 6 months of data.