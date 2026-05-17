 Home Services Marketplace Platform

A distributed on-demand home services marketplace built with microservices architecture, where customers can book skilled professionals (plumbers, carpenters, electricians, cleaners, etc.).

---

## Architecture

The system is built using **4 independent microservices**, each with its own database:

```
┌─────────────────────────────────────────────────────────────┐
│                     MICROSERVICES                            │
├──────────────────┬───────────┬──────────────────────────────┤
│ Service          │ Port      │ Responsibility               │
├──────────────────┼───────────┼──────────────────────────────┤
│ User Service     │ 8081      │ Registration, Login, Wallet  │
│ Offer Service    │ 8082      │ Service offers management    │
│ Booking Service  │ 8083      │ Bookings & payments          │
│ Notification Svc │ 8084      │ Async notifications          │
└──────────────────┴───────────┴──────────────────────────────┘
```

---

## 🛠️ Tech Stack

- **Backend:** Java 17, Spring Boot 4.0
- **Security:** Spring Security + JWT Authentication
- **Messaging:** RabbitMQ (Topic Exchange + Direct Exchange)
- **Database:** PostgreSQL (separate DB per service)
- **Containerization:** Docker & Docker Compose
- **Build Tool:** Maven

---

## ✨ Features

### Customer
- Register with initial wallet balance
- Add funds to wallet at any time
- Browse available services by category
- Book a professional service
- Receive booking confirmation/failure notifications
- View booking history

### Service Provider
- Register with profession type (Plumber, Carpenter, etc.)
- Create service offers with price and available date
- View and update their own offers
- Receive booking notifications

### Admin
- View all registered users
- View all booking history
- Receive payment failure notifications

---

## RabbitMQ Messaging

```
Booking Service ──→ booking.exchange (Topic)
                         │
                         ├──→ booking.confirmation queue ──→ Notification Service
                         └──→ booking.failure queue     ──→ Notification Service

Booking Service ──→ payments exchange (Direct)
                         │
                         └──→ admin.payment.failed queue ──→ Notification Service (Admin alert)
```

- **Booking confirmed** → customer and provider notified asynchronously
- **Booking failed** → customer notified + admin alerted via direct exchange
- **Rollback** → balance returned if booking fails

---

## EJB Concepts (Spring Boot Equivalents)

| EJB Bean | Spring Equivalent | Used In | Purpose |
|---|---|---|---|
| `@Stateless` | `@Service` | `BookingService` | Stateless booking logic |
| `@Singleton` | `@Component @Scope("singleton")` | `BookingStatsManager` | Shared booking statistics |

---

## Getting Started

### Prerequisites
- Java 17+
- Docker Desktop
- Maven

### 1. Start infrastructure (DBs + RabbitMQ)
```bash
cd home-services
docker compose up -d
```

### 2. Run each service in order
```bash
# Terminal 1
cd user-service && mvn spring-boot:run

# Terminal 2
cd offer-service && mvn spring-boot:run

# Terminal 3
cd Booking-service && mvn spring-boot:run

# Terminal 4
cd notification-service && mvn spring-boot:run
```

---

## API Endpoints

### User Service (port 8081)
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/users/register/customer` | Public | Register customer |
| POST | `/users/register/provider` | Public | Register provider |
| POST | `/users/login` | Public | Login & get JWT token |
| GET | `/users/balance` | Token | Get wallet balance |
| PUT | `/users/add-funds?amount=100` | Token | Add funds to wallet |
| GET | `/users/all` | Admin | View all users |

### Offer Service (port 8082)
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/offers/create` | Provider | Create service offer |
| GET | `/offers/my-offers` | Provider | View own offers |
| GET | `/offers/category/{category}` | Public | Search by category |
| PUT | `/offers/update/{offerId}` | Provider | Update offer |
| GET | `/offers/all` | Admin | View all offers |

### Booking Service (port 8083)
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/bookings/book/{offerId}` | Customer | Book a service |
| GET | `/bookings/my-bookings` | Customer | View my bookings |
| GET | `/bookings/all` | Admin | View all bookings |
| GET | `/bookings/stats` | Public | Booking statistics |

### Notification Service (port 8084)
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/notifications/my-notifications` | Token | View my notifications |
| GET | `/notifications/all` | Admin | View all notifications |

---

## Authentication

The system uses **JWT (JSON Web Tokens)** for stateless authentication across all microservices:

```
1. Register → POST /users/register/customer
2. Login    → POST /users/login → returns JWT token
3. Use token in every request header:
   Authorization: Bearer <token>
```

---

## Docker Services

```yaml
user-db:         PostgreSQL for User Service
offer-db:        PostgreSQL for Offer Service
booking-db:      PostgreSQL for Booking Service
notification-db: PostgreSQL for Notification Service
rabbitmq:        Message broker with management dashboard
```

RabbitMQ dashboard: http://localhost:15672 (guest/guest)
