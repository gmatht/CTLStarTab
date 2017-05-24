#!/bin/bash
FORMULA=$1
NAME=$2
if [ -z "$nCPU"  ]
then nCPU=`cat /proc/cpuinfo | grep processor | wc -l`; nJOB=$nCPU
fi
DEPTH="${DEPTH:-9}"
echo $NAME `echo $FORMULA | 
grep -o ^..................................................................`
echo > pids
#set | grep ^n 2>&1  
rm ~/out/result.$NAME.txt 2> /dev/null || true 2> /dev/null
j=1; ( for n in $NODES localhost
do
	#echo $j $((j+nCPU-1))
	X="cd ~/prj/quicktab; for i in `seq $j $((j+nCPU-1))| tr '\n' ' '`; do (date || true; JOB_NO=\$i/$nJOB@$DEPTH /usr/bin/time -o ~/out/time.$NAME.\$i.txt java -jar quicktab.jar '$FORMULA') > ~/out/log.$NAME.\$i.txt &done; wait"
	#X="cd ~/prj/quicktab; for i in `seq $j $((j+nCPU-1))| tr '\n' ' '`; do (date || true; JOB_NO=\$i/$nJOB@$DEPTH /usr/bin/time -o ~/out/time.$NAME.\$i.txt ./qtab '$FORMULA') > ~/out/log.$NAME.\$i.txt &done; wait"
	#echo "$X"
        < /dev/null ssh -q -t -t $n "$X" &
        echo $! >> pids
        j=$((j+$nCPU))
done ; wait ) 2>&1 | strings | while read -r L
do
#echo "READ $L"
case "$L" in
*IsSat*)
	echo "VOTE: formula is satisfiable $L"
	kill `cat pids`
	break
	;;
*Unsat*)
	unsat=$((unsat+1))
	#echo "$L -> $unsat"
	if [ $unsat -ge $nJOB ]
	then
		echo "VOTE: formula is unsatisfiable $L"
	fi
	;;
*)
	echo "UNKNOWN WARNING: $L"

esac
done | tee ~/out/result.$NAME.txt
