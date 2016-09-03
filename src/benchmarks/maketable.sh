#!/bin/bash
#set -x
SET="FLL10NY"

cd benchmarks
cd ..

#Run do_all.sh first
if false # ! command -v R
then
   if ! sudo apt-get install r-base-core
   then
       echo cannot install R
       #exit
   fi
fi


#grep olour FLL10NY*out | sort -k2 -n -t '.' | grep -v '+
get_colours() {
	grep -H olour $BENCH_MODE"_"$SET*out | sed 's/[^.]*.//' | sort -n | grep -v '+' | tr -dc '0123456789\n ' | sed 's/  / /
s/      / /'
#sed 's/    / /' | sed 's/  / /'
}
get_time() {
	grep -H user $BENCH_MODE"_"$SET*time | sed 's/[^.]*.//' | sort -n | sed 's/.*time://
s/user.*data / /g
s/...maxresident.*//
s/  */ /'
}

get_ct() {
	paste  <(get_colours) <(get_time) | grep ^[0-9]
}

#get_ct_tail() {
#	paste  <(get_colours|grep ^[0-9]|tail -n1) <(get_time|grep [^0-9]|tail -n1) 
#}

make_sed_fname2formula() {
    cd ../benchmarks
    for BENCH_MODE in BCTL MIX ORIG
    do
	export BENCH_MODE
	for n in - ""; do
	    echo "s,$BENCH_MODE FLL10$n""NY,$n`./FLL10_formulas.sh f N Y`," 
	    echo "s,$BENCH_MODE FLL10$n""YN,$n`./FLL10_formulas.sh f Y N`," 
	done
    done
}
make_sed_2tex() {
    cat <<EOF
s, [|] , ,g
s,[	 ], \\&	,g
s,[-],\\\\neg{},g
s,[A-Z],\\\\&,g
s,>,\\\\rightarrow{},g
s,<,\\\\leftarrow{},g
s,^[^ 	]* ,$&$,g
s,.M.B,MB,g
s,q,{}q,g
s,$,\\\\\\\\,g
s,____.*$,\\\\hline,g
#s,<,\\leftarrow,,g
EOF
}

make_sed() {
	make_sed_fname2formula
	make_sed_2tex
}

2tex() {
	sed -f <(make_sed)
}

full_tables() {
for BENCH_MODE in BCTL MIX ORIG
do
for SET in FLL10NY FLL10YN FLL10-NY FLL10-YN
do
echo ------------------------------------------------------------------------
echo "$BENCH_MODE $SET:	`echo $BENCH_MODE $SET | sed -f <(make_sed_fname2formula)`"
echo "BCTL+BCTL*  combined tableau     |   BCTL* tableau"
echo "i colors hues col/hue time MB "
#paste -d \| <(get_ct|tail -n1) <(cd ../v1.0/src/output && get_ct|tail -n1) | sed 's/|$/|? ? ? ? ? ?/
get_ct>$BENCH_MODE.$SET.ct
(cd ../v1.0/src/output && get_ct) > $BENCH_MODE.$SET.ct1
< $BENCH_MODE.$SET.ct cut -d\  -f1,2 > $BENCH_MODE.$SET.colours.txt
< $BENCH_MODE.$SET.ct cut -d\  -f1,3 > $BENCH_MODE.$SET.hues.txt
ADD_Q_SET='s/|$/|? ? ? ? ? ?/
s/^|/? ? ? ? ? ?|/
s/|/ | /'
paste -d \| $BENCH_MODE.$SET.ct $BENCH_MODE.$SET.ct1  | sed "$ADD_Q_SET" | tee $BENCH_MODE.$SET.table |  column -t
cat $BENCH_MODE.$SET.table | sed "s/^/$BENCH_MODE $SET /" | 2tex | column -t > $BENCH_MODE.$SET.tex
done 
done
}

summary() {
T="\$i\$ colors hues c/h seconds MB"
echo ____
echo "\\phi $T $T"
echo ____
for BENCH_MODE in BCTL MIX ORIG; do
for SET in FLL10NY FLL10YN FLL10-NY FLL10-YN; do
echo -n $BENCH_MODE $SET\ 
paste -d \| <(get_ct|tail -n1) <(cd ../v1.0/src/output && get_ct|tail -n1) | sed 's/|$/|? ? ? ? ? ?/
s/^|/? ? ? ? ? ?|/
s/|/ | /' 
done 
echo ____
done
}

regression() {
	for TYPE in colours hues; do
		for BENCH_MODE in BCTL MIX ORIG; do
			for SET in FLL10NY FLL10YN FLL10-NY FLL10-YN; do
				echo -n "$TYPE	$BENCH_MODE $SET	`wc -l $BENCH_MODE.$SET.$TYPE.txt`	"
				R -q -e 'd = read.table("'$BENCH_MODE.$SET.$TYPE.txt'");c=coef(lm(d$V2~poly(d$V1,3,raw=TRUE)));paste(c[1],c[2],c[3],c[4])' | grep '"$' 2> /dev/null 
			done
		done
	done
}

#make_sed

cd output 
cd ../output

full_tables | tee benchmark_fulltables.txt
summary | sed -f <(make_sed) | column -t | tee summary.tex
regression | tee benchmark_regression.txt

exit
#exit
#summary | sed 's/[	 ]/	\&/g' > benchmark_summary.txt
#paste <( grep olour FLL10NY*out | sed 's/[^.]*.//' | sort -n | grep -v '+' | tr -dc '0123456789\n ' ) <(grep user FLL10NY*time | sed 's/[^.]*.//' | sort -n | sed s/.*:// | sed s/user.*data/ | sed s/)
#paste <(get_colours) <(get_time)
#get_time
#get_colours

#summary | sed -f <(make_sed) | column -t
