#!/bin/bash

# this line is needed since ths script will be called from cron
cd /home/tacusr/server/score_merge/seeding/

java -classpath ../scmserver.jar se.sics.tasim.is.score.MinAvgZeroScoreMerger -config seeding.conf 2>&1 >>merge_log.txt

