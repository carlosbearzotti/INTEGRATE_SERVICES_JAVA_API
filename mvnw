#!/usr/bin/env bash
# ----------------------------------------------------------------------------
# Apache Maven Wrapper Executable Script (Linux / macOS)
# ----------------------------------------------------------------------------

set -e

[ -z "$MAVEN_WRAPPER_DEBUG" ] || set -x

# Resolve script directory
PRG="$0"
while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done
scriptDir=`dirname "$PRG"`

# Check Java
if [ -n "$JAVA_HOME" ]; then
  JAVACMD="$JAVA_HOME/bin/java"
else
  JAVACMD="java"
fi

if ! command -v "$JAVACMD" >/dev/null 2>&1; then
  echo "Error: JAVA_HOME is not set and no 'java' command could be found in PATH." >&2
  exit 1
fi

WRAPPER_PROPERTIES="$scriptDir/.mvn/wrapper/maven-wrapper.properties"
if [ ! -f "$WRAPPER_PROPERTIES" ]; then
  echo "Error: $WRAPPER_PROPERTIES not found." >&2
  exit 1
fi

DIST_URL=$(grep -E "^distributionUrl=" "$WRAPPER_PROPERTIES" | cut -d= -f2- | tr -d '\r\n')
if [ -z "$DIST_URL" ]; then
  echo "Error: distributionUrl not found in $WRAPPER_PROPERTIES." >&2
  exit 1
fi

DIST_ZIP_NAME=$(basename "$DIST_URL")
DIST_NAME="${DIST_ZIP_NAME%-bin.zip}"
[ "$DIST_NAME" != "$DIST_ZIP_NAME" ] || DIST_NAME="${DIST_ZIP_NAME%.zip}"

# Calculate SHA-256 hash of URL for caching
if command -v sha256sum >/dev/null 2>&1; then
  HASH=$(printf "%s" "$DIST_URL" | sha256sum | awk '{print $1}')
elif command -v shasum >/dev/null 2>&1; then
  HASH=$(printf "%s" "$DIST_URL" | shasum -a 256 | awk '{print $1}')
else
  HASH="default"
fi

USER_HOME="${MAVEN_USER_HOME:-$HOME/.m2}"
MAVEN_HOME="$USER_HOME/wrapper/dists/$DIST_NAME/$HASH/$DIST_NAME"

# Check if Maven is already downloaded
if [ ! -x "$MAVEN_HOME/bin/mvn" ]; then
  echo "Downloading Maven from $DIST_URL..."
  mkdir -p "$USER_HOME/wrapper/dists/$DIST_NAME/$HASH"
  TMP_ZIP="$USER_HOME/wrapper/dists/$DIST_NAME/$HASH/$DIST_ZIP_NAME"

  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$DIST_URL" -o "$TMP_ZIP"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$DIST_URL" -O "$TMP_ZIP"
  else
    echo "Error: Neither curl nor wget is available to download Maven." >&2
    exit 1
  fi

  echo "Unzipping Maven to $USER_HOME/wrapper/dists/$DIST_NAME/$HASH..."
  if command -v unzip >/dev/null 2>&1; then
    unzip -q -o "$TMP_ZIP" -d "$USER_HOME/wrapper/dists/$DIST_NAME/$HASH"
  else
    python3 -c "import zipfile; zipfile.ZipFile('$TMP_ZIP').extractall('$USER_HOME/wrapper/dists/$DIST_NAME/$HASH')"
  fi
  rm -f "$TMP_ZIP"
  chmod +x "$MAVEN_HOME/bin/mvn"
fi

# Execute Maven
exec "$MAVEN_HOME/bin/mvn" "$@"
