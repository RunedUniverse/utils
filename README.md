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
[![MvnRepository](https://badges.mvnrepository.com/badge/net.runeduniverse.lib.utils/utils-bom/badge.svg?label=MvnRepository)](https://mvnrepository.com/artifact/net.runeduniverse.lib.utils/utils-bom)

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>net.runeduniverse.lib.utils</groupId>
      <artifactId>utils-bom</artifactId>
      <version>2.0.0</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

#### [Maven] Java Utils Async
[![MvnRepository](https://badges.mvnrepository.com/badge/net.runeduniverse.lib.utils/utils-async/badge.svg?label=MvnRepository)](https://mvnrepository.com/artifact/net.runeduniverse.lib.utils/utils-async)

```xml
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-async-api</artifactId>
</dependency>
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-async</artifactId>
</dependency>
```

#### [Maven] Java Chain Library
> Provides a versatile library for building modular "Chains" by utilizing static methods

[![MvnRepository](https://badges.mvnrepository.com/badge/net.runeduniverse.lib.utils/utils-chain/badge.svg?label=MvnRepository)](https://mvnrepository.com/artifact/net.runeduniverse.lib.utils/utils-chain)

```xml
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-chain-api</artifactId>
</dependency>
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-chain</artifactId>
</dependency>
```

#### [Maven] Java Utils Common
[![MvnRepository](https://badges.mvnrepository.com/badge/net.runeduniverse.lib.utils/utils-common/badge.svg?label=MvnRepository)](https://mvnrepository.com/artifact/net.runeduniverse.lib.utils/utils-common)

```xml
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-common-api</artifactId>
</dependency>
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-common</artifactId>
</dependency>
```

#### [Maven] Java Conditional Utils
[![MvnRepository](https://badges.mvnrepository.com/badge/net.runeduniverse.lib.utils/utils-conditional/badge.svg?label=MvnRepository)](https://mvnrepository.com/artifact/net.runeduniverse.lib.utils/utils-conditional)

```xml
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-conditional-api</artifactId>
</dependency>
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-conditional</artifactId>
</dependency>
```

#### [Maven] Java Config Utils
[![MvnRepository](https://badges.mvnrepository.com/badge/net.runeduniverse.lib.utils/utils-config/badge.svg?label=MvnRepository)](https://mvnrepository.com/artifact/net.runeduniverse.lib.utils/utils-config)

```xml
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-config-api</artifactId>
</dependency>
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-config</artifactId>
</dependency>
```

#### [Maven] Java Error Handling Library
[![MvnRepository](https://badges.mvnrepository.com/badge/net.runeduniverse.lib.utils/utils-errors/badge.svg?label=MvnRepository)](https://mvnrepository.com/artifact/net.runeduniverse.lib.utils/utils-errors)

```xml
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-errors</artifactId>
</dependency>
```

#### [Maven] Java Logging Tools
[![MvnRepository](https://badges.mvnrepository.com/badge/net.runeduniverse.lib.utils/utils-logging/badge.svg?label=MvnRepository)](https://mvnrepository.com/artifact/net.runeduniverse.lib.utils/utils-logging)

```xml
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-logging-api</artifactId>
</dependency>
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-logging</artifactId>
</dependency>
```

#### [Maven] Java Maven3 Utils
[![MvnRepository](https://badges.mvnrepository.com/badge/net.runeduniverse.lib.utils/utils-maven3/badge.svg?label=MvnRepository)](https://mvnrepository.com/artifact/net.runeduniverse.lib.utils/utils-maven3)

```xml
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-maven3-api</artifactId>
</dependency>
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-maven3</artifactId>
</dependency>
```

#### [Maven] Java Maven3 Extension Utils
[![MvnRepository](https://badges.mvnrepository.com/badge/net.runeduniverse.lib.utils/utils-maven3-ext/badge.svg?label=MvnRepository)](https://mvnrepository.com/artifact/net.runeduniverse.lib.utils/utils-maven3-ext)

```xml
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-maven3-ext-api</artifactId>
</dependency>
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-maven3-ext</artifactId>
</dependency>
```

#### [Maven] Java Network Library
[![MvnRepository](https://badges.mvnrepository.com/badge/net.runeduniverse.lib.utils/utils-net/badge.svg?label=MvnRepository)](https://mvnrepository.com/artifact/net.runeduniverse.lib.utils/utils-net)

```xml
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-net-api</artifactId>
</dependency>
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-net</artifactId>
</dependency>
```

#### [Maven] Java Plexus Utils
[![MvnRepository](https://badges.mvnrepository.com/badge/net.runeduniverse.lib.utils/utils-plexus/badge.svg?label=MvnRepository)](https://mvnrepository.com/artifact/net.runeduniverse.lib.utils/utils-plexus)

```xml
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-plexus</artifactId>
</dependency>
```

#### [Maven] Java Scanner
> Provides a versatile PackageScanner for collecting Classes

[![MvnRepository](https://badges.mvnrepository.com/badge/net.runeduniverse.lib.utils/utils-scanner/badge.svg?label=MvnRepository)](https://mvnrepository.com/artifact/net.runeduniverse.lib.utils/utils-scanner)

```xml
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-scanner-api</artifactId>
</dependency>
<dependency>
  <groupId>net.runeduniverse.lib.utils</groupId>
  <artifactId>utils-scanner</artifactId>
</dependency>
```
