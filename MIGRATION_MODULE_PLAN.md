# Koha Java Module Phasing and Swagger Path Ownership

This document finalizes the phased Java module set using:
- `koha-original/docs/java-migration-plan.md` (Appendix A)
- `koha-original/api/v1/swagger/paths/` (current Swagger path files)

Scope is API migration for JSON/XML only. HTML migration remains separate.

## 1) Root Aggregator and Coupling Rules

- Root `pom.xml` is the single Maven aggregator for Java modules.
- `koha-original/` stays intact and is not added as a Java module.
- Current module structure follows `koha-java-*` naming to match existing workspace conventions.

## 2) Final Wave Definitions

### Wave 1 (Foundation Domain APIs)
- `koha-java-catalog`
- `koha-java-patron`
- `koha-java-auth`
- `koha-java-search`
- `koha-java-admin`

### Wave 2 (Transactional and Messaging Core)
- `koha-java-circulation`
- `koha-java-holds`
- `koha-java-finance`
- `koha-java-notification`
- `koha-java-acquisitions`

### Wave 3 (Specialized and Platform Services)
- `koha-java-serials`
- `koha-java-erm`
- `koha-java-ill`
- `koha-java-reporting`
- `koha-java-batch`
- `koha-java-opac-gateway`
- `koha-java-intranet-gateway`

## 3) Scaffold Standard (Parity Contract)

Each module has parity with `koha-java-catalog` baseline:
- `pom.xml`
- `src/main/java/.../KohaJava*Application.java`
- `src/test/java/.../KohaJava*ApplicationTests.java`
- `src/main/resources/application.properties`

## 4) Swagger Path Ownership Map

Ownership below is the initial migration split for implementation planning.

| Swagger path file | Owning Java module | Wave | Notes |
|---|---|---|---|
| `acquisitions_baskets.yaml` | `koha-java-acquisitions` | 2 | Acquisitions baskets |
| `acquisitions_edifiles.yaml` | `koha-java-acquisitions` | 2 | EDI/EDIFACT |
| `acquisitions_funds.yaml` | `koha-java-acquisitions` | 2 | Budgets/funds |
| `acquisitions_orders.yaml` | `koha-java-acquisitions` | 2 | Orders lifecycle |
| `acquisitions_vendors.yaml` | `koha-java-acquisitions` | 2 | Vendor CRUD |
| `acquisitions_vendors_config.yaml` | `koha-java-acquisitions` | 2 | Vendor settings |
| `acquisitions_vendors_extended_attribute_types.yaml` | `koha-java-acquisitions` | 2 | Vendor attrs |
| `acquisitions_vendor_issues.yaml` | `koha-java-acquisitions` | 2 | Vendor issue workflows |
| `additional_contents.yaml` | `koha-java-notification` | 2 | Notices/content templates |
| `advancededitormacros.yaml` | `koha-java-admin` | 1 | Admin configuration |
| `article_requests.yaml` | `koha-java-holds` | 2 | Linked with hold/request workflows |
| `auth.yaml` | `koha-java-auth` | 1 | Authentication/authorization |
| `authorised_values.yaml` | `koha-java-admin` | 1 | Config dictionary |
| `authorised_value_categories.yaml` | `koha-java-admin` | 1 | Config dictionary |
| `authorities.yaml` | `koha-java-catalog` | 1 | Catalog authorities |
| `biblios.yaml` | `koha-java-catalog` | 1 | Bibliographic records |
| `biblios_item_groups.yaml` | `koha-java-catalog` | 1 | Item groups |
| `biblios_merge.yaml` | `koha-java-catalog` | 1 | Biblio merge |
| `bookings.yaml` | `koha-java-circulation` | 2 | Bookings |
| `cash_registers.yaml` | `koha-java-finance` | 2 | POS/cash |
| `checkouts.yaml` | `koha-java-circulation` | 2 | Checkouts/returns |
| `circulation_rules.yaml` | `koha-java-circulation` | 2 | Rules engine |
| `cities.yaml` | `koha-java-admin` | 1 | Branch/city setup |
| `clubs.yaml` | `koha-java-patron` | 1 | Patron clubs |
| `config_file_transports.yaml` | `koha-java-notification` | 2 | Delivery channels |
| `config_smtp_servers.yaml` | `koha-java-notification` | 2 | SMTP config |
| `deleted_biblios.yaml` | `koha-java-catalog` | 1 | Deleted records |
| `erm_agreements.yaml` | `koha-java-erm` | 3 | ERM agreements |
| `erm_config.yaml` | `koha-java-erm` | 3 | ERM config |
| `erm_counter_files.yaml` | `koha-java-erm` | 3 | COUNTER files |
| `erm_counter_logs.yaml` | `koha-java-erm` | 3 | COUNTER logs |
| `erm_counter_registries.yaml` | `koha-java-erm` | 3 | COUNTER registries |
| `erm_custom_reports.yaml` | `koha-java-erm` | 3 | ERM reports |
| `erm_default_usage_reports.yaml` | `koha-java-erm` | 3 | Usage reports |
| `erm_documents.yaml` | `koha-java-erm` | 3 | ERM docs |
| `erm_eholdings_packages.yaml` | `koha-java-erm` | 3 | eHoldings |
| `erm_eholdings_packages_resources.yaml` | `koha-java-erm` | 3 | eHoldings |
| `erm_eholdings_resources.yaml` | `koha-java-erm` | 3 | eHoldings |
| `erm_eholdings_titles.yaml` | `koha-java-erm` | 3 | eHoldings |
| `erm_eholdings_titles_resources.yaml` | `koha-java-erm` | 3 | eHoldings |
| `erm_extended_attribute_types.yaml` | `koha-java-erm` | 3 | ERM attrs |
| `erm_licenses.yaml` | `koha-java-erm` | 3 | Licenses |
| `erm_sushi_services.yaml` | `koha-java-erm` | 3 | SUSHI |
| `erm_usage_databases.yaml` | `koha-java-erm` | 3 | Usage |
| `erm_usage_data_providers.yaml` | `koha-java-erm` | 3 | Usage |
| `erm_usage_items.yaml` | `koha-java-erm` | 3 | Usage |
| `erm_usage_platforms.yaml` | `koha-java-erm` | 3 | Usage |
| `erm_usage_titles.yaml` | `koha-java-erm` | 3 | Usage |
| `erm_users.yaml` | `koha-java-erm` | 3 | ERM users |
| `extended_attribute_types.yaml` | `koha-java-admin` | 1 | Global attribute types |
| `holds.yaml` | `koha-java-holds` | 2 | Holds core |
| `ill_backends.yaml` | `koha-java-ill` | 3 | ILL backend adapters |
| `ill_batches.yaml` | `koha-java-ill` | 3 | ILL batches |
| `ill_batchstatuses.yaml` | `koha-java-ill` | 3 | ILL statuses |
| `ill_requests.yaml` | `koha-java-ill` | 3 | ILL requests |
| `ill_users.yaml` | `koha-java-ill` | 3 | ILL users |
| `import_batches.yaml` | `koha-java-catalog` | 1 | Catalog import |
| `import_batch_profiles.yaml` | `koha-java-catalog` | 1 | Catalog import profiles |
| `items.yaml` | `koha-java-catalog` | 1 | Items |
| `item_types.yaml` | `koha-java-catalog` | 1 | Item types |
| `jobs.yaml` | `koha-java-batch` | 3 | Batch job orchestration |
| `libraries.yaml` | `koha-java-admin` | 1 | Library/branch admin |
| `lists.yaml` | `koha-java-patron` | 1 | Patron/public lists |
| `oauth.yaml` | `koha-java-auth` | 1 | OAuth flows |
| `patrons.yaml` | `koha-java-patron` | 1 | Patron core |
| `patrons_account.yaml` | `koha-java-finance` | 2 | Fines/payments |
| `patrons_checkouts.yaml` | `koha-java-patron` | 1 | Patron view projections |
| `patrons_extended_attributes.yaml` | `koha-java-patron` | 1 | Patron attrs |
| `patrons_holds.yaml` | `koha-java-patron` | 1 | Patron holds view |
| `patrons_hold_groups.yaml` | `koha-java-patron` | 1 | Patron hold groups view |
| `patrons_password.yaml` | `koha-java-patron` | 1 | Patron password workflow |
| `patrons_recalls.yaml` | `koha-java-patron` | 1 | Patron recalls view |
| `patron_categories.yaml` | `koha-java-patron` | 1 | Categories |
| `preservation_config.yaml` | `koha-java-admin` | 1 | Temporary owner until preservation service is split |
| `preservation_processings.yaml` | `koha-java-admin` | 1 | Temporary owner until preservation service is split |
| `preservation_trains.yaml` | `koha-java-admin` | 1 | Temporary owner until preservation service is split |
| `preservation_waiting_list.yaml` | `koha-java-admin` | 1 | Temporary owner until preservation service is split |
| `public_csp_reports.yaml` | `koha-java-opac-gateway` | 3 | Public gateway concern |
| `public_oauth.yaml` | `koha-java-opac-gateway` | 3 | Public auth edge |
| `public_patrons.yaml` | `koha-java-opac-gateway` | 3 | Public patron edge |
| `quotes.yaml` | `koha-java-acquisitions` | 2 | Vendor/quote workflows |
| `record_sources.yaml` | `koha-java-catalog` | 1 | Catalog sources |
| `return_claims.yaml` | `koha-java-circulation` | 2 | Return claims |
| `rotas.yaml` | `koha-java-circulation` | 2 | Rotation/roster routes |
| `search_filters.yaml` | `koha-java-search` | 1 | Search configuration |
| `sip2_accounts.yaml` | `koha-java-admin` | 1 | Interim owner before SIP2 service split |
| `sip2_institutions.yaml` | `koha-java-admin` | 1 | Interim owner before SIP2 service split |
| `sip2_system_preference_overrides.yaml` | `koha-java-admin` | 1 | Interim owner before SIP2 service split |
| `status.yaml` | `koha-java-opac-gateway` | 3 | Edge/system status exposure |
| `suggestions.yaml` | `koha-java-acquisitions` | 2 | Suggestion to order flow |
| `tickets.yaml` | `koha-java-admin` | 1 | Admin/service desk interim owner |
| `transfer_limits.yaml` | `koha-java-admin` | 1 | Branch transfer policy |

## 5) Gaps and Follow-up

- No explicit `serials*.yaml` path file appears in current Swagger path directory; serials implementation will rely on table/business mapping from migration plan and may require new OpenAPI route files.
- Preservation and SIP2 have temporary ownership under `koha-java-admin` until dedicated services are added (as defined in broader migration plan).
- Gateway modules (`koha-java-opac-gateway`, `koha-java-intranet-gateway`) are cross-wave edge modules and should focus on route composition and policy enforcement.

