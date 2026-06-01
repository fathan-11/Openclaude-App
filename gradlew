#!/bin/sh
# Gradle wrapper stub - downloads and runs Gradle
GRADLE_VERSION=8.5
GRADLE_DIR="$HOME/.gradle/wrapper/dists/gradle-${GRADLE_VERSION}-bin"
if [ ! -d "$GRADLE_DIR" ]; then
    echo "Downloading Gradle $GRADLE_VERSION..."
    mkdir -p "$GRADLE_DIR"
    curl -sL "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -o /tmp/gradle.zip
    unzip -q /tmp/gradle.zip -d "$GRADLE_DIR"
fi
exec "$GRADLE_DIR/gradle-${GRADLE_VERSION}/bin/gradle" "$@"
