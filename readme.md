based on https://cap.cloud.sap/docs/java/getting-started

```
npm install @sap/cds-dk -g
```

```
npm i
```

***How it was generated

```
git clone https://github.com/vomav/gebit
```
```
cds init gebit --add java
```
```
npm add @cap-js/postgres
```

```
cds add postgres
```

```
cds add liquibase 
```

**inside parent/pom.xml
replace cds.build goal
```
					<execution>
						<id>cds.build</id>
						<goals>
							<goal>cds</goal>
						</goals>
						<configuration>
							<commands>
								<command>build --for java</command>
								<command>deploy --to postgres --dry &gt; "${project.basedir}/src/main/resources/db/changelog/dev/schema.sql"</command>
								<command>deploy --model-only --dry &gt; "${project.basedir}/src/main/resources/db/changelog/dev/csn.json"</command>
								<!-- <command>compile srv/service.cds -2 openapi openapi:url /odata/v4/srv.review &gt; ${project.basedir}/src/main/resources/swagger/openapi.json</command> -->
							</commands>
						</configuration>
					</execution>
```

then add this build plugin
```
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>build-helper-maven-plugin</artifactId>
        <version>3.6.0</version>
        <executions>
          <execution>
            <id>add-source</id>
            <phase>generate-sources</phase>
            <goals>
              <goal>add-source</goal>
            </goals>
            <configuration>
              <sources>
                <source>src/gen/java</source>
              </sources>
            </configuration>
          </execution>
        </executions>
      </plugin>
```

create directory dev in srv/src/main/resources/db/changelog

compile delta

https://cap.cloud.sap/docs/guides/databases-postgres#generate-scripts
```
cds deploy --dry --delta-from cds-model.csn > delta.sql
```

```CDS
annotation SRID : Integer;

context cds_spatial // cds.spatial
{
    type Geography : String @odata.Type : 'Edm.Geography';
    type GeographyCollection : String @odata.Type : 'Edm.GeographyCollection';
    type GeographyLineString : String @odata.Type : 'Edm.GeographyLineString';
    type GeographyMultiLineString : String @odata.Type : 'Edm.GeographyMultiLineString';
    type GeographyMultiPoint : String @odata.Type : 'Edm.GeographyMultiPoint';
    type GeographyMultiPolygon : String @odata.Type : 'Edm.GeographyMultiPolygon';
    type GeographyPoint : String @odata.Type : 'Edm.GeographyPoint';
    type GeographyPolygon : String @odata.Type : 'Edm.GeographyPolygon';
    type Geometry : String @odata.Type : 'Edm.Geometry';
    type GeometryCollection : String @odata.Type : 'Edm.GeometryCollection';
    type GeometryLineString : String @odata.Type : 'Edm.GeometryLineString';
    type GeometryMultiLineString : String @odata.Type : 'Edm.GeometryMultiLineString';
    type GeometryMultiPoint : String @odata.Type : 'Edm.GeometryMultiPoint';
    type GeometryMultiPolygon : String @odata.Type : 'Edm.GeometryMultiPolygon';
    type GeometryPoint : String @odata.Type : 'Edm.GeometryPoint';
    type GeometryPolygon : String @odata.Type : 'Edm.GeometryPolygon';
}
```
https://cap.cloud.sap/docs/java/working-with-cql/query-api#copying-modifying-cql-statements


## Start gebit in local docker container
```
cd docker/localhost
```

```
docker compose build
```

```
docker compose -f docker-compose.yml up -d
```


```
cds deploy --profile pg --dry --delta-from srv/src/main/resources/db/changelog/v1/model.csn --out srv/src/main/resources/db/changelog/v2/model.sql
```