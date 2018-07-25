#!/usr/bin/env bash
./gradlew :notepad:assembleDebug &&
./gradlew :notepad:installDebug &&
adb shell am start -n "me.juhezi.eternal.notepad/me.juhezi.notepad.main.MainActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER &&
say "Nice to meet you!" &&
if [ `uname` == "Darwin" ]; then
    osascript -e 'display notification "Finish" with title "Nice to meet you!"'
else
    echo "完成！"
fi