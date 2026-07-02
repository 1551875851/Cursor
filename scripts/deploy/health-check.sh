#!/bin/bash
# 健康检查：登录接口返回 200 即视为服务正常
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/../mailcursor-env.sh"

API_BASE=${API_BASE:-http://127.0.0.1:8080}
LOGIN_JSON=${LOGIN_JSON:-$CONFIG_DIR/test-login.json}
if [ ! -f "$LOGIN_JSON" ]; then
  LOGIN_JSON="$SCRIPT_DIR/test-login.json"
fi

if [ ! -f "$LOGIN_JSON" ]; then
  echo "健康检查失败：未找到 $LOGIN_JSON"
  exit 1
fi

sleep "${STARTUP_WAIT_SECONDS:-12}"

RESP=$(curl -s -X POST "${API_BASE}/api/auth/login" \
  -H 'Content-Type: application/json' \
  --data-binary @"$LOGIN_JSON")

CODE=$(echo "$RESP" | python3 -c "import sys,json; print(json.load(sys.stdin).get('code', 0))")
if [ "$CODE" = "200" ]; then
  echo "健康检查通过 (login=200)"
  exit 0
fi

echo "健康检查失败 (login code=$CODE)"
echo "$RESP"
exit 1
