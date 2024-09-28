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