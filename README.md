# Koha Port Workspace

This repository is organized so the workspace root is the main Java project entry point while preserving the original Koha codebase.

## Structure

- `koha-java-catalog/`: Catalog microservice scaffold.
- `koha-java-auth/`: Authentication microservice scaffold.
- `koha-java-patron/`: Patron microservice scaffold.
- `koha-java-search/`: Search microservice scaffold.
- `koha-java-admin/`: Administration microservice scaffold.
- `koha-java-circulation/`: Circulation microservice scaffold.
- `koha-java-holds/`: Holds microservice scaffold.
- `koha-java-finance/`: Finance microservice scaffold.
- `koha-java-notification/`: Notification microservice scaffold.
- `koha-java-acquisitions/`: Acquisitions microservice scaffold.
- `koha-java-serials/`: Serials microservice scaffold.
- `koha-java-erm/`: ERM microservice scaffold.
- `koha-java-ill/`: ILL microservice scaffold.
- `koha-java-reporting/`: Reporting microservice scaffold.
- `koha-java-batch/`: Batch microservice scaffold.
- `koha-java-opac-gateway/`: OPAC gateway scaffold.
- `koha-java-intranet-gateway/`: Intranet gateway scaffold.
- `koha-original/`: Original Koha codebase kept for reference and phased migration.
- `MIGRATION_MODULE_PLAN.md`: Wave plan and Swagger path ownership map.

## Root Maven Entry Point

The root `pom.xml` is an aggregator project.

- Build all Java modules from root:
  - `mvn -f pom.xml clean test`
- Or use the existing wrapper in the Java module:
  - `koha-java-catalog\\mvnw.cmd -f pom.xml -pl koha-java-catalog -am clean test`

## Notes

- `koha-original/` is intentionally kept side-by-side and not modified by the Java build.
- You can import the repository root into IntelliJ as a Maven project and manage `koha-java-catalog` as a module.

