#!/bin/bash
# 手工打包脚本（无 Gradle）：aapt2 -> javac -> d8 -> zipalign -> apksigner
# 环境要求：JDK 17、Android SDK（platform 34 + build-tools 34.0.0）
#   export JAVA_HOME=/path/to/jdk17
#   export ANDROID_HOME=/path/to/android-sdk
# 正式签名（可选）：设置 KEYSTORE_FILE / KEYSTORE_PASSWORD / KEY_ALIAS；
# 未设置时自动生成本地 debug 证书（debug.keystore，已被 .gitignore 排除）。
set -e
: "${JAVA_HOME:?请先设置 JAVA_HOME}"
: "${ANDROID_HOME:=${ANDROID_SDK_ROOT:?请先设置 ANDROID_HOME}}"
export PATH=$JAVA_HOME/bin:$PATH
BT=$ANDROID_HOME/build-tools/34.0.0
AJ=$ANDROID_HOME/platforms/android-34/android.jar
ROOT=$(cd "$(dirname "$0")" && pwd)
APP=$ROOT/app
B=$APP/build
rm -rf $B && mkdir -p $B/compiled $B/gen $B/classes $B/dex

echo "[1/6] aapt2 compile"
$BT/aapt2 compile --dir $APP/res -o $B/compiled/res.zip

echo "[2/6] aapt2 link"
$BT/aapt2 link -o $B/app-unsigned.apk -I $AJ --manifest $APP/AndroidManifest.xml --java $B/gen $B/compiled/res.zip

echo "[3/6] javac"
find $APP/src $B/gen -name '*.java' > $B/sources.txt
javac -source 8 -target 8 -encoding UTF-8 -cp $AJ -d $B/classes @$B/sources.txt 2>&1 | grep -v 'bootstrap\|deprecat\|warning' || true

echo "[4/6] d8"
(cd $B/classes && jar cf $B/classes.jar .)
$BT/d8 --release --min-api 26 --lib $AJ --output $B/dex $B/classes.jar

echo "[5/6] pack + zipalign"
cp $B/app-unsigned.apk $B/app-with-dex.apk
(cd $B/dex && zip -q $B/app-with-dex.apk classes.dex)
$BT/zipalign -f -p 4 $B/app-with-dex.apk $B/app-aligned.apk

echo "[6/6] sign"
KS=${KEYSTORE_FILE:-$ROOT/debug.keystore}
PASS=${KEYSTORE_PASSWORD:-android}
ALIAS=${KEY_ALIAS:-pickup}
if [ ! -f "$KS" ]; then
  keytool -genkeypair -keystore "$KS" -alias "$ALIAS" -keyalg RSA -keysize 2048 -validity 10000 -storepass "$PASS" -keypass "$PASS" -dname "CN=debug, O=dev, C=CN" 2>/dev/null
fi
OUT=$ROOT/PDDPickup.apk
$BT/apksigner sign --ks "$KS" --ks-pass pass:"$PASS" --key-pass pass:"$PASS" --out $OUT $B/app-aligned.apk
$BT/apksigner verify --print-certs $OUT | head -4
ls -la $OUT
echo BUILD_OK
