##!/usr/bin/bash bash

if [[ "$TRAVIS_TEST_RESULT" -eq 0 ]] && [[ $TRAVIS_COMMIT_MESSAGE == *"[UPLOAD PACKAGE]"* ]]; then
   echo "upload package"
   ./gradlew -PbintrayUser="$BINTRAY_USER" -PbintrayApiKey="$BINTRAY_API_KEY" bintrayUpload
fi


