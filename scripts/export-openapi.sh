#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUTPUT_DIR="$ROOT_DIR/docs-site"
OUTPUT_FILE="$OUTPUT_DIR/openapi.json"
API_URL="${API_URL:-http://localhost:8080/v3/api-docs}"

mkdir -p "$OUTPUT_DIR"
curl --fail --silent --show-error "$API_URL" > "$OUTPUT_FILE"
echo "OpenAPI document written to $OUTPUT_FILE"
