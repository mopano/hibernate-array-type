Array datatype contributor for Hibernate ORM 5.2
========

If you need to use PostgreSQL arrays in your JPA entities and already use
Hibernate as your JPA provider, this is the plugin for you.

One intentional limitation is that you cannot use arrays of primitives.
This is because `java.sql.Connection.createArrayOf` requires an array of objects.
While conversion is doable, it's undesirable.

Set-up for testing environment:
--------

* PostgreSQL 9 server or newer, running on port 5432
* Username, password and database `hibernate_orm_test` with full privileges that database.

Compiling:
--------

    ./gradlew clean build

If you want to build without running the tests, use `assemble` instead of `build`.

To make the final jar available for your Maven or compatible project type
that uses the Maven repository, run:

    ./gradlew publishToMavenLocal

Now you just need to add the dependency to your project's `pom.xml`.

    <dependency>
        <groupId>com.mopano</groupId>
        <artifactId>hibernate-array-contributor</artifactId>
        <version>1.2</version>
    </dependency>

Or if you're using Gradle:

    dependencies {
        compile group: 'com.mopano', name: 'hibernate-array-contributor', version: '1.2'
    }

Changelog:
--------
### version 1.2
 * added `java.util.UUID` conversion using Postgres-specific type handler.
 * fixed `java.lang.Class` conversion.
 * fixed `java.time.Duration` conversion.
 * fixed `java.time.OffsetTime` conversion.
 * `hibernate.arrays.uuidtype` JPA configuration property can be set to override the default PostgresUUID array type handler.

### version 1.1
 * Miscelaneous internal changes.

Tested working types as of version 1.2:
--------

* `java.lang.String[]` as `varchar[]` and `text[]`
* `java.lang.Long[]` as `bigint[]`
* `java.lang.Integer[]` as `integer[]`
* `java.lang.Short[]` as `smallint[]`
* `java.lang.Boolean[]` as `boolean[]`
* `java.lang.Float[]` as `real[]`
* `java.lang.Double[]` as `double precision[]`
* `java.lang.Class[]` as `varchar[]` and `text[]`
* `java.math.BigDecimal[]` as `numeric[]`
* `java.math.BigInteger[]` as `numeric[]`
* `java.net.URL[]` as `varchar[]` and `text[]`
* `java.util.Currency[]` as `varchar[]`
* `java.util.Locale[]` as `varchar[]`
* `java.util.UUID[]` as `uuid[]`
* `java.time.Instant[]` as `timestamptz[]`
* `java.time.Duration[]` as `bigint[]`
* `java.time.LocalDate[]` as `date[]`
* `java.time.LocalTime[]` as `time[]`
* `java.time.LocalDateTime[]` as `timestamp[]`
* `java.time.ZonedDateTime[]` as `timestamptz[]`. Warning: Offset is not precisely preserved within the database itself. If you are not running the server and your java application in UTC, then `equals(Object other)` is false, but `isEqual(ChronoZonedDateTime<?> other)` is true. It is fine with calculations, comparisons and conversions, if you are aware of this.
* `java.time.OffsetDateTime[]` as `timestamptz[]`. Warning: Offset is not precisely preserved within the database itself. If you are not running the server and your java application in UTC, then `equals(Object other)` is false, but `isEqual(OffsetDateTime other)` is true. It is fine with calculations, comparisons and conversions, if you are aware of this.
* `java.time.OffsetTime[]` as `timetz[]`. Warning: Offset is not precisely preserved within the database itself. If you are not running the server and your java application in UTC, then `equals(Object other)` is false, but `isEqual(OffsetTime other)` is true. It is fine with calculations, comparisons and conversions, if you are aware of this.
* `org.w3c.dom.Document[]` as `xml[]` (support not included in this package)
* `javax.json.JsonStructure[]` as `jsonb[]` (support not included in this package)

Types that don't work:
--------

* `java.util.TimeZone` Data becomes mangled.
* `java.sql.Date` Reflection type detection failure. Special case could be written, but type is deprecated. Just use `java.time.LocalDate`.
* `java.sql.Time`  Reflection type detection failure. Special case could be written, but type is deprecated. Just use `java.time.LocalTime`.
* `java.sql.Timestamp`  Reflection type detection failure. Special case could be written, but type is deprecated. Just use `java.time.Instant`.

Types that remain untested and likely don't work:
--------

* `java.util.Calendar`. Use the `java.time.*` classes instead.
* `java.util.Date`. Use the `java.time.*` classes instead.
* Numeric boolean types.
* Yes/no boolean types.
* True/false textual boolean types.
* All binary types.
* All CLOB/BLOB types.
* Char array type. If you even try to use that instead of `varchar` or `text` you might be crazy.
* National char/string/text types. The only major databases that support these don't support arrays at this time.

Known issues:
--------

Hibernate `5.2.9.Final` introduced a regression where your arrays cannot contain null values. This is fixed in `5.2.14.Final` and `5.3.0.Final`.
