# StockTag — Inventory Management System

A full-stack inventory, order, and team management system. Originally built as a Java Swing desktop app, rebuilt here as a **Spring Boot REST API** with a **responsive web frontend** — usable from a desktop browser or a phone.

[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## Features

- 🔐 **Secure auth** — JWT-based login, BCrypt-hashed passwords, role-based access (Admin / Staff)
- 📦 **Product & category management** — stock levels, low-stock alerts, pricing
- 👥 **Customer & order management** — point-of-sale style order builder with live cart and stock validation
- 📊 **Dashboard** — live stats: revenue, order count, low-stock warnings
- 📱 **Fully responsive** — sidebar nav on desktop, bottom tab bar on mobile, tables collapse into cards
- 🎨 **Two themes** — clean "Daylight" theme and an original dark "Boar Hat Tavern" theme
- 🔊 **Sound design** — synthesized click feedback and ambient background tone (generated in-browser, no audio files)
- ✨ Smooth, tasteful micro-animations throughout

## Project structure

```
.
├── inventory-api/        Spring Boot REST API (Java 21, Spring Security, JPA, MySQL, Flyway)
├── inventory-web/        Responsive frontend (vanilla HTML/CSS/JS — no build step required)
├── legacy-swing-app/     The original Java Swing desktop version (kept for history — see its README)
├── docker-compose.yml    One-command local stack: MySQL + API + web, via Docker
└── LICENSE
```

## Quick start (Docker — easiest)

Requires [Docker](https://www.docker.com/) installed.

```bash
git clone https://github.com/MohitGoel109/<your-repo-name>.git
cd <your-repo-name>
docker compose up --build
```

Then open:
- **Web app:** http://localhost:8081
- **API:** http://localhost:8080/api

On first run, a default admin account is created automatically. **Check the API container logs** for the generated email/password:

```bash
docker compose logs api | grep -A4 "First run detected"
```

Log in, then immediately change that password from the Team page.

## Quick start (without Docker)

### 1. Database

Create a MySQL 8 database:

```sql
CREATE DATABASE inventory_db;
```

### 2. Backend

```bash
cd inventory-api
export DB_URL="jdbc:mysql://localhost:3306/inventory_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password
export JWT_SECRET=$(openssl rand -base64 64)   # or any string 32+ characters long
export CORS_ALLOWED_ORIGINS=http://localhost:5500

./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`. Flyway creates the schema automatically; a default admin account is created on first boot (see console output).

> **Windows:** use `mvnw.cmd` instead of `./mvnw`, and `set VAR=value` instead of `export VAR=value`.

### 3. Frontend

The frontend is plain HTML/CSS/JS — no build step. Serve it with any static file server, for example:

```bash
cd inventory-web
python3 -m http.server 5500
```

Open `http://localhost:5500`. If your API runs somewhere other than `http://localhost:8080/api`, set this before the other scripts load (e.g. in a small `<script>` tag in each HTML file, or by editing `js/api.js`):

```html
<script>window.INVENTORY_API_BASE_URL = "https://your-api-host/api";</script>
```

## Environment variables (backend)

| Variable | Purpose | Default |
|---|---|---|
| `DB_URL` | JDBC connection string | `jdbc:mysql://localhost:3306/inventory_db...` |
| `DB_USERNAME` | Database username | `root` |
| `DB_PASSWORD` | Database password | *(empty — set this!)* |
| `JWT_SECRET` | Signing key for auth tokens (32+ chars) | dev-only fallback, **override in production** |
| `JWT_EXPIRATION_MS` | Token lifetime in ms | `86400000` (24h) |
| `CORS_ALLOWED_ORIGINS` | Comma-separated allowed frontend origins | `http://localhost:5500,...` |
| `APP_BOOTSTRAP_ADMIN_EMAIL` | First-run admin email | `admin@inventory.local` |
| `APP_BOOTSTRAP_ADMIN_PASSWORD` | First-run admin password | `Admin@12345` (**change immediately**) |
| `SERVER_PORT` | API port | `8080` |

None of these are committed to the repo — see `inventory-api/src/main/resources/application.yml`, which reads all of them from the environment with safe local-dev fallbacks only.

## Tech stack

**Backend:** Java 21 · Spring Boot 3 · Spring Security · Spring Data JPA · MySQL 8 · Flyway · JWT (jjwt) · Maven

**Frontend:** Vanilla HTML/CSS/JavaScript (no framework, no build step) · Web Audio API for synthesized sound

## Security notes

This project fixes several issues present in the original Swing version:
- Replaced string-concatenated SQL (vulnerable to SQL injection) with parameterized queries via Spring Data JPA
- Replaced plaintext password storage with BCrypt hashing
- Removed hardcoded database credentials from source code (now via environment variables)
- Added transactional stock validation when placing orders (prevents overselling / partial-failure stock corruption)

## License

MIT — see [LICENSE](LICENSE).

## Author

**Mohit Goel** — [@MohitGoel109](https://github.com/MohitGoel109)
