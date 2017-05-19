set -x
TIMEOUT=2
[ -e qtab ] ||
	gcj --main=quicktab.QuickTab */*class -o qtab
#i=1
i=`tail -n1 log_fuzz.txt | cut -f1`
i=$((i+0))
ocaml /home/john/uni/PhD/code/ocaml/parser3/make_random_ctls_formulas.ml 50 2 $i 99999 | tr '~N' '-X' | while read f
do
	timeout $TIMEOUT java quicktab.QuickTab "$f" 2> log_serial.tmp
	killall java; sleep 0.1; killall java -9

	echo -ne "$i\t" >> log_parallel.txt
	timeout $TIMEOUT ./parallel.sh "$f" $i | grep VOTE > log_parallel.tmp
	killall java; sleep 0.1; killall java -9
	DEPTH=4 nJOB=2 nCPU=2 timeout $TIMEOUT ./parallel.sh "$f" $i | grep VOTE > log_parallel2.tmp
	killall java; sleep 0.1; killall java -9
	echo -e "$i\t"`cat log_serial.tmp`"\t|| "`cat log_parallel.tmp` "\t++ "`cat log_parallel2.tmp`  >> log_fuzz.txt
	i=$((i+1))
done
	
#grep Unsat log_fuzz.txt | column -t -s"`echo -e '\t'`"	
#for t in . null @4 @9; do echo -ne "$t\t"; grep $t log_fuzz.txt | wc -l; done
