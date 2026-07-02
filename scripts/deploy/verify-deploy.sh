#!/bin/bash
# 发版后基础功能验证：前端可访问、登录接口正常、鉴权接口可用
set -e

API_BASE=${API_BASE:-http://127.0.0.1:8080}
WEB_BASE=${WEB_BASE:-http://127.0.0.1:8888}
LOGIN_JSON=${LOGIN_JSON:-/app/mailcursor/config/test-login.json}
BRANCH=${BRANCH:-dev}

echo "=== 发版验证 (branch=$BRANCH) ==="

# 1. 前端静态页
NGINX_CODE=$(curl -s -o /dev/null -w '%{http_code}' "$WEB_BASE/" || echo "000")
if [ "$NGINX_CODE" != "200" ]; then
  echo "FAIL: 前端不可访问，status=$NGINX_CODE"
  exit 1
fi
echo "OK: 前端可访问 ($WEB_BASE/) status=$NGINX_CODE"

# 2. 登录接口
if [ ! -f "$LOGIN_JSON" ]; then
  echo "FAIL: 登录测试文件不存在 $LOGIN_JSON"
  exit 1
fi

LOGIN_RESP=$(curl -s -X POST "$API_BASE/api/auth/login" \
  -H 'Content-Type: application/json' \
  --data-binary @"$LOGIN_JSON")

TOKEN=$(echo "$LOGIN_RESP" | python3 -c "
import sys, json
d = json.load(sys.stdin)
if d.get('code') != 200:
    raise SystemExit('login failed: ' + json.dumps(d, ensure_ascii=False))
print(d['data']['token'])
" 2>&1) || {
  echo "FAIL: 登录接口异常"
  echo "$LOGIN_RESP"
  echo "$TOKEN"
  exit 1
}

if [ -z "$TOKEN" ]; then
  echo "FAIL: 登录未返回 token"
  echo "$LOGIN_RESP"
  exit 1
fi
echo "OK: 登录接口正常，token 长度=${#TOKEN}"

# 3. 鉴权接口 /api/auth/me
ME_RESP=$(curl -s "$API_BASE/api/auth/me" -H "Authorization: Bearer $TOKEN")
ME_USER=$(echo "$ME_RESP" | python3 -c "
import sys, json
d = json.load(sys.stdin)
if d.get('code') != 200:
    raise SystemExit('me failed: ' + json.dumps(d, ensure_ascii=False))
print(d['data']['username'])
" 2>&1) || {
  echo "FAIL: /api/auth/me 异常"
  echo "$ME_RESP"
  echo "$ME_USER"
  exit 1
}
echo "OK: 鉴权接口正常，当前用户=$ME_USER"

echo "=== 发版验证通过 ==="
