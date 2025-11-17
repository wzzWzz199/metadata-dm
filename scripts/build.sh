#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
CLIENT_DIR="$ROOT_DIR/client"

cd "$CLIENT_DIR"
npm install
npm run build

cd "$ROOT_DIR"
./mvnw -pl server -am clean package
