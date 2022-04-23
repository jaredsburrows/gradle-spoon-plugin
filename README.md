# Gradle Spoon Plugin

[![License](https://img.shields.io/badge/license-apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Build](https://github.com/jaredsburrows/gradle-spoon-plugin/actions/workflows/build.yml/badge.svg)](https://github.com/jaredsburrows/gradle-spoon-plugin/actions/workflows/build.yml)
[![Twitter Follow](https://img.shields.io/twitter/follow/jaredsburrows.svg?style=social)](https://twitter.com/jaredsburrows)

Gradle plugin for [Spoon](https://github.com/square/spoon) 2+ and [Android Gradle Plugin](https://developer.android.com/studio/releases/gradle-plugin.html) 3+.

## Download

**Release:**
```groovy
buildscript {
  repositories {
    mavenCentral()
    // For Spoon snapshot, until 2.0.0 is released
    maven { url 'https://oss.sonatype.org/content/repositories/snapshots' }
  }

  dependencies {
    classpath 'com.jaredsburrows:gradle-spoon-plugin:1.5.1'
  }
}

repositories {
  // For Spoon snapshot, until 2.0.0 is released
  maven { url 'https://oss.sonatype.org/content/repositories/snapshots' }
}

apply plugin: 'com.android.application'
apply plugin: 'com.jaredsburrows.spoon'

dependencies {
  // For Spoon snapshot, until 2.0.0 is released
  androidTestCompile 'com.squareup.spoon:spoon-client:2.0.0-SNAPSHOT'
}
```
Release versions are available in the [Sonatype's release repository](https://repo1.maven.org/maven2/com/jaredsburrows/gradle-spoon-plugin/).

**Snapshot:**
```groovy
buildscript {
  repositories {
    maven { url 'https://oss.jfrog.org/artifactory/oss-snapshot-local/' }
    // For Spoon snapshot, until 2.0.0 is released
    maven { url 'https://oss.sonatype.org/content/repositories/snapshots' }
  }

  dependencies {
    classpath 'com.jaredsburrows:gradle-spoon-plugin:1.6.0-SNAPSHOT'
  }
}

repositories {
  // For Spoon snapshot, until 2.0.0 is released
  maven { url 'https://oss.sonatype.org/content/repositories/snapshots' }
}

apply plugin: 'com.android.application'
apply plugin: 'com.jaredsburrows.spoon'

dependencies {
  // For Spoon snapshot, until 2.0.0 is released
  androidTestCompile 'com.squareup.spoon:spoon-client:2.0.0-SNAPSHOT'
}
```
Snapshot versions are available in the [Sonatype's snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/com/jaredsburrows/gradle-spoon-plugin/).

**Library modules:**

This plugin allows Spoon to be run on library modules too!

```groovy
apply plugin: 'com.android.library'
apply plugin: 'com.jaredsburrows.spoon'

dependencies {
  androidTestCompile 'com.squareup.spoon:spoon-client:2.0.0-SNAPSHOT' // For Spoon snapshot, until 2.0.0 is released
}
```

## Tasks

Entire project:
- **`gradlew spoon{variant}`**

or per module:
- **`gradlew app:spoon{variant}`**
- **`gradlew library:spoon{variant}`**

## Usage

**Optional extension:**
```groovy
spoon {
  // Identifying title for this execution. ("Spoon Execution" by default)
  title = "My tests"
  
  // Path to output directory. ("$buildDir/spoon-output" by default)
  baseOutputDir = "spoonTests"

  // Whether or not debug logging is enabled. (false by default)
  debug = true

  // Whether or not animations are enabled. Disable animated gif generation. (false by default)
  noAnimations = true

  // Set ADB timeout. (minutes) (default is 10 mins)
  adbTimeout = 5

  // Add device serials for test execution
  devices = ["emulator-5554", "emulator-5556"]

  // Add device serials for skipping test execution.
  skipDevices = ["emulator-5555"]

  // Extra arguments to pass to instrumentation.
  instrumentationArgs = ["listener:com.foo.Listener,com.foo.Listener2", "classLoader:com.foo.CustomClassLoader"]

  // Test class name to run (fully-qualified).
  className = "com.android.foo.FooClassName"
  
  // Run annotated tests - small, medium, large
  testSize = "large"
  
  // Allow no devices to be connected. (false by default)
  allowNoDevices = true

  // Execute the tests device by device. (false by default)
  sequential = true

  // Grant all runtime permissions during installation on Marshmallow and above devices. (false by default)
  grantAll = true

  // Test method name to run (must also use className)
  methodName = "testMethodName"

  // Code coverage flag. For Spoon to calculate coverage file your app must have the `WRITE_EXTERNAL_STORAGE` permission. (false by default)
  codeCoverage = true

  // Toggle sharding. (false by default)
  shard = true

  // The number of separate shards to create.
  numShards = 10

  // The shardIndex option to specify which shard to run.
  shardIndex = 2
  
  // Run tests in separate instrumentation calls.
  singleInstrumentationCall = true

  // Do not fail build if a test fails, let all the tests run and finish. (false by default)
  ignoreFailures = true

  // Clear app data before each test. (false by default)
  clearAppDataBeforeEachTest = true
}
```

## License
```
Copyright (C) 2017 Jared Burrows

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
