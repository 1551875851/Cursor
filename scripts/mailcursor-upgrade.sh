#!/bin/bash
set -e

source "$(dirname "$0")/mailcursor-env.sh"

NEW_JAR="$1"
if [ -z "$NEW_JAR" ]; then
  NEW_JAR="$MAILCURSOR_HOME/releases/${JAR_NAME}.new"
fi

if [ ! -f "$NEW_JAR" ]; then
  echo "错误：新 jar 不存在：$NEW_JAR"
  echo "用法：$0 [新jar路径]"
  exit 1
fi

mkdir -p "$BACKUP_DIR" "$LOG_DIR" "$CONFIG_DIR" "$MAILCURSOR_HOME/releases"

if [ ! -f "$CONFIG_DIR/application.yml" ]; then
  if [ -f "$SCRIPT_DIR/config/application.yml.example" ]; then
    cp "$SCRIPT_DIR/config/application.yml.example" "$CONFIG_DIR/application.yml"
    echo "已从模板创建 $CONFIG_DIR/application.yml"
  else
    echo "错误：未找到 $CONFIG_DIR/application.yml"
    exit 1
  fi
fi

BACKUP_FILE=""
if [ -f "$JAR_PATH" ]; then
  BACKUP_FILE="$BACKUP_DIR/${JAR_NAME}.$(date +%Y%m%d-%H%M%S)"
  cp -f "$JAR_PATH" "$BACKUP_FILE"
  echo "$BACKUP_FILE" > "$BACKUP_DIR/latest"
  echo "已备份当前版本：$BACKUP_FILE"
fi

cp -f "$NEW_JAR" "$JAR_PATH"
echo "已部署新 jar：$JAR_PATH"

"$(dirname "$0")/mailcursor-stop.sh"
sleep 2
"$(dirname "$0")/mailcursor-start.sh"

echo "等待服务启动..."
sleep "$STARTUP_WAIT_SECONDS"

HTTP_CODE=$(curl -s -o /dev/null -w '%{http_code}' -X POST "$HEALTH_URL" || echo "000")
if [ "$HTTP_CODE" != "200" ]; then
  echo "健康检查失败，status=$HTTP_CODE，开始自动回退"
  "$(dirname "$0")/mailcursor-rollback.sh" --auto
  exit 1
fi

echo "升级成功，status=$HTTP_CODE"
rm -f "$NEW_JAR" 2>/dev/null || true
