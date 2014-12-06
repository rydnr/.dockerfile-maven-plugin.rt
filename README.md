dockerfile-maven-plugin
=======================

A Maven plugin to generate Dockerfiles based on templates.

= Usage =

== pom.xml ==

Add the following plugin declaration to your pom.xml:

```
  <build>
    [..]
    <plugin>
      <groupId>org.acmsl</groupId>
      <artifactId>dockerfile-maven-plugin</artifactId>
      <version>latest-SNAPSHOT</version>
      <configuration>
        <outputDir>${project.build.outputDirectory}/META-INF/</outputDir>
        <template>${project.basedir}/src/main/assembly/Dockerfile.stg</template>
      </configuration>
    </plugin>
    [..]
  </build>
```

== Dockerfile.stg ==

Define your Dockerfile template.
You can use the following to customize yours:
```
group Dockerfile;

source(C) ::= <<
<!
  Defines the rules to generate Dockerfile files.
  @param C the context.
!>
<dockerfile(C=C, pom=C.P)>
>>

dockerfile(C, pom) ::= <<
<!
  Generates a custom Dockerfile.
  @param C the context.
  @param pom the pom.
!>

>>
```