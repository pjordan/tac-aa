#!/bin/bash
# cd to this directory since we expec this job to be called from cron and don't know what the present working directory is
cd /home/dyoon/TACAA2011/aa-server-10.1.0.1/score_merge/qualifying/

#replace with your path to the tasim jar
java -classpath ../tasim-10.1.0.1.jar se.sics.tasim.is.score.MinAvgZeroScoreMerger -config qualifying.conf 2>&1 >>merge_log.txt

