# Database Migration Learning Project

A simple project to understand and explore database migration concepts and implementation. This project serves as a learning exercise to dive deeper into how database schema changes are managed and versioned.

## What I'm Learning

- How to implement SQL-based migrations
- Version control for database schemas
- Checksum implementation for file integrity
- Transaction handling in migrations
- Database connectivity and operations

## Project Structure

1. Migration files are stored in `src/main/resources/migrations/` following the pattern:
   ```
   V{version}__{description}.sql
   ```
   Example: `V1__create_users.sql`

2. To build:
   ```bash
   ./gradlew build
   ```

3. To run:
   ```bash
   java -jar build/libs/simple-db-migrator-1.0-SNAPSHOT.jar <jdbc-url> <username> <password>
   ```

## Test Example