#!/bin/bash
set=$1
OPWD=`pwd`
#java JApplet formula [BCTLNEW|BCTLOLD|CTL] outputfile
i=0;
while read -r formula
do
	base=output/"$BENCH_MODE"_$set.$i
    	cd $BENCHMARK_RUNDIR
	mkdir -p output
	/usr/bin/time -o $base.time java JApplet $formula BCTLNEW $base.out 2> $base.err > $base.out
	cd $OPWD
	i=$((i+1))
done
