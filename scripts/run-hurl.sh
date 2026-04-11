#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
HURL_IMAGE="${HURL_IMAGE:-ghcr.io/orange-opensource/hurl:latest}"
HOST_URL="${HOST_URL:-http://app:8080}"
UID_VALUE="${UID_VALUE:-$(date +%s%N)}"

if [[ -z "${NETWORK_NAME:-}" ]]; then
  APP_CONTAINER_ID="$(docker compose ps -q app)"
  NETWORK_NAME="$(docker inspect -f '{{range $name, $_ := .NetworkSettings.Networks}}{{println $name}}{{end}}' "$APP_CONTAINER_ID" | head -n1 | tr -d '[:space:]')"
fi

if [[ -z "$NETWORK_NAME" ]]; then
  echo "Could not determine Docker network for compose app service" >&2
  exit 1
fi

docker run --rm \
  --entrypoint sh \
  --network "$NETWORK_NAME" \
  -v "$ROOT_DIR/hurl:/hurl:ro" \
  "$HURL_IMAGE" \
  -lc "hurl --test --retry 30 --retry-interval 1000 --variable host=$HOST_URL --variable uid=$UID_VALUE /hurl/*.hurl"
