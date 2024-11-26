# Task description

Write a simple UI that displays weather information in a list from a hard-coded set
of cities. Selecting an item in the list presents a detailed view of the city’s weather.

# Solution

App is using clean code approach built on the top of MVVM. App is targeting API 35 (Android 15).
For storing data and managing city items I was using DataStorePreferences.

Project was written in Kotlin.

For documentation i was using Dokka.
Documentation can be build with executing gradlew task 'dokkaHtml'.

[Main screen](app/src/main/java/com/snapone/weatherproject/WeatherApplication.kt)
[Add city screen](app/src/main/java/com/snapone/weatherproject/AddCityFragment.kt)

# Setup

App is using next frameworks:

• Retrofit
• Glide
• Preferences Data Store


# Known build problem ⚠️⚠️⚠️

An exception occurred applying plugin request [id: 'com.android.application']
> Failed to apply plugin 'com.android.internal.application'.
> Android Gradle plugin requires Java 11 to run. You are currently using Java 1.8. You can try some of the following options:

- changing the IDE settings.
- changing the JAVA_HOME environment variable.
- changing `org.gradle.java.home` in `gradle.properties`.

✅ AndroidStudio -> File -> Project Structure -> SDK Location -> Gradle settings -> select at least Java 11

# Possible improvements

1. present data by days?
2. ?
