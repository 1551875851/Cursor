#!/bin/bash
set -e

APP=${APP:-/app}
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

mkdir -p "$APP/mailcursor/logs" "$APP/nginx/logs"

"$SCRIPT_DIR/mailcursor-stop.sh"
"$APP/nginx/sbin/nginx" -s stop 2>/dev/null || true
sleep 1

"$SCRIPT_DIR/mailcursor-start.sh"
"$APP/nginx/sbin/nginx"

sleep 3
ss -tlnp 2>/dev/null | grep -E ':8080|:8888' || netstat -tlnp 2>/dev/null | grep -E ':8080|:8888'
echo "=== 全部服务已启动 ==="
curl -s -o /dev/null -w "nginx:%{http_code}\n" http://127.0.0.1:8888/ || true
curl -s -o /dev/null -w "api:%{http_code}\n" -X POST http://127.0.0.1:8080/api/ruankao/scan || true
