package com.jaredsburrows.spoon

import org.gradle.api.UncheckedIOException
import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException

/**
 * Renders information in a format suitable for logging to the console.
 *
 * Taken from: https://github.com/gradle/gradle/blob/master/subprojects/logging/src/main/java/org/gradle/internal/logging/ConsoleRenderer.java
 */
object ConsoleRenderer {
  /**
   * Renders a path name as a file URL that is likely recognized by consoles.
   */
  fun asClickableFileUrl(path: File): String {
    // File.toURI().toString() leads to an URL like this on Mac: file:/reports/index.html
    // This URL is not recognized by the Mac console (too few leading slashes). We solve
    // this be creating an URI with an empty authority.
    try {
      return URI("file", "", path.toURI().path, null, null).toString()
    } catch (e: URISyntaxException) {
      throw UncheckedException.throwAsUncheckedException(e)
    }
  }
}

/**
 * Wraps a checked exception. Carries no other context.
 *
 * Taken from: https://github.com/gradle/gradle/blob/master/subprojects/base-services/src/main/java/org/gradle/internal/UncheckedException.java
 */
class UncheckedException : RuntimeException {
  constructor(cause: Throwable) : super(cause)
  constructor(message: String, cause: Throwable) : super(message, cause)

  companion object {
    /**
     * Note: always throws the failure in some form. The return value is to keep the compiler happy.
     */
    @JvmOverloads fun throwAsUncheckedException(
      throwable: Throwable,
      preserveMessage: Boolean = false
    ): RuntimeException {
      if (throwable is RuntimeException) {
        throw throwable
      }

      if (throwable is Error) {
        throw throwable
      }

      if (throwable is IOException) {
        if (preserveMessage) {
          throw UncheckedIOException(throwable.message.orEmpty(), throwable)
        } else {
          throw UncheckedIOException(throwable)
        }
      }

      if (preserveMessage) {
        throw UncheckedException(throwable.message.orEmpty(), throwable)
      } else {
        throw UncheckedException(throwable)
      }
    }
  }
}
