# UTILS by RunedUniverse
The Utils Repo contains all commonly used Utils of RunedUniverse.

Each artifact starts with the name of the programming language followed by its respective name:

## Distribution

### Maven Repository
#### RunedUniverse: Development
> This contains our locally hosted development artifacts.<br>

```xml
<repository>
  <id>runeduniverse-development</id>
  <url>https://nexus.runeduniverse.net/repository/maven-development/</url>
</repository>
```

### Dependencies

#### [Maven] Java Utils - Bill of Materials
[![Maven Central](https://img.shields.io/maven-central/v/net.runeduniverse.lib.utils/utils-bom.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.runeduniverse.lib.utils%22%20AND%20a:%22utils-bom%22)

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>net.runeduniverse.lib.utils</groupId>
      <artifactId>utils-bom</artifactId>
      <version>1.0.1</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

#### [Maven] Java Utils Async
[![Maven Central](https://img.shields.io/maven-central/v/net.runeduniverse.lib.utils/utils-async.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.runeduniverse.lib.utils%22%20AND%20a:%22utils-async%22)

```xml
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-async</artifactId>
  <version>2.1.1</version>
</dependency>
```

#### [Maven] Java Chain Library
> Provides a versatile library for building modular "Chains" by utilizing static methods

[![Maven Central](https://img.shields.io/maven-central/v/net.runeduniverse.lib.utils/utils-chain.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.runeduniverse.lib.utils%22%20AND%20a:%22utils-chain%22)

```xml
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-chain</artifactId>
  <version>1.0.1</version>
</dependency>
```

#### [Maven] Java Utils Common
[![Maven Central](https://img.shields.io/maven-central/v/net.runeduniverse.lib.utils/utils-common.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.runeduniverse.lib.utils%22%20AND%20a:%22utils-common%22)

```xml
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-common</artifactId>
  <version>1.1.0</version>
</dependency>
```

#### [Maven] Java Error Handling Library
[![Maven Central](https://img.shields.io/maven-central/v/net.runeduniverse.lib.utils/utils-errors.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.runeduniverse.lib.utils%22%20AND%20a:%22utils-errors%22)

```xml
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-errors</artifactId>
  <version>1.0.1</version>
</dependency>
```

#### [Maven] Java Logging Tools
[![Maven Central](https://img.shields.io/maven-central/v/net.runeduniverse.lib.utils/utils-logging.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.runeduniverse.lib.utils%22%20AND%20a:%22utils-logging%22)

```xml
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-logging</artifactId>
  <version>1.0.2</version>
</dependency>
```

#### [Maven] Java Scanner
> Provides a versatile PackageScanner for collecting Classes

[![Maven Central](https://img.shields.io/maven-central/v/net.runeduniverse.lib.utils/utils-scanner.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.runeduniverse.lib.utils%22%20AND%20a:%22utils-scanner%22)

```xml
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-scanner</artifactId>
  <version>1.1.0</version>
</dependency>
```

