Array datatype contributor for Hibernate ORM 5.2
========

If you need to use PostgreSQL arrays in your JPA entities and already use
Hibernate as your JPA provider, this is the plugin for you.


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
        <version>1.0</version>
    </dependency>

Or if you're using Gradle:

    dependencies {
        compile group: 'com.mopano', name: 'hibernate-array-contributor', version: '1.0'
    }


Tested types:
--------

* java.lang.String[] as varchar[] and text[]
* java.time.LocalDate[] as date[]
* java.lang.Long[] as bigint[]
* org.w3c.dom.Document[] as xml[] (support not included in this package)
* javax.json.JsonStructure[] as jsonb[] (support not included in this package)


Known issues:
--------

Hibernate `5.2.9.Final` introduced a regression where your arrays cannot contain null values. This will likely be fixed in `5.2.14.Final` and `5.3.0.Final`.

While you normally can pass nulls inside the arrays, you cannot pass on null objects to array parameters in **native** queries, due to being
unable to determine the type of object and defaulting to Serializable. Technically what
happens is, PreparedStatement.setNull is called with java.sql.Types.VARBINARY as it's
type, since it can't be determined. Changing the BasicBinder to send Types.NULL instead
will fix this problem for Postgres, but other problems will occur in other databases,
and they have existing tests that start to fail if that is changed.
