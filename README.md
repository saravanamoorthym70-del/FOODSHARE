# FoodShare

FoodShare matches surplus food donations with local NGOs. The Spring Boot application stores donors, food listings, NGOs, and claims in MySQL and serves the light-themed UI at `http://localhost:8080`.

## Run locally

1. Start the `MySQL80` Windows service.
2. From PowerShell in this folder, run `./run.ps1` and enter the MySQL password when prompted. The password is held only for that application process; it is not stored in the project.
3. Open `http://localhost:8080`.

The application creates the `foodshare_db` schema and its tables on startup. `DB_PASSWORD` can also be supplied as an environment variable when launching Maven directly. Do not put database credentials in `application.properties`.

## MySQL Workbench

Create a Standard TCP/IP connection with hostname `127.0.0.1`, port `3306`, and username `root`. Enter the same password used by `run.ps1` in Workbench's password prompt. Start the app once, then refresh the Schemas panel and open `foodshare_db`; Hibernate creates or updates the tables for donors, NGOs, food listings, and claims.

## Main workflows

- Register a donor, then publish food with quantity, type, unit, pickup notes, and a safe-to-eat deadline.
- Register an NGO, browse available donations, and claim a listing.
- Mark an active claim collected or cancel it. The server expires unclaimed listings after their safe-to-eat deadline.
- View the monthly collected-listing and diverted-quantity report.

Run with `./run.ps1` from PowerShell. The Maven launch uses cached dependencies (`-o`) so a temporary Maven Central outage does not block local startup.