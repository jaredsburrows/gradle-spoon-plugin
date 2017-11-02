# Gradle Spoon Plugin

[![License](https://img.shields.io/badge/license-apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Build Status](https://travis-ci.org/jaredsburrows/gradle-spoon-plugin.svg?branch=master)](https://travis-ci.org/jaredsburrows/gradle-spoon-plugin)
[![Coverage Status](https://coveralls.io/repos/github/jaredsburrows/gradle-spoon-plugin/badge.svg?branch=master)](https://coveralls.io/github/jaredsburrows/gradle-spoon-plugin?branch=master)
[![Twitter Follow](https://img.shields.io/twitter/follow/jaredsburrows.svg?style=social)](https://twitter.com/jaredsburrows)

Gradle plugin for [Spoon](https://github.com/square/spoon).

## Download

**Release:**
```groovy
buildscript {
  repositories {
    jcenter()
  }

  dependencies {
    classpath "com.jaredsburrows:gradle-spoon-plugin:0.1.0"
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
  }

  dependencies {
    classpath "com.jaredsburrows:gradle-spoon-plugin:0.1.0-SNAPSHOT"
  }
}

apply plugin: "com.android.application"
apply plugin: "com.jaredsburrows.spoon"
```
Snapshot versions are available in the JFrog Artifactory repository: https://oss.jfrog.org/webapp/#/builds/gradle-spoon-plugin

## Tasks

- **`spoon{variant}`**

## Usage

### How to use it
TODO
