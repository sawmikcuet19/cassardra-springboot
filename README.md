# Apache Cassandra + Spring Boot

A comprehensive learning project demonstrating **Apache Cassandra** integration with **Spring Boot**, covering CRUD operations, user-defined types, collections, TTL, batch writes, lightweight transactions, SASI indexes, materialized views, JWT authentication, counter tables, and advanced CQL queries.

---

## Tech Stack

| Component | Version |
|-----------|---------|
| Java | 25 |
| Spring Boot | 4.1.1 |
| Apache Cassandra | 5.0 |
| Spring Data Cassandra | (via starter) |
| Spring Security + JWT | (jjwt 0.13.0) |
| Lombok | 1.18.46 |
| Docker Compose | (for Cassandra) |

---

## Project Structure

```
cassandra/
├── docker-compose.yml              # Cassandra 5.0 container
├── pom.xml                         # Maven dependencies
├── src/main/java/com/sawmik/cassandra/
│   ├── CassandraApplication.java   # Entry point
│   ├── config/
│   │   ├── AppConfig.java          # CORS + JWT properties binding
│   │   ├── CassandraConfig.java    # Two-phase CqlSession init
│   │   ├── SecurityConfig.java     # Spring Security filter chain
│   │   ├── GlobalExceptionHandler.java  # Centralized error handling
│   │   └── ViewInitializer.java    # Materialized view creation on startup
│   ├── entity/
│   │   ├── User.java
│   │   ├── Product.java
│   │   ├── Article.java
│   │   ├── LogEntry.java
│   │   ├── Employee.java
│   │   ├── Event.java
│   │   ├── Location.java
│   │   ├── MetricsCounter.java
│   │   ├── SensorReading.java
│   │   └── SensorReadingKey.java   # Composite primary key
│   ├── udt/
│   │   ├── Review.java             # User Defined Type
│   │   ├── Address.java
│   │   └── Coordinate.java
│   ├── dto/
│   │   ├── auth/                   # RegisterRequest, LoginRequest, AuthResponse
│   │   ├── product/                # ProductRequest
│   │   ├── location/               # LocationRequest
│   │   └── user/                   # UserResponse
│   ├── repository/                 # 9 Cassandra repositories
│   ├── service/                    # 16 service classes
│   ├── controller/                 # 17 REST controllers
│   └── security/
│       ├── JwtTokenProvider.java
│       ├── JwtAuthenticationFilter.java
│       └── CustomUserDetailsService.java
└── src/test/
    └── CassandraApplicationTests.java
```

---

## Cassandra Schema

### Keyspace

```
cassandra_app (SimpleStrategy, replication_factor = 1)
```

### Tables Created

| Table | Primary Key | Description |
|-------|-------------|-------------|
| `users` | `id TEXT` | JWT authentication users |
| `products` | `id UUID` | Products with UDT reviews |
| `articles` | `id UUID` | Articles with list/set collections |
| `log_entries` | `id UUID` | Application log entries |
| `employees` | `id UUID` | Employees with set/map collections |
| `events` | `id UUID` | Events with map/set/list collections |
| `locations` | `id UUID` | Locations with Coordinate UDT |
| `sensor_readings` | `(sensor_id UUID, reading_time TIMESTAMP)` | Time-series data (composite key) |
| `metrics_counters` | `name TEXT` | Counter columns |

### User Defined Types (UDTs)

| UDT | Fields | Used By |
|-----|--------|---------|
| `review` | `reviewer TEXT, comment TEXT, rating INT` | `Product.reviews` |
| `coordinate` | `latitude DOUBLE, longitude DOUBLE` | `Location.coordinates` |
| `address` | `street, city, state, zipCode, country` | (created, available for use) |

### Materialized Views (6)

| View | Base Table | Partition Key | Purpose |
|------|-----------|---------------|---------|
| `products_by_category` | `products` | `(category, id)` | Query products by category |
| `articles_by_author` | `articles` | `(author, id)` | Query articles by author |
| `logs_by_level` | `log_entries` | `(level, id)` | Query logs by severity level |
| `locations_by_country` | `locations` | `(country, id)` | Query locations by country |
| `employees_by_department` | `employees` | `(department, id)` | Query employees by department |
| `events_by_type` | `events` | `(event_type, id)` | Query events by type |

---

## Architecture Flow

### Application Startup Flow

```mermaid
flowchart TD
    A[Spring Boot Startup] --> B[CassandraConfig - Two-Phase Init]
    
    subgraph Phase1["Phase 1: Admin Session (no keyspace)"]
        B --> C[Connect to Cassandra without keyspace]
        C --> D[CREATE KEYSPACE IF NOT EXISTS cassandra_app]
        D --> E[CREATE TYPE IF NOT EXISTS review]
        E --> F[CREATE TYPE IF NOT EXISTS coordinate]
        F --> G[CREATE TYPE IF NOT EXISTS address]
    end
    
    subgraph Phase2["Phase 2: Application Session (with keyspace)"]
        G --> H[Connect to cassandra_app keyspace]
        H --> I[Spring Data auto-creates 9 tables]
        I --> J[Spring Data auto-creates indexes]
    end
    
    J --> K[ViewInitializer - CommandLineRunner]
    
    subgraph Views["Materialized Views Created"]
        K --> V1[products_by_category]
        K --> V2[articles_by_author]
        K --> V3[logs_by_level]
        K --> V4[locations_by_country]
        K --> V5[employees_by_department]
        K --> V6[events_by_type]
    end
    
    V1 & V2 & V3 & V4 & V5 & V6 --> L[Tomcat starts on port 8080]
    L --> M[Application READY]
```

### Authentication Flow (JWT)

```mermaid
sequenceDiagram
    participant C as Client
    participant AC as AuthController
    participant AS as AuthService
    participant UR as UserRepository
    participant PE as PasswordEncoder
    participant AM as AuthenticationManager
    participant JP as JwtTokenProvider

    Note over C,JP: Registration Flow
    C->>AC: POST /api/auth/register {username, email, password}
    AC->>AS: register(RegisterRequest)
    AS->>UR: existsByUsername(username)
    UR-->>AS: false
    AS->>UR: existsByEmail(email)
    UR-->>AS: false
    AS->>PE: encode(password)
    PE-->>AS: encodedPassword
    AS->>UR: save(User)
    AS->>AM: authenticate(UsernamePasswordAuthenticationToken)
    AM-->>AS: Authentication
    AS->>JP: generateToken(authentication)
    JP-->>AS: jwtToken
    AS-->>AC: AuthResponse(token, username, role)
    AC-->>C: 200 OK + AuthResponse

    Note over C,JP: Login Flow
    C->>AC: POST /api/auth/login {username, password}
    AC->>AS: login(LoginRequest)
    AS->>AM: authenticate(UsernamePasswordAuthenticationToken)
    AM->>AM: Load user via CustomUserDetailsService
    AM->>AM: Verify BCrypt password
    AM-->>AS: Authentication
    AS->>JP: generateToken(authentication)
    JP-->>AS: jwtToken
    AS->>UR: findByUsername(username)
    UR-->>AS: User
    AS-->>AC: AuthResponse(token, username, role)
    AC-->>C: 200 OK + AuthResponse
```

### Request Authentication Flow (JWT Filter)

```mermaid
flowchart TD
    A[Client Request + Bearer JWT] --> B[JwtAuthenticationFilter]
    B --> C{Token in Header?}
    C -->|No| D[Continue filter chain - unauthenticated]
    C -->|Yes| E[Extract token from Authorization header]
    E --> F[JwtTokenProvider.validateToken]
    F -->|Invalid| G[Return 401 Unauthorized]
    F -->|Valid| H[Extract username from token]
    H --> I[CustomUserDetailsService.loadUserByUsername]
    I --> J[Create UsernamePasswordAuthenticationToken]
    J --> K[Set SecurityContextHolder context]
    K --> L[Controller processes request with auth context]
```

### Security Rules Flow

```mermaid
flowchart TD
    A[Incoming Request] --> B{Path matches /api/auth/**?}
    B -->|Yes| C[permitAll - No auth required]
    B -->|No| D{Path matches /actuator/health?}
    D -->|Yes| E[permitAll - No auth required]
    D -->|No| F{Path matches /api/admin/**?}
    F -->|Yes| G{hasRole ADMIN?}
    F -->|No| H{Path matches GET /api/products,articles,logs,...?}
    H -->|Yes| I[authenticated - Any valid JWT]
    H -->|No| J[.anyRequest().authenticated]
    
    G -->|Admin role| K[Access granted]
    G -->|Non-admin| L[403 Forbidden]
    I -->|Valid token| K
    I -->|No/invalid token| M[401 Unauthorized]
    J -->|Valid token| K
    J -->|No/invalid token| M
```

### Product CRUD Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant PC as ProductController
    participant PS as ProductService
    participant PR as ProductRepository
    participant DB as Cassandra

    Note over C,DB: Create Product
    C->>PC: POST /api/products {name, category, price, ...}
    PC->>PS: save(ProductRequest)
    PS->>PS: Set UUID, timestamps, defaults
    PS->>PR: save(Product)
    PR->>DB: INSERT INTO products (...)
    DB-->>PR: Ack
    PR-->>PS: Product
    PS-->>PC: Product
    PC-->>C: 200 OK + Product JSON

    Note over C,DB: Search by Category
    C->>PC: GET /api/products/search/category/electronics
    PC->>PR: findByCategory("electronics")
    PR->>DB: SELECT * FROM products WHERE category = ? ALLOW FILTERING
    DB-->>PR: ResultSet
    PR-->>PC: List<Product>
    PC-->>C: 200 OK + List<Product> JSON
```

### Time-Series Data Flow (Sensor Readings)

```mermaid
flowchart TD
    subgraph CompositeKey["Composite Primary Key"]
        PK[Partition Key: sensor_id UUID]
        CK[Clustering Key: reading_time TIMESTAMP ASC]
    end

    subgraph Write["Write Flow"]
        W1[POST /api/sensors/:sensorId/readings] --> W2[Create SensorReadingKey]
        W2 --> W3[Create SensorReading]
        W3 --> W4[repository.save]
        W4 --> W5[INSERT INTO sensor_readings<br/>VALUES sensor_id, reading_time, value, unit]
    end

    subgraph ReadRange["Read by Time Range"]
        R1[GET /api/sensors/:id/readings/range?start=&end=] --> R2[findBySensorIdAndIdReadingTimeBetween]
        R2 --> R3[SELECT * FROM sensor_readings<br/>WHERE sensor_id = ?<br/>AND reading_time >= ? AND reading_time <= ?<br/>ORDER BY reading_time ASC]
    end

    subgraph ReadLatest["Read Latest N"]
        L1[GET /api/sensors/:id/readings/latest?limit=10] --> L2[findLatestReadings]
        L2 --> L3[SELECT * FROM sensor_readings<br/>WHERE sensor_id = ?<br/>ORDER BY reading_time DESC LIMIT ?]
    end
```

### Counter Table Flow (Metrics)

```mermaid
flowchart TD
    subgraph Schema["Table: metrics_counters"]
        S1[name TEXT - Primary Key]
        S2[counter_value COUNTER]
    end

    subgraph Create["Create Counter"]
        C1[POST /api/metrics] --> C2[UPDATE metrics_counters<br/>SET counter_value = counter_value + 0<br/>WHERE name = ?]
    end

    subgraph Increment["Increment"]
        I1[PATCH /api/metrics/:name/increment?delta=5] --> I2[UPDATE metrics_counters<br/>SET counter_value = counter_value + 5<br/>WHERE name = ?]
    end

    subgraph Decrement["Decrement"]
        D1[PATCH /api/metrics/:name/decrement?delta=1] --> D2[UPDATE metrics_counters<br/>SET counter_value = counter_value - 1<br/>WHERE name = ?]
    end

    style Schema fill:#f9f,stroke:#333
    style Note fill:#ff9,stroke:#333
```

> **Note:** Counter columns can ONLY be incremented/decremented. You cannot set them to an arbitrary value.

### TTL (Time-To-Live) Flow

```mermaid
flowchart TD
    subgraph Insert["Insert with TTL"]
        I1[POST /api/admin/ttl/:table] --> I2[INSERT INTO table (...)<br/>VALUES (...)<br/>USING TTL 300]
        I2 --> I3[Data auto-expires after 300 seconds]
    end

    subgraph Read["Read with TTL"]
        R1[GET /api/admin/ttl/:table] --> R2[SELECT *, TTL(id) as ttl<br/>FROM table]
        R2 --> R3[Returns remaining TTL for each row]
    end

    subgraph Update["Update TTL"]
        U1[PUT /api/admin/ttl/:table/:id?ttlSeconds=600] --> U2[Read existing row]
        U2 --> U3[Re-insert with new TTL]
        U3 --> U4[TTL cannot be updated in-place]
    end
```

### Batch Write Flow

```mermaid
flowchart TD
    A[POST /api/admin/batch/products] --> B[BatchService]
    B --> C[Prepare INSERT statement]
    C --> D[BEGIN UNLOGGED BATCH]
    D --> E[For each item:<br/>Bind params to PreparedStatement<br/>Add to batch]
    E --> F[APPLY BATCH]
    F --> G[All items inserted]

    style D fill:#f96,stroke:#333,color:#fff
    style F fill:#f96,stroke:#333,color:#fff
```

> **Note:** UNLOGGED batches do NOT guarantee atomicity across partitions. They are for performance, not for transactions.

### Lightweight Transaction (LWT) Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant TC as TransactionController
    participant TS as TransactionService
    participant DB as Cassandra

    Note over C,DB: Insert IF NOT EXISTS
    C->>TC: POST /api/admin/transactions/insert-if-not-exists/products
    TC->>TS: insertIfNotExists(table, keyColumn, keyValue, valueColumn, valueValue)
    TS->>DB: INSERT INTO products (...) VALUES (...) IF NOT EXISTS
    DB-->>TS: ResultSet [applied=true/false]
    TS-->>C: {applied: true/false}

    Note over C,DB: Compare and Set
    C->>TC: PUT /api/admin/transactions/compare-and-set/products
    TC->>TS: compareAndSet(table, keyColumn, keyValue, valueColumn, expectedValue, newValue)
    TS->>DB: UPDATE products SET column = newValue WHERE id = ? IF column = expectedValue
    DB-->>TS: ResultSet [applied=true/false]
    TS-->>C: {applied: true/false}

    Note over C,DB: Delete IF EXISTS
    C->>TC: DELETE /api/admin/transactions/delete-if-exists/products/:id
    TC->>TS: deleteIfExists(table, id)
    TS->>DB: DELETE FROM products WHERE id = ? IF EXISTS
    DB-->>TS: ResultSet [applied=true/false]
    TS-->>C: {applied: true/false}
```

> **Note:** LWT uses Paxos consensus. Performance cost: ~2x normal writes. Not recommended for high-throughput scenarios.

### Collection Operations Flow

```mermaid
flowchart LR
    subgraph List["LIST (ordered, allows duplicates)"]
        L1["ADD: UPDATE t SET list = list + ['a']"]
        L2["PREPEND: UPDATE t SET list = ['a'] + list"]
        L3["REMOVE: UPDATE t SET list = list - ['a']"]
    end

    subgraph Set["SET (unique, unordered)"]
        S1["ADD: UPDATE t SET set = set + {'a'}"]
        S2["REMOVE: UPDATE t SET set = set - {'a'}"]
    end

    subgraph Map["MAP (key-value pairs)"]
        M1["PUT: UPDATE t SET map = map + {'k': 'v'}"]
        M2["REMOVE: UPDATE t SET map = map - {'k'}"]
    end

    style List fill:#e1f5fe
    style Set fill:#f3e5f5
    style Map fill:#e8f5e9
```

### SASI Index Flow

```mermaid
flowchart TD
    A[SASI Index Types] --> B[CONTAINS - Full-text search]
    A --> C[PREFIX - Autocomplete]
    A --> D[SPARSE - Low-cardinality columns]

    B --> B1["CREATE CUSTOM INDEX ON table (column)<br/>USING 'SASIIndex'<br/>WITH OPTIONS = {'mode': 'CONTAINS'}"]
    C --> C1["CREATE CUSTOM INDEX ON table (column)<br/>USING 'SASIIndex'<br/>WITH OPTIONS = {'mode': 'PREFIX'}"]
    D --> D1["CREATE CUSTOM INDEX ON table (column)<br/>USING 'SASIIndex'<br/>WITH OPTIONS = {'mode': 'SPARSE'}"]

    style B fill:#bbdefb
    style C fill:#c8e6c9
    style D fill:#ffe0b2
```

### Advanced Query Flow (Consistency Levels)

```mermaid
flowchart TD
    A[POST /api/admin/advanced/query/consistency/QUORUM] --> B[Parse consistency level from URL]
    B --> C[Create SimpleStatement from CQL]
    C --> D[Set consistency on statement]
    D --> E[Execute via CqlSession]
    E --> F[Return results as List of Map]

    subgraph Levels["Consistency Levels"]
        L1[ONE - 1 replica must respond]
        L2[QUORUM - (RF/2)+1 replicas respond]
        L3[ALL - All replicas must respond]
        L4[LOCAL_QUORUM - (RF/2)+1 in local DC]
    end

    G[Trade-off] --> H[Higher consistency = More latency + Higher availability]
    G --> I[Lower consistency = Less latency + Lower availability]

    style Levels fill:#fff3e0
```

### Token Range Query Flow

```mermaid
flowchart TD
    A[GET /api/admin/advanced/token-range/products/name] --> B[Cassandra assigns token to each row based on partition key]
    B --> C[Token range query scans specific partition range]
    C --> D[More efficient than full table scan]

    E[Generated CQL] --> F["SELECT * FROM products<br/>WHERE token(name) > -9223372036854775808<br/>AND token(name) <= 0"]

    G[Use Cases] --> H[Data migration between nodes]
    G --> I[Debugging partition distribution]
    G --> J[Range-based data analysis]
```

---

## Complete API Reference

### Authentication (No Auth Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT |

### Products

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/products` | Create product |
| GET | `/api/products` | List all products |
| GET | `/api/products/{id}` | Get product by ID |
| PUT | `/api/products/{id}` | Full update product |
| PATCH | `/api/products/{id}` | Partial update product |
| DELETE | `/api/products/{id}` | Delete product |
| DELETE | `/api/products` | Delete all products |
| GET | `/api/products/count` | Count all products |
| POST | `/api/products/bulk` | Bulk create products |
| DELETE | `/api/products/bulk` | Bulk delete products |
| GET | `/api/products/search/name/{name}` | Search by name |
| GET | `/api/products/search/category/{category}` | Search by category |
| GET | `/api/products/search/price?min=&max=` | Search by price range |
| GET | `/api/products/search/tag/{tag}` | Search by tag |
| GET | `/api/products/search/available/{bool}` | Search by availability |
| GET | `/api/products/search/category/{cat}/available/{avail}` | Combined search |
| GET | `/api/products/search/price-less-than/{price}` | Search price < value |
| GET | `/api/products/count/category/{category}` | Count by category |
| GET | `/api/products/count/available/{bool}` | Count by availability |

### Articles

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/articles` | Create article |
| GET | `/api/articles` | List all articles |
| GET | `/api/articles/{id}` | Get article by ID |
| PUT | `/api/articles/{id}` | Update article |
| DELETE | `/api/articles/{id}` | Delete article |
| GET | `/api/articles/count` | Count all articles |
| POST | `/api/articles/bulk` | Bulk create articles |
| GET | `/api/articles/author/{author}` | Search by author |
| GET | `/api/articles/status/{status}` | Search by status |
| GET | `/api/articles/published/{bool}` | Search by published state |
| GET | `/api/articles/published-not-deleted` | Get published non-deleted |
| GET | `/api/articles/search/title/{title}` | Search by title |
| GET | `/api/articles/search/content/{content}` | Search by content |
| GET | `/api/articles/date-range?start=&end=` | Search by date range |
| GET | `/api/articles/count/author/{author}` | Count by author |
| GET | `/api/articles/count/status/{status}` | Count by status |
| PATCH | `/api/articles/{id}/view` | Increment view count |

### Log Entries

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/logs` | Create log entry |
| GET | `/api/logs` | List all logs |
| GET | `/api/logs/{id}` | Get log by ID |
| DELETE | `/api/logs/{id}` | Delete log |
| DELETE | `/api/logs` | Delete all logs |
| POST | `/api/logs/bulk` | Bulk create logs |
| GET | `/api/logs/level/{level}` | Search by level |
| GET | `/api/logs/service/{service}` | Search by service |
| GET | `/api/logs/level/{level}/service/{service}` | Combined search |
| GET | `/api/logs/response-code/{code}` | Search by response code |
| GET | `/api/logs/trace/{traceId}` | Search by trace ID |
| GET | `/api/logs/search/message/{message}` | Search by message |
| GET | `/api/logs/date-range?start=&end=` | Search by date range |
| GET | `/api/logs/level/{level}/date-range` | Level + date range |
| GET | `/api/logs/errors` | Get error logs (code >= 400) |
| GET | `/api/logs/slow?minDurationMs=1000` | Get slow requests |
| GET | `/api/logs/count/level/{level}` | Count by level |
| GET | `/api/logs/count/service/{service}` | Count by service |

### Locations

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/locations` | Create location |
| GET | `/api/locations` | List all locations |
| GET | `/api/locations/{id}` | Get location by ID |
| PUT | `/api/locations/{id}` | Update location |
| DELETE | `/api/locations/{id}` | Delete location |
| GET | `/api/locations/type/{type}` | Search by type |
| GET | `/api/locations/country/{country}` | Search by country |
| GET | `/api/locations/city/{city}` | Search by city |
| GET | `/api/locations/country/{country}/city/{city}` | Combined search |
| GET | `/api/locations/search/name/{name}` | Search by name |
| GET | `/api/locations/type/{type}/country/{country}` | Type + country |

### Employees

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/employees` | Create employee |
| GET | `/api/employees` | List all employees |
| GET | `/api/employees/{id}` | Get employee by ID |
| DELETE | `/api/employees/{id}` | Delete employee |
| GET | `/api/employees/department/{dept}` | Search by department |
| GET | `/api/employees/skill/{skill}` | Search by skill |

### Events

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/events` | Create event |
| GET | `/api/events` | List all events |
| GET | `/api/events/{id}` | Get event by ID |
| DELETE | `/api/events/{id}` | Delete event |
| GET | `/api/events/type/{type}` | Search by type |
| GET | `/api/events/type/{type}/range?start=&end=` | Type + time range |
| GET | `/api/events/type/{type}/latest?limit=10` | Latest N events |
| GET | `/api/events/type/{type}/page?size=10` | Paginated events |
| GET | `/api/events/count/type/{type}` | Count by type |

### Sensor Readings

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/sensors/{sensorId}/readings` | Create reading |
| GET | `/api/sensors/{sensorId}/readings` | Get all readings |
| GET | `/api/sensors/{sensorId}/readings/range?start=&end=` | Time range |
| GET | `/api/sensors/{sensorId}/readings/latest?limit=10` | Latest readings |
| POST | `/api/sensors/batch` | Bulk create readings |
| DELETE | `/api/sensors/readings` | Delete all readings |

### Metrics Counters

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/metrics` | Create counter |
| GET | `/api/metrics/{name}` | Get counter value |
| PATCH | `/api/metrics/{name}/increment?delta=1` | Increment counter |
| PATCH | `/api/metrics/{name}/decrement?delta=1` | Decrement counter |
| DELETE | `/api/metrics/{name}` | Delete counter |

### Users

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/me` | Get current user |
| GET | `/api/users` | List all users |
| GET | `/api/users/{id}` | Get user by ID |
| DELETE | `/api/users/{id}` | Delete user |
| PATCH | `/api/users/{id}/role?role=` | Update user role |
| PATCH | `/api/users/{id}/enabled` | Toggle user enabled |

### Admin - Batch Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/admin/batch/products` | Batch insert products |
| POST | `/api/admin/batch/logs` | Batch insert logs |

### Admin - Collection Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/admin/collections/{table}/list/add` | Add to list |
| POST | `/api/admin/collections/{table}/list/remove` | Remove from list |
| POST | `/api/admin/collections/{table}/list/prepend` | Prepend to list |
| POST | `/api/admin/collections/{table}/set/add` | Add to set |
| POST | `/api/admin/collections/{table}/set/remove` | Remove from set |
| POST | `/api/admin/collections/{table}/map/put` | Put to map |
| POST | `/api/admin/collections/{table}/map/remove` | Remove from map |

### Admin - Schema Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/admin/schema/keyspace` | Create keyspace |
| POST | `/api/admin/schema/table/{keyspace}` | Create table |
| POST | `/api/admin/schema/index/{keyspace}/{table}` | Create index |
| POST | `/api/admin/schema/materialized-view/{keyspace}` | Create materialized view |
| DELETE | `/api/admin/schema/table/{keyspace}/{table}` | Drop table |
| DELETE | `/api/admin/schema/keyspace/{keyspace}` | Drop keyspace |
| GET | `/api/admin/schema/describe/{keyspace}` | Describe keyspace |

### Admin - SASI Index Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/admin/sasi/{keyspace}/{table}` | Create CONTAINS index |
| POST | `/api/admin/sasi/{keyspace}/{table}/prefix` | Create PREFIX index |
| POST | `/api/admin/sasi/{keyspace}/{table}/sparse` | Create SPARSE index |
| DELETE | `/api/admin/sasi/{keyspace}/{indexName}` | Drop SASI index |

### Admin - Transactions (LWT)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/admin/transactions/insert-if-not-exists/{table}` | Insert if not exists |
| PUT | `/api/admin/transactions/compare-and-set/{table}` | Compare and set |
| DELETE | `/api/admin/transactions/delete-if-exists/{table}/{id}` | Delete if exists |

### Admin - TTL Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/admin/ttl/{table}` | Insert with TTL |
| GET | `/api/admin/ttl/{table}` | Get all with TTL values |
| PUT | `/api/admin/ttl/{table}/{id}?ttlSeconds=` | Update TTL |

### Admin - Advanced Queries

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/advanced/count/{table}` | Count rows in table |
| GET | `/api/admin/advanced/min/{table}/{column}` | Min value of column |
| GET | `/api/admin/advanced/max/{table}/{column}` | Max value of column |
| GET | `/api/admin/advanced/sum/{table}/{column}` | Sum of column |
| GET | `/api/admin/advanced/avg/{table}/{column}` | Average of column |
| GET | `/api/admin/advanced/token-range/{table}/{pk}` | Token range query |
| POST | `/api/admin/advanced/truncate/{table}` | Truncate table |
| POST | `/api/admin/advanced/alter/add/{table}` | Add column |
| POST | `/api/admin/advanced/alter/drop/{table}` | Drop column |
| POST | `/api/admin/advanced/alter/rename/{table}` | Rename column |
| POST | `/api/admin/advanced/alter/comment/{table}` | Add table comment |
| POST | `/api/admin/advanced/delete-range/{table}` | Delete range of rows |
| POST | `/api/admin/advanced/query/consistency/{level}` | Query with consistency |

### Actuator Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/actuator/health` | Application health check |
| GET | `/actuator/info` | Application info |
| GET | `/actuator/beans` | Spring beans |
| GET | `/actuator/cassandra` | Cassandra health details |

---

## Key Cassandra Concepts Demonstrated

### 1. User Defined Types (UDTs)
Embed structured data within a column. The `Review` UDT stores reviewer, comment, and rating inside the `Product.reviews` list column.

### 2. Collections (List, Set, Map)
Cassandra supports three collection types:
- **List**: Ordered, allows duplicates (e.g., `categories`, `tags` in Article)
- **Set**: Unordered, unique values (e.g., `skills`, `certifications` in Employee)
- **Map**: Key-value pairs (e.g., `data` in Event, `attributes` in Employee)

### 3. Composite Primary Keys
`SensorReading` uses a composite key with `sensor_id` as partition key and `reading_time` as clustering key, enabling efficient time-series queries within a partition.

### 4. Counter Columns
`MetricsCounter` uses Cassandra's counter column type for atomic increment/decrement operations. Counters can only be updated, not set to arbitrary values.

### 5. Materialized Views
Pre-computed denormalized views for alternative query patterns. Created on startup by `ViewInitializer`.

### 6. Lightweight Transactions (LWT)
Paxos-based conditional operations: `IF NOT EXISTS`, `IF column = value`, `IF EXISTS`. Ensures atomicity at the cost of ~2x write latency.

### 7. TTL (Time-To-Live)
Automatic data expiration. Rows with TTL are automatically deleted by Cassandra after the specified duration.

### 8. Batch Operations
`UNLOGGED` batches for performance-oriented bulk inserts across multiple partitions. Not atomic across partitions.

### 9. SASI Indexes
Secondary indexes supporting full-text search (CONTAINS), prefix matching (PREFIX), and low-cardinality columns (SPARSE).

### 10. Consistency Levels
Runtime-configurable consistency: ONE, QUORUM, ALL, LOCAL_QUORUM. Controls the trade-off between latency and data consistency.

### 11. Token Range Queries
Direct partition-level access using the `token()` function for data migration and debugging partition distribution.

---

## Getting Started

### Prerequisites

- Java 25+
- Docker & Docker Compose
- Maven 3.9+

### 1. Start Cassandra

```bash
docker-compose up -d
```

Wait ~60 seconds for Cassandra to be fully ready.

### 2. Build and Run

```bash
./mvnw clean package -DskipTests
java -jar target/cassandra-0.0.1-SNAPSHOT.jar
```

Or:

```bash
./mvnw spring-boot:run
```

### 3. Verify

```bash
# Health check
curl http://localhost:8080/actuator/health

# Register a user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","email":"admin@example.com","password":"admin123"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

---

## Configuration

Key settings in `application.yaml`:

```yaml
spring:
  cassandra:
    contact-points: localhost
    port: 9042
    local-datacenter: dc1
    schema-action: create_if_not_exists
    username: cassandra
    password: password

server:
  port: 8080

app:
  jwt:
    secret: <hex-encoded-secret>
    expiration-ms: 86400000  # 24 hours

management:
  endpoints:
    web:
      exposure:
        include: health,info,beans,cassandra
```

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
