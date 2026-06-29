# StockTag — Inventory Management System

A full-stack inventory, order, and team management system. Originally built as a Java Swing desktop app, rebuilt as a **Spring Boot REST API** with a **responsive web frontend** — usable from a desktop browser or a phone.

[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Live Demo](https://img.shields.io/badge/Live%20Demo-Vercel-black)](https://stocktag-inventory-system.vercel.app/)

---

## 🌐 Live Demo

**Frontend:** [https://stocktag-inventory-system.vercel.app/](https://stocktag-inventory-system.vercel.app/)

**Backend API:** [https://stocktag-inventory-system-production.up.railway.app/api/health](https://stocktag-inventory-system-production.up.railway.app/api/health)

---

## 🔑 Demo Login Credentials

Try the app instantly using these accounts — no sign-up needed.

### Admin Account
> Full access — can manage products, categories, customers, orders, and team members.

| Field | Value |
|---|---|
| Email | `admin@inventory.local` |
| Password | `Admin@12345` |

### Staff Accounts
> Limited access — can manage products, categories, customers and orders. Cannot access the Team page.

| Name | Email | Password |
|---|---|---|
| Anita Singh | `anita@stocktag.local` | `StockTag@123` |
| Rahul Verma | `rahul@stocktag.local` | `StockTag@123` |
| Pooja Sharma | `pooja@stocktag.local` | `StockTag@123` |
| Karan Singh | `karan@stocktag.local` | `StockTag@123` |

### Inactive Account (for testing)
> Login will be blocked with an appropriate error message.

| Field | Value |
|---|---|
| Email | `ritu@stocktag.local` |
| Password | `StockTag@123` |

---

## Features

- 🔐 **Secure auth** — JWT-based login, BCrypt-hashed passwords, role-based access (Admin / Staff)
- 📦 **Product & category management** — stock levels, low-stock alerts, pricing
- 👥 **Customer & order management** — point-of-sale style order builder with live cart and stock validation
- 🧾 **Bill generation** — print or save PDF invoice per order
- 📊 **Dashboard** — live stats: revenue, order count, low-stock warnings
- 📱 **Fully responsive** — sidebar nav on desktop, bottom tab bar on mobile, tables collapse into cards
- 🎨 **Two themes** — clean "Daylight" theme and a dark "Boar Hat Tavern" theme
- 🔊 **Sound design** — synthesized click feedback and ambient background tone (Web Audio API, no files)
- ✨ Smooth micro-animations throughout

---

## Project structure

```
.
├── inventory-api/        Spring Boot REST API (Java 21, Spring Security, JPA, MySQL, Flyway)
├── inventory-web/        Responsive frontend (vanilla HTML/CSS/JS — no build step required)
├── legacy-swing-app/     The original Java Swing desktop version (kept for history)
├── docker-compose.yml    One-command local stack: MySQL + API + web, via Docker
└── LICENSE
```

---

## Quick start (Docker — easiest)

Requires [Docker](https://www.docker.com/) installed.

```bash
git clone https://github.com/MohitGoel109/stocktag-inventory-system.git
cd stocktag-inventory-system
docker compose up --build
```

Then open **http://localhost:8081**

On first run, a default admin account is created. Check the API container logs:

```bash
docker compose logs api | grep -A4 "First run detected"
```

---

## Quick start (without Docker)

### 1. Database

```sql
CREATE DATABASE inventory_db;
```

### 2. Backend

```bash
cd inventory-api
export DB_URL="jdbc:mysql://localhost:3306/inventory_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password
export JWT_SECRET=your_secret_key_at_least_32_characters
export CORS_ALLOWED_ORIGINS=http://localhost:5500

./mvnw spring-boot:run
```

> **Windows PowerShell:** use `$env:DB_URL="..."` instead of `export`

### 3. Frontend

```bash
cd inventory-web
python -m http.server 5500
```

Open **http://localhost:5500**

---

## Environment variables (backend)

| Variable | Purpose | Default |
|---|---|---|
| `DB_URL` | JDBC connection string | localhost/inventory_db |
| `DB_USERNAME` | Database username | `root` |
| `DB_PASSWORD` | Database password | *(set this!)* |
| `JWT_SECRET` | Signing key (32+ chars) | dev fallback only |
| `JWT_EXPIRATION_MS` | Token lifetime in ms | `86400000` (24h) |
| `CORS_ALLOWED_ORIGINS` | Allowed frontend origins | `http://localhost:5500` |
| `APP_BOOTSTRAP_ADMIN_EMAIL` | First-run admin email | `admin@inventory.local` |
| `APP_BOOTSTRAP_ADMIN_PASSWORD` | First-run admin password | `Admin@12345` |
| `SERVER_PORT` | API port | `8080` |

---

## Tech stack

**Backend:** Java 21 · Spring Boot 3 · Spring Security · Spring Data JPA · MySQL 8 · Flyway · JWT (jjwt) · Maven

**Frontend:** Vanilla HTML/CSS/JavaScript · Web Audio API

**Deployment:** Railway (API + MySQL) · Vercel (Frontend)

---

## Security improvements over original

The original Swing version had several security issues — all fixed in this rebuild:

- ✅ Replaced SQL injection vulnerable string-concatenated queries with parameterized JPA queries
- ✅ Replaced plaintext password storage with BCrypt hashing
- ✅ Removed hardcoded database credentials (now via environment variables)
- ✅ Added transactional stock validation on order placement
- ✅ JWT-based stateless authentication with role-based access control

---

## License

MIT — see [LICENSE](LICENSE)

## Author

**Mohit Goel** — [@MohitGoel109](https://github.com/MohitGoel109)
