#!/bin/bash
PROJECT="JaMaCa"
CFLAGS="-J-Dfile.encoding=UTF-8"
JFLAGS="-Dfile.encoding=UTF-8"
CLASSES="classes"
LIB="lib"
TMP="tmp"
DOC="doc"
TARGET="DoubleMatrix"
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
      -d      create doc
      -t      run test
EOF
  exit
}

format () {
  find -name '*.java' | xargs java "$JFLAGS" -jar "$FORMATTER" -i
}

clean () {
  rm -rf "$CLASSES" "$LIB" "$TMP" "$DOC"
  mkdir "$CLASSES" "$LIB" "$TMP" "$DOC"
}

make () {
  find -name '*.java' | xargs javac "$CFLAGS" -d "$CLASSES"
}

makejar () {
  jar "$CFLAGS" cvf "$LIB/$PROJECT.jar" '*.java' -C "$CLASSES" .
}

makedoc () {
  javadoc "$CFLAGS" -d "$DOC" "$TARGET.java"
}

test () {
  java "$JFLAGS" -cp "$CLASSES" -ea "$TEST"
}


if [ $# -eq 0 ]; then
  usage
fi

while getopts 'hfcmjdt' opt; do
  case "$opt" in
    h) usage ;;
    f) format ;;
    c) clean ;;
    m) make ;;
    j) clean && make && makejar ;;
    d) clean && makedoc ;;
    t) format && make && test ;;
  esac
done
