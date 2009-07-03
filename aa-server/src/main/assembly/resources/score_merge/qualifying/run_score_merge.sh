#!/bin/bash

# this line is needed since ths script will be called from cron
cd /home/tacusr/server/score_merge/qualifying/

java -classpath ../scmserver.jar se.sics.tasim.is.score.MinAvgZeroScoreMerger -config qualifying.conf 2>&1 >>merge_log.txt

