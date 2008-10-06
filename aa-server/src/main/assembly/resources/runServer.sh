#!/bin/bash
#$Id: dm.sh,v 1.5 2005/07/26 19:45:06 mijason Exp $
#
# Usage
# From the deepmaize directory:
#   sh ./dm.sh
#

 TACAA_HOME=`pwd`
 LIB=${TACAA_HOME}/lib
 CLASSPATH=.
 for i in $( ls ${LIB}/*.jar ); do
     CLASSPATH=${CLASSPATH}:$i
 done

#cd agentmanager;

#java -server -cp $CLASSPATH -Xms512m -Xmx512m se.sics.tasim.aw.client.Main -config aw.conf

java -cp $CLASSPATH se.sics.tasim.sim.Main