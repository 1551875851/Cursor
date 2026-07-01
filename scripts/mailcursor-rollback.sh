#!/bin/bash
set -e

source "$(dirname "$0")/mailcursor-env.sh"

BACKUP_FILE=""

if [ "$1" = "--auto" ]; then
  if [ -f "$BACKUP_DIR/latest" ]; then
    BACKUP_FILE=$(cat "$BACKUP_DIR/latest")
  fi
elif [ -n "$1" ]; then
  BACKUP_FILE="$1"
else
  BACKUP_FILE=$(ls -t "$BACKUP_DIR"/${JAR_NAME}.* 2>/dev/null | head -1)
fi

if [ -z "$BACKUP_FILE" ] || [ ! -f "$BACKUP_FILE" ]; then
  echo "错误：没有可用的备份版本"
  echo "用法：$0 [备份文件路径|--auto]"
  echo "现有备份："
  ls -lt "$BACKUP_DIR"/${JAR_NAME}.* 2>/dev/null || true
  exit 1
fi

if [ ! -f "$CONFIG_DIR/application.yml" ]; then
  echo "错误：未找到 $CONFIG_DIR/application.yml"
  exit 1
fi

echo "回退到：$BACKUP_FILE"
"$(dirname "$0")/mailcursor-stop.sh"
sleep 2
cp -f "$BACKUP_FILE" "$JAR_PATH"
"$(dirname "$0")/mailcursor-start.sh"

sleep "$STARTUP_WAIT_SECONDS"
HTTP_CODE=$(curl -s -o /dev/null -w '%{http_code}' -X POST "$HEALTH_URL" || echo "000")
if [ "$HTTP_CODE" != "200" ]; then
  echo "回退后健康检查失败，status=$HTTP_CODE，请查看 $LOG_DIR/app.log"
  exit 1
fi

echo "回退成功，status=$HTTP_CODE"
