#!/bin/bash
# ECS 发版：从 GitHub dev 分支拉取最新代码，构建并部署，最后执行基础验证
set -e

APP=${APP:-/app}
BRANCH=${BRANCH:-dev}
MAILCURSOR_REPO=${MAILCURSOR_REPO:-1551875851/Cursor}
MAILWEB_REPO=${MAILWEB_REPO:-1551875851/MailWeb}
SRC_DIR=$APP/src
MC_HOME=$APP/mailcursor
MC_SCRIPTS=$MC_HOME/scripts

export JAVA_HOME=${JAVA_HOME:-$APP/jdk}
export PATH=$JAVA_HOME/bin:$APP/maven/bin:$APP/node/bin:$PATH
export NPM_CONFIG_REGISTRY=${NPM_CONFIG_REGISTRY:-https://registry.npmmirror.com}
export LANG=${LANG:-C.UTF-8}
export LC_ALL=${LC_ALL:-C.UTF-8}

mkdir -p "$SRC_DIR" "$MC_HOME/releases" "$MC_HOME/config" "$APP/mailweb" "$MC_SCRIPTS/deploy"

echo "=== [1/5] 拉取 MailCursor ($BRANCH) ==="
cd "$SRC_DIR"
curl -fL "https://github.com/${MAILCURSOR_REPO}/archive/refs/heads/${BRANCH}.zip" -o mailcursor.zip
rm -rf MailCursor
unzip -q -o mailcursor.zip
mv -f "Cursor-${BRANCH}" MailCursor 2>/dev/null || mv -f Cursor-dev MailCursor
rm -f mailcursor.zip

echo "=== [2/5] 构建 MailCursor ==="
cd "$SRC_DIR/MailCursor"
mvn -q package -DskipTests
cp -f target/MailCursor-1.0.0-SNAPSHOT.jar "$MC_HOME/releases/MailCursor-1.0.0-SNAPSHOT.jar.new"

if [ -d "$SRC_DIR/MailCursor/scripts" ]; then
  cp -rf "$SRC_DIR/MailCursor/scripts/"* "$MC_SCRIPTS/"
  chmod +x "$MC_SCRIPTS"/*.sh "$MC_SCRIPTS"/deploy/*.sh 2>/dev/null || true
fi
if [ -f "$SRC_DIR/MailCursor/scripts/deploy/test-login.json" ]; then
  cp -f "$SRC_DIR/MailCursor/scripts/deploy/test-login.json" "$MC_HOME/config/test-login.json"
fi

echo "=== [3/5] 拉取 MailWeb ($BRANCH) ==="
cd "$SRC_DIR"
curl -fL "https://github.com/${MAILWEB_REPO}/archive/refs/heads/${BRANCH}.zip" -o mailweb.zip
rm -rf MailWeb
unzip -q -o mailweb.zip
mv -f "MailWeb-${BRANCH}" MailWeb 2>/dev/null || mv -f MailWeb-dev MailWeb
rm -f mailweb.zip

echo "=== [4/5] 构建 MailWeb ==="
cd "$SRC_DIR/MailWeb"
"$APP/node/bin/npm" ci --silent
"$APP/node/bin/npm" run build
rm -rf "$APP/mailweb"
mkdir -p "$APP/mailweb"
cp -r dist/* "$APP/mailweb/"

echo "=== [5/5] 升级后端并重启 ==="
bash "$MC_SCRIPTS/mailcursor-upgrade.sh" "$MC_HOME/releases/MailCursor-1.0.0-SNAPSHOT.jar.new"

if [ -x "$APP/nginx/sbin/nginx" ]; then
  "$APP/nginx/sbin/nginx" -s reload 2>/dev/null || "$APP/nginx/sbin/nginx"
fi

echo "=== 执行发版验证 ==="
bash "$MC_SCRIPTS/deploy/verify-deploy.sh"

echo "=== 发版完成 ==="
