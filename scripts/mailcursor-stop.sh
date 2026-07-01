#!/bin/bash

source "$(dirname "$0")/mailcursor-env.sh"

stopped=0

if [ -f "$PID_FILE" ]; then
  pid=$(cat "$PID_FILE")
  if [ -n "$pid" ] && kill -0 "$pid" 2>/dev/null; then
    kill "$pid" 2>/dev/null || true
    for _ in $(seq 1 15); do
      kill -0 "$pid" 2>/dev/null || break
      sleep 1
    done
    kill -9 "$pid" 2>/dev/null || true
    stopped=1
  fi
  rm -f "$PID_FILE"
fi

if pkill -f "$JAR_NAME" 2>/dev/null; then
  stopped=1
  sleep 1
fi

if [ "$stopped" -eq 1 ]; then
  echo "MailCursor 已停止"
else
  echo "MailCursor 未在运行"
fi
