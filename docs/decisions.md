# Architecture & Version Decisions

## Pinned Versions (resolved from Maven Central, 2026-11-16)

| Library | GroupId | ArtifactId | Version | Reason |
|---|---|---|---|---|
| Struts 2 | org.apache.struts | struts2-core | **6.7.0** | Latest 6.x with javax.* namespace; 6.12.0 exists but 6.7.0 is the last version confirmed stable on Tomcat 9 by the community. Using 6.7.0 as a safe choice. |
| Hibernate ORM | org.hibernate | hibernate-core | **5.6.15.Final** | Last 5.6.x release; uses javax.persistence, required for Tomcat 9. Hibernate 6+ uses jakarta.* |
| hibernate-hikaricp | org.hibernate | hibernate-hikaricp | **5.6.15.Final** | Same stream as hibernate-core; bundles HikariCP |
| MySQL Connector/J | com.mysql | mysql-connector-j | **8.4.0** | Confirmed in existing tp4 project; supports MySQL 8 |
| jBCrypt | org.mindrot | jbcrypt | **0.4** | Stable, no updates needed |
| JSTL | javax.servlet | jstl | **1.2** | JSTL 1.2 for Tomcat 9/JSP 2.3 |
| Servlet API | javax.servlet | javax.servlet-api | **4.0.1** | Provided by Tomcat 9 |
| JSP API | javax.servlet.jsp | javax.servlet.jsp-api | **2.3.3** | Provided by Tomcat 9 |
| JUnit 5 | org.junit.jupiter | junit-jupiter | **5.11.4** | Latest JUnit 5 |
| Log4j2 | org.apache.logging.log4j | log4j-core | **2.26.1** | Latest stable Log4j2 |
| SLF4J | org.apache.logging.log4j | log4j-slf4j2-impl | **2.26.1** | SLF4J bridge for Log4j2 |
| Smile (ML) | com.github.haifengl | smile-core | **3.1.0** | Last version supporting JDK 17; 4.x requires Java 21+ |
| Maven Compiler | org.apache.maven.plugins | maven-compiler-plugin | **3.13.0** | Matches existing TP projects |
| Maven WAR | org.apache.maven.plugins | maven-war-plugin | **3.4.0** | Stable, produces .war |
| Maven Surefire | org.apache.maven.plugins | maven-surefire-plugin | **3.5.2** | JUnit 5 support |

## Architectural Decisions

### AD-01: Composition Root Pattern
**Decision:** `AppInitListener` is the only class in `web` that knows `*Impl` classes. All other actions use `AppContext.get(IXxxMetier.class)`.
**Reason:** Enforces T1 layer separation. Metier implementations can be swapped without touching actions.

### AD-02: Transaction boundary at metier layer
**Decision:** `ITransactionRunner` is declared in `dao`, implemented in `dao.impl`, injected into metier. DAOs never commit.
**Reason:** Business operations (conflict-check + insert reservation) must be atomic. Commit/rollback belongs to the business layer, not the DAO.

### AD-03: Lazy collection strategy
**Decision:** All `@OneToMany` are LAZY by default. Actions call metier methods that use `join fetch` or map to DTOs before the session closes.
**Reason:** Prevents LazyInitializationException from JSPs. T1 rule: metier, not JSP, determines what is loaded.

### AD-04: Conflict detection overlap formula
**Decision:** Two periods [s1,e1] and [s2,e2] overlap iff `s1 <= e2 AND e1 >= s2` (inclusive on both ends).
**Reason:** Brief says "any day of the requested period". Same-day turnarounds (return day = new pickup day) are considered non-overlapping per standard rental convention, but overdue vehicles use `max(plannedEnd, today)` as effective end.

### AD-05: Invoice formula
- `rentalDays = max(1, daysBetween(start, plannedEnd))`
- `lateDays = max(0, daysBetween(plannedEnd, actualReturn))`
- `base = rentalDays × dailyRateSnapshot`
- `equipment = rentalDays × equipmentDailyTotalSnapshot`
- `lateFee = lateDays × dailyRateSnapshot × lateFeeRate` (lateFeeRate e.g. 1.5)
- `subtotal = base + equipment + lateFee`; `vat = subtotal × vatRate`; `total = subtotal + vat`
- Early return does NOT reduce the charge (billed on planned period).
**Reason:** Simplest reading of the brief. Documented to defend at review.

### AD-06: Smile 3.1.0 for ML
**Decision:** Use `smile-core:3.1.0` for the risk model (Random Forest classifier).
**Reason:** Smile 4.x requires Java 21+; 3.1.0 is the last JDK 17-compatible release. Confirmed by project maintainer docs.

### AD-07: hibernate.hbm2ddl.auto=validate for production runs
**Decision:** Schema is managed by `sql/schema.sql`; Hibernate only validates mappings, never creates/updates tables.
**Reason:** Prevents silent schema drift. DDL is explicit and version-controlled.

### AD-08: ML model status when untrained
**Decision:** `IRiskMetier.status()` returns `ModelStatus.NOT_TRAINED` at startup if no model file exists. JSPs show a neutral "no model yet" badge.
**Reason:** App must not crash if ML has never been trained. Separation of concern.

### AD-09: BCrypt work factor
**Decision:** BCrypt strength factor 12.
**Reason:** Industry recommendation for 2024+; acceptable latency (~100ms) on login, negligible on register.

### AD-10: Session fixation prevention
**Decision:** At login success, store new session attributes then call `session.invalidate()` and create a new session, transferring only the user object.
**Reason:** Prevents session fixation attacks (security checklist from SKILL.md section 9).
