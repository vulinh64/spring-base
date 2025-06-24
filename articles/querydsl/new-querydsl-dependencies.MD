> Thanks to this [comment](https://github.com/querydsl/querydsl/issues/3131#issuecomment-1549255382) that QueryDSL can
> now be used without the outdated `com.mysema.maven:apt-maven-plugin` maven plugin.

You now only have to include this line in `org.apache.maven.plugins:maven-compiler-plugin` to make QueryDSL APT work:

```xml

<path>
    <groupId>jakarta.persistence</groupId>
    <artifactId>jakarta.persistence-api</artifactId>
    <version>${jakarta-persistence.version}</version>
</path>
```

Therefore, a proper maven configuration will now look something like this:

```xml

<project>
    <!-- Other configurations above -->

    <build>
        <plugins>
            <!-- Other plugins here -->

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                    <annotationProcessorPaths>
                        <!-- Other annotation processors here -->

                        <path>
                            <groupId>com.querydsl</groupId>
                            <artifactId>querydsl-apt</artifactId>
                            <version>${querydsl.version}</version>
                            <!-- Use jakarta classifier if you are using Spring Boot 3+ -->
                            <classifier>jakarta</classifier>
                        </path>
                        <!-- Older version might have to use javax.persistence instead -->
                        <path>
                            <groupId>jakarta.persistence</groupId>
                            <artifactId>jakarta.persistence-api</artifactId>
                            <version>${jakarta-persistence.version}</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

You can copy/paste this short version:

```xml

<annotationProcessorPaths>
    <path>
        <groupId>com.querydsl</groupId>
        <artifactId>querydsl-apt</artifactId>
        <version>${querydsl.version}</version>
        <classifier>jakarta</classifier>
    </path>
    <path>
        <groupId>jakarta.persistence</groupId>
        <artifactId>jakarta.persistence-api</artifactId>
        <version>${jakarta-persistence.version}</version>
    </path>
</annotationProcessorPaths>
```

You can either use `maven clean compile` or `Build -> Rebuild Project` in IntelliJ IDEA (you might need to add
`target/generated-sources/annotations` as a source folder in the project configuration).