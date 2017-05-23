#!/bin/bash
set -e
#set -x

cmd_root () {
	#Plain rooted bctl tableau
	echo "java -jar root.jar JApplet '$1' BCTLNEW $2" 
}


cmd_bshade () {
	#Plain rooted bctl tableau
	echo "java -jar bshade.jar \"$1\" BCTLNEW $2" 
}


cmd_mlsolver () {
	echo "./mlsolver --satisfiability ctl \"$(echo "$1" | sed -f mlsolver.sed)\" -pgs recursive"
}


mkdir -p output/$SOLVER
set=$1
#OPWD=`pwd`
#java JApplet formula [BCTLNEW|BCTLOLD|CTL] outputfile
#i=0
#while read -r formula
#output/mlsolver/BCTL_FLL10-NY.11.time
#{0001..9999} {0001..9999}
for i in $(seq -f '%04g' 1 9999)
do
	read -r formula
	base=output/$SOLVER/"$BENCH_MODE"_$set.$i
	#echo cmd_$SOLVER $formula $base.txt
	cmd="`cmd_$SOLVER $formula $base.txt`"
	echo "$cmd" >> output/commands.log
	echo "$cmd" > $base.sh
	mkdir -p output
	echo `date +%R` `pwd`/output/$SOLVER/"$BENCH_MODE"_$set.$i
	/usr/bin/time -o $base.time sh $base.sh 2> $base.err > $base.out
	#$cmd
#	cd $OPWD
#	i=$((i+1))
#	echo -- $i --
 done
