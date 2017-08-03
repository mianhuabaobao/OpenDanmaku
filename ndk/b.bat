cmd /c ndk-build clean
cmd /c ndk-build
adb push libs/armeabi-v7a/hinit /data/local/tmp/.handjoy/hinit
adb shell chmod 777 /data/local/tmp/.handjoy/hinit