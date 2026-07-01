#!/bin/bash

source "$(dirname "$0")/mailcursor-env.sh"

DB_PATH="${MAILCURSOR_DB:-$MAILCURSOR_HOME/data/mailcursor.db}"

usage() {
  cat <<'EOF'
用法: mailcursor-db-query.sh [命令] [参数]

命令:
  status                     查看数据库文件与各表记录数
  tables                     列出所有表
  access [条数]              最近访问日志（默认 20 条）
  mail [条数]                最近邮件发送日志（默认 20 条）
  ruankao [条数]             最近软考扫描日志（默认 20 条）
  system [条数]              最近系统事件日志（默认 20 条）
  sql "SELECT ..."           执行自定义 SQL（只读）
  help                       显示帮助

环境变量:
  MAILCURSOR_DB              数据库路径（默认 /app/mailcursor/data/mailcursor.db）

示例:
  bash mailcursor-db-query.sh status
  bash mailcursor-db-query.sh access 10
  bash mailcursor-db-query.sh sql "SELECT * FROM mail_send_log ORDER BY id DESC LIMIT 5"
EOF
}

if [ ! -f "$DB_PATH" ]; then
  echo "错误：数据库不存在：$DB_PATH"
  exit 1
fi

if ! command -v python3 >/dev/null 2>&1; then
  echo "错误：未找到 python3，无法查询 SQLite"
  exit 1
fi

CMD="${1:-status}"
shift 2>/dev/null || true
ARG="${1:-20}"

export DB_PATH
export DB_CMD="$CMD"
export DB_SQL="$*"
export DB_ARG="$ARG"

python3 <<'PY'
import os
import sqlite3
import sys

db_path = os.environ["DB_PATH"]
cmd = os.environ["DB_CMD"]
arg = os.environ["DB_ARG"]
sql = os.environ.get("DB_SQL", "")

conn = sqlite3.connect(db_path)
conn.row_factory = sqlite3.Row
cur = conn.cursor()

def print_rows(rows, columns=None):
    if not rows:
        print("(无记录)")
        return
    if columns is None:
        columns = rows[0].keys()
    widths = {col: max(len(col), *(len(str(row[col]) if row[col] is not None else "") for row in rows)) for col in columns}
    header = " | ".join(col.ljust(widths[col]) for col in columns)
    print(header)
    print("-+-".join("-" * widths[col] for col in columns))
    for row in rows:
        print(" | ".join(str(row[col] if row[col] is not None else "").ljust(widths[col]) for col in columns))

if cmd == "status":
    size = os.path.getsize(db_path)
    print(f"数据库: {db_path}")
    print(f"大小: {size} bytes")
    print()
    tables = [r[0] for r in cur.execute("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name")]
    print(f"{'表名':<20} {'记录数':>8}")
    print("-" * 30)
    for table in tables:
        count = cur.execute(f"SELECT COUNT(*) FROM {table}").fetchone()[0]
        print(f"{table:<20} {count:>8}")

elif cmd == "tables":
    for row in cur.execute("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name"):
        print(row[0])

elif cmd == "access":
    limit = int(arg)
    rows = cur.execute(
        "SELECT id, created_at, client_ip, http_method, request_uri, response_status, cost_ms, log_type "
        "FROM access_log ORDER BY id DESC LIMIT ?", (limit,)
    ).fetchall()
    print_rows(rows)

elif cmd == "mail":
    limit = int(arg)
    rows = cur.execute(
        "SELECT id, created_at, client_ip, sender, recipient, subject, status, error_message "
        "FROM mail_send_log ORDER BY id DESC LIMIT ?", (limit,)
    ).fetchall()
    print_rows(rows)

elif cmd == "ruankao":
    limit = int(arg)
    rows = cur.execute(
        "SELECT id, created_at, client_ip, trigger_type, matched, email_sent, message, matched_titles, status "
        "FROM ruankao_scan_log ORDER BY id DESC LIMIT ?", (limit,)
    ).fetchall()
    print_rows(rows)

elif cmd == "system":
    limit = int(arg)
    rows = cur.execute(
        "SELECT id, created_at, log_level, category, message, detail "
        "FROM system_event_log ORDER BY id DESC LIMIT ?", (limit,)
    ).fetchall()
    print_rows(rows)

elif cmd == "sql":
    if not sql.strip():
        print("错误：请提供 SQL，例如：sql \"SELECT * FROM access_log LIMIT 5\"")
        sys.exit(1)
    if not sql.strip().upper().startswith("SELECT"):
        print("错误：仅允许 SELECT 查询")
        sys.exit(1)
    rows = cur.execute(sql).fetchall()
    if rows:
        print_rows(rows)
    else:
        print("(无记录)")

elif cmd in ("help", "-h", "--help"):
    pass
else:
    print(f"未知命令: {cmd}")
    sys.exit(1)

conn.close()
PY

if [ "$CMD" = "help" ] || [ "$CMD" = "-h" ] || [ "$CMD" = "--help" ]; then
  usage
fi
