#!/bin/bash
# 无 Gradle 构建：aapt2 -> javac -> d8 -> zipalign -> apksigner 手工流水线
# 依赖：JDK 17（JAVA_HOME 或 ~/jdk17）、Android SDK（ANDROID_SDK_ROOT 或 ~/android-sdk）
#       需含 platforms/android-34 与 build-tools/34.0.0
set -e
ROOT=$(cd "$(dirname "$0")" && pwd)
export JAVA_HOME=${JAVA_HOME:-$HOME/jdk17}
export PATH=$JAVA_HOME/bin:$PATH
SDK=${ANDROID_SDK_ROOT:-$HOME/android-sdk}
BT=$SDK/build-tools/34.0.0
AJ=$SDK/platforms/android-34/android.jar
B=$ROOT/build
rm -rf $B && mkdir -p $B/compiled $B/gen $B/classes $B/dex

echo "[1/6] aapt2 compile"
$BT/aapt2 compile --dir $ROOT/res -o $B/compiled/res.zip

echo "[2/6] aapt2 link"
$BT/aapt2 link -o $B/app-unsigned.apk -I $AJ --manifest $ROOT/AndroidManifest.xml --java $B/gen $B/compiled/res.zip

echo "[3/6] javac"
find $ROOT/src $B/gen -name '*.java' > $B/sources.txt
javac -source 8 -target 8 -encoding UTF-8 -cp $AJ -d $B/classes @$B/sources.txt

echo "[4/6] d8"
(cd $B/classes && jar cf $B/classes.jar .)
$BT/d8 --release --min-api 26 --lib $AJ --output $B/dex $B/classes.jar

echo "[5/6] pack + zipalign"
cp $B/app-unsigned.apk $B/app-with-dex.apk
(cd $B/dex && zip -q $B/app-with-dex.apk classes.dex)
$BT/zipalign -f -p 4 $B/app-with-dex.apk $B/app-aligned.apk

echo "[6/6] sign"
KS=$ROOT/release.keystore
if [ ! -f $KS ]; then
  PASS=$(head -c 18 /dev/urandom | base64 | tr -d '/+=' | head -c 20)
  echo "$PASS" > $ROOT/keystore-password.txt
  keytool -genkeypair -keystore $KS -alias pickup -keyalg RSA -keysize 2048 -validity 10000 -storepass "$PASS" -keypass "$PASS" -dname "CN=lomoho, O=personal, C=CN" 2>/dev/null
  echo "已生成本地签名密钥 release.keystore（已被 .gitignore 排除，请勿上传）"
fi
PASS=$(cat $ROOT/keystore-password.txt)
$BT/apksigner sign --ks $KS --ks-pass pass:"$PASS" --key-pass pass:"$PASS" --out $ROOT/PddPickup.apk $B/app-aligned.apk
$BT/apksigner verify $ROOT/PddPickup.apk
ls -la $ROOT/PddPickup.apk
echo BUILD_OK
