#!/bin/sh
dir=$(dirname "$0")
java -cp "$dir/h2-1.4.188.jar:$H2DRIVERS:$CLASSPATH" org.h2.tools.Console "$@"
