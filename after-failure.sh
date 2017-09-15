#!/bin/bash
# Based on https://gist.github.com/s-leroux/7cb7424d33ba3753e907cc2553bcd1ba

# Uploads downstream log file to Gist
function uploadLog {
  FNAME=$1

  # 1. Somehow sanitize the file content
  #    Remove \r (from Windows end-of-lines),
  #    Replace tabs by \t
  #    Replace " by \"
  #    Replace EOL by \n
  CONTENT=$(sed -e 's/\r//' -e's/\t/\\t/g' -e 's/"/\\"/g' "${FNAME}" | awk '{ printf($0 "\\n") }')

  # 2. Build the JSON request
  read -r -d '' DESC <<EOF
  {
    "description": "Log file for $FNAME",
    "public": true,
    "files": {
      "${FNAME}": {
        "content": "${CONTENT}"
      }
    }
  }
EOF

  # 3. Use curl to send a POST request
  OUTPUT=$(curl -H "Authorization: token $GH_WRITE_TOKEN" -X POST -d "${DESC}" "https://api.github.com/gists")

  # 4. Extract Gist URL from http response
  URL=$(echo "$OUTPUT" | grep "html_url\": \"https://gist.github.com" | awk '{ printf($2) }')

  if [ -z "$URL" ] ; then
    echo -e "POST /gists failed. Body:\n\n$OUTPUT"
  fi

  # 5. Print results without the trailing comma
  echo "You can find log file '$FNAME' at ${URL%,}"
}

# Open logs directory
cd $TRAVIS_BUILD_DIR/subprojects/testDownstream/build

# Find all log files
logFiles=$(ls *.log)

for logFile in $logFiles ; do
  uploadLog $logFile
done
