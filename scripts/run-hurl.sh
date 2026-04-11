#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
NETWORK_NAME="${NETWORK_NAME:-realworld_default}"
HURL_IMAGE="${HURL_IMAGE:-ghcr.io/orange-opensource/hurl:latest}"
HOST_URL="${HOST_URL:-http://app:8080}"
UID_VALUE="${UID_VALUE:-$(date +%s%N)}"

docker run --rm \
  --entrypoint sh \
  --network "$NETWORK_NAME" \
  -v "$ROOT_DIR/hurl:/hurl:ro" \
  "$HURL_IMAGE" \
  -lc "hurl --test --retry 30 --retry-interval 1000 --variable host=$HOST_URL --variable uid=$UID_VALUE /hurl/*.hurl"
