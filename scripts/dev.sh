#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
CLIENT_DIR="$ROOT_DIR/client"

if [ ! -d "$CLIENT_DIR/node_modules" ]; then
  (cd "$CLIENT_DIR" && npm install)
fi

cleanup() {
  local pids
  pids=$(jobs -pr 2>/dev/null || true)
  if [ -n "$pids" ]; then
    kill $pids >/dev/null 2>&1 || true
  fi
}
trap cleanup EXIT

(
  cd "$ROOT_DIR"
  ./mvnw -pl server -am spring-boot:run
) &
SERVER_PID=$!

(
  cd "$CLIENT_DIR"
  npm run dev -- --host 0.0.0.0
) &
CLIENT_PID=$!

wait $SERVER_PID $CLIENT_PID
