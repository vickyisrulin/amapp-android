## - Generate release build and SIGN manually using Android Studio
./gradlew clean
./gradlew assembleRelease #sasdaf
ls AMApp/build/outputs/apk/
$HOME/adt/sdk//build-tools/23.0.1/zipalign -v -p 4 AMApp/build/outputs/apk/AMApp-release-unsigned.apk AMApp/build/outputs/apk/AMApp-release-unsigned2.apk
mv AMApp/build/outputs/apk/AMApp-release-unsigned2.apk AMApp/build/outputs/apk/AMApp-release-unsigned.apk
ls AMApp/build/outputs/apk/

## - SIGN the AMApp/build/outputs/apk/AMApp-release-unsigned.apk manually using Android Studio


## -- for debug build
## ./gradlew clean
## ./gradlew assembleDebug


