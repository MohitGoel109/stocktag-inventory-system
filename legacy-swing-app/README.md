# Legacy: Java Swing Desktop App

This is the **original version** of this project — a Java Swing desktop application backed by JDBC/MySQL, built with the NetBeans GUI Builder.

It's kept here for historical reference and to show the project's progression. **The actively maintained version lives in [`../inventory-api`](../inventory-api) and [`../inventory-web`](../inventory-web)** — a Spring Boot REST API with a responsive web frontend, usable on desktop and mobile.

## Status

This code is **not actively maintained**. A few known issues from the original version (documented honestly, not swept under the rug):

- The UI uses NetBeans `AbsoluteLayout`, so it's hardcoded to a specific large screen size and will not resize gracefully.
- Two `UPDATE` queries (editing a product and editing a category) have a SQL syntax bug (`set name?` is missing `=`) and will throw an error at runtime if triggered.
- Passwords were originally stored and compared in plaintext.
- The login query originally built SQL via string concatenation (SQL injection risk) — **before publishing this repository, the hardcoded database password that was present in two files was removed** (see `src/dao/ConnectionProvider.java` and `src/ManageUser.java`). You'll need to set `DB_USER` / `DB_PASSWORD` environment variables to run this locally.

All of the above are fixed in the current Spring Boot + web version — see the root [README](../README.md) for details.

## Running this legacy app (if you want to)

You'll need:
- Java JDK 8+ 
- NetBeans IDE (recommended, since this uses NetBeans-specific project files and `.form` GUI layouts)
- MySQL with a database named `inventory` and the matching tables (no schema script was ever included in the original project — table structure can be inferred from `src/dao/tables.java`)
- The MySQL Connector/J JAR on the classpath

Set environment variables before running:

```bash
export DB_USER=root
export DB_PASSWORD=your_mysql_password
```

Then open the project in NetBeans and run it, or build with `ant` using the included `build.xml`.
