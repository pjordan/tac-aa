#!/bin/bash
#
# Usage
#   sh ./runViewer.sh -file [filename]
#

TACAA_HOME=`pwd`
LIB=${TACAA_HOME}/lib
CLASSPATH=.
for i in $( ls ${LIB}/*.jar ); do
    CLASSPATH=${CLASSPATH}:$i
done


java -cp $CLASSPATH se.sics.tasim.logtool.Main $*