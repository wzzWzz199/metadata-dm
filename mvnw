#!/usr/bin/env bash
set -euo pipefail

if command -v mvn >/dev/null 2>&1; then
  exec mvn "$@"
else
  echo "Maven is required to run this project. Please install Apache Maven or update mvnw to point to a bundled distribution." >&2
  exit 1
fi
