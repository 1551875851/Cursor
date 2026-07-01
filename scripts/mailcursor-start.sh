#!/bin/bash
set -e

source "$(dirname "$0")/mailcursor-env.sh"

mkdir -p "$CONFIG_DIR" "$LOG_DIR"

if [ ! -f "$CONFIG_DIR/application.yml" ]; then
  if [ -f "$SCRIPT_DIR/config/application.yml.example" ]; then
    cp "$SCRIPT_DIR/config/application.yml.example" "$CONFIG_DIR/application.yml"
    echo "已从模板创建 $CONFIG_DIR/application.yml，请按需修改后重启"
  else
    echo "错误：未找到 $CONFIG_DIR/application.yml"
    exit 1
  fi
fi

if [ ! -f "$JAR_PATH" ]; then
  echo "错误：未找到 $JAR_PATH"
  exit 1
fi

cd "$MAILCURSOR_HOME"
nohup java -jar "$JAR_NAME" \
  --spring.config.additional-location=file:${CONFIG_DIR}/ \
  > "$LOG_DIR/app.log" 2>&1 &
echo $! > "$PID_FILE"
echo "MailCursor 已启动，pid=$(cat "$PID_FILE")，配置=$CONFIG_DIR/application.yml"
