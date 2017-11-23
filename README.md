# Gradle Spoon Plugin

[![License](https://img.shields.io/badge/license-apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Build Status](https://travis-ci.org/jaredsburrows/gradle-spoon-plugin.svg?branch=master)](https://travis-ci.org/jaredsburrows/gradle-spoon-plugin)
[![Coverage Status](https://coveralls.io/repos/github/jaredsburrows/gradle-spoon-plugin/badge.svg?branch=master)](https://coveralls.io/github/jaredsburrows/gradle-spoon-plugin?branch=master)
[![Twitter Follow](https://img.shields.io/twitter/follow/jaredsburrows.svg?style=social)](https://twitter.com/jaredsburrows)

Gradle plugin for [Spoon](https://github.com/square/spoon) 2+ and [Android Gradle Plugin](https://developer.android.com/studio/releases/gradle-plugin.html) 3+.

## Download

**Release:**
```groovy
buildscript {
  repositories {
    jcenter()
    maven { url "https://oss.sonatype.org/content/repositories/snapshots" } // For Spoon snapshot, until 2.0.0 is released
  }

  dependencies {
    classpath "com.jaredsburrows:gradle-spoon-plugin:1.1.0"
  }
}

apply plugin: "com.android.application"
apply plugin: "com.jaredsburrows.spoon"
```
Release versions are available in the JFrog Bintray repository: https://bintray.com/jaredsburrows/maven/gradle-spoon-plugin

**Snapshot:**
```groovy
buildscript {
  repositories {
    maven { url "https://oss.jfrog.org/artifactory/oss-snapshot-local/" }
    maven { url "https://oss.sonatype.org/content/repositories/snapshots" } // For Spoon snapshot, until 2.0.0 is released
  }

  dependencies {
    classpath "com.jaredsburrows:gradle-spoon-plugin:1.2.0-SNAPSHOT"
  }
}

apply plugin: "com.android.application"
apply plugin: "com.jaredsburrows.spoon"
```
Snapshot versions are available in the JFrog Artifactory repository: https://oss.jfrog.org/webapp/#/builds/gradle-spoon-plugin

## Tasks

- **`gradlew spoon{variant}`**

## Usage

**Optional extension:**
```groovy
spoon {
  // Identifying title for this execution. ("Spoon Execution" by default)
  title = "My tests"
  
  // Path to output directory. ("$buildDir/spoon-output" by default)
  output = "spoonTests"

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
  instrumentationArgs = ["listener com.foo.Listener,com.foo.Listener2", "classLoader com.foo.CustomClassLoader"]

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
  numShards = 1

  // The shardIndex option to specify which shard to run.
  shardIndex = 1
  
  // Run tests in separate instrumentation calls.
  singleInstrumentationCall = true

  // Do not fail build if a test fails, let all the tests run and finish. (false by default)
  ignoreFailures = true
}

dependencies {
  androidTestCompile "com.squareup.spoon:spoon-client:2.0.0-SNAPSHOT"
}
```
