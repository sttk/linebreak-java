# [linebreak-java][repo-url] [![Maven Central][mvn-img]][mvn-url] [![GitHub.io][io-img]][io-url] [![CI Status][ci-img]][ci-url] [![MIT License][mit-img]][mit-url]

A Java library for breaking a given text into lines within a specified width.

## Install

This package can be installed from [Maven Central Repository][mvn-url].

The examples of declaring that repository and the dependency on this package in Maven `pom.xml` and Gradle `build.gradle` are as follows:

### for Maven

```
  <dependencies>
    <dependency>
      <groupId>io.github.sttk</groupId>
      <artifactId>linebreak</artifactId>
      <version>0.1.0</version>
    </dependency>
  </dependencies>
```

### for Gradle

```
repositories {
  mavenCentral()
}
dependencies {
  implementation 'io.github.sttk:linebreak:0.1.0'
}
```

## Usage

The following code breaks the argument text into lines within the terminal width, and outputs them to stdout.

```
import com.github.sttk.linebreak.LineIter;
...
    var iter = new LineIter(text, Term.getCols());
    iter.setIndent(" ".repeat(4));
    while (iter.hasNext()) {
      System.out.println(iter.next());
    }
```

## Native build

This library supports native build with GraalVM.

See the following pages to setup native build environment on Linux/macOS or Windows.
- [Setup native build environment on Linux/macOS](https://www.graalvm.org/latest/reference-manual/native-image/)
- [Setup native build environment on Windows](https://www.graalvm.org/latest/docs/getting-started/windows/#prerequisites-for-native-image-on-windows)

And see the following pages to build native image with Maven or Gradle.
- [Native image building with Maven plugin](https://graalvm.github.io/native-build-tools/latest/maven-plugin.html)
- [Native image building with Gradle plugin](https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html)

This library uses [ICU4J](https://icu.unicode.org/home) and [JNA](https://github.com/java-native-access/jna).
Since the ICU4J's jar file includes data files, it is required to write those data files in `resource-config.json` which is one of native build configuration files.
And since JNA uses JNI, reflection and so on, it is also required to write configurations into native build configuration files: `jni-config.json`, `proxy-config.json`, `reflect-config.json` and `resource-config.json`.

These configuration files can be generated automatically with [Tracing Agent](https://www.graalvm.org/latest/reference-manual/native-image/metadata/AutomaticMetadataCollection/).
Therefore, the build tool `build.sh` in this library includes an option `trace-test` to run unit tests with the Trace Agent.


## Supporting JDK versions

This framework supports JDK 21 or later.

### Actually checked JDK versions:

- GraalVM CE 21.0.1+12.1 (openjdk version 21.0.1)

## License

Copyright (C) 2023 Takayuki Sato

This program is free software under MIT License.<br>
See the file LICENSE in this distribution for more details.


[repo-url]: https://github.com/sttk/linebreak-java
[mvn-img]: https://img.shields.io/badge/maven_central-0.1.0-276bdd.svg
[mvn-url]: https://central.sonatype.com/artifact/io.github.sttk/linebreak/0.1.0
[io-img]: https://img.shields.io/badge/github.io-Javadoc-4d7a97.svg
[io-url]: https://sttk.github.io/linebreak-java/
[ci-img]: https://github.com/sttk/linebreak-java/actions/workflows/java-ci.yml/badge.svg?branch=main
[ci-url]: https://github.com/sttk/linebreak-java/actions
[mit-img]: https://img.shields.io/badge/license-MIT-green.svg
[mit-url]: https://opensource.org/licenses/MIT
