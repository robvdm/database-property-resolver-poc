# Quarkus database-property-resolver-poc

## Notes

* Quarkus uses SmallRye config for it's configuration:
  - https://quarkus.io/guides/config-reference
  - https://github.com/smallrye/smallrye-config
* Config can from multiple sources: config files, environment variables etc. and custom sources like URL's or a Database. Which one will be picked is based on ordinals with a certain weight. No need to merge sources manually.
* A custom resource has to be registered in META-INF/services/io.smallrye.config.ConfigSourceFactory
* At config time not everything is available. A config source based on Panache did not work at that stage (which is logical).
* The Quarkus extensions project has a JDBC based config resource: https://github.com/quarkiverse/quarkus-config-extensions/tree/main/jdbc/runtime/src/main/java/io/quarkiverse/config/jdbc/runtime
* This PoC is based on it with a different database backend and SQL queries
* There is no Spring JDBCTemplate equivalent so you need to do some extra JDBC coding but that's minimal (there is a port with this code is so minimal that it's not worth the effort).

## Usage

Configure database in application.properties:

```
quarkus.datasource.db-kind=postgresql
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/postgres
quarkus.datasource.username=baeldung
quarkus.datasource.password=baeldung
quarkus.hibernate-orm.database.generation=drop-and-create
quarkus.hibernate-orm.sql-load-script = import.sql

rftm.properties.jdbc.username=${quarkus.datasource.username}
rftm.properties.jdbc.password=${quarkus.datasource.password}
rftm.properties.jdbc.url=${quarkus.datasource.jdbc.url}
rftm.properties.jdbc.cache=true
rftm.properties.jdbc.application-name=rabobank-file-transfer-manager-API
```
Configure the greeting message in application.properties:
```
greeting=Hello Orca
```

or in the database:

```
application=as defined in rftm.properties.jdbc.application-name
encrypted=true/false
key=greeting
value=Hello Jaguar
```

Start the application:

```
mvn quarkus:dev
```

Ask for a greeting a see who wins:

```
curl -vs http://localhost:8080/hello/greeting/Rob
```


