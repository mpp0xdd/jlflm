#!/bin/bash
CFLAGS="-J-Dfile.encoding=UTF-8"
JFLAGS="-Dfile.encoding=UTF-8"
CLASSES="classes"
TMP="tmp"
TEST="DoubleMatrixTest"
FORMATTER="../Lib/google-java-format-1.15.0-all-deps.jar"

usage () {
  cat 1>&2 <<EOF
    Usage: $0 [options]
    Options:
      -h      display this help
      -f      format source code
      -c      remove all class and temporary files
      -m      run compile
      -j      create jar
      -t      run test
EOF
  exit
}

format () {
  find -name '*.java' | xargs java "$JFLAGS" -jar "$FORMATTER" -i
}

clean () {
  rm -rf "$CLASSES" "$TMP"
  mkdir "$CLASSES" "$TMP"
}

make () {
  find -name '*.java' | xargs javac "$CFLAGS" -d "$CLASSES"
}

makejar () {
  javac "$CFLAGS" -d "$CLASSES" '*.java'
  jar "$CFLAGS" cvf 'jglib.jar' '*.java' -C "$CLASSES" .
}

test () {
  java "$JFLAGS" -cp "$CLASSES" -ea "$TEST"
}


if [ $# -eq 0 ]; then
  usage
fi

while getopts 'hfcmjt' opt; do
  case "$opt" in
    h) usage ;;
    f) format ;;
    c) clean ;;
    m) make ;;
    j) clean && makejar ;;
    t) format && make && test ;;
  esac
done
