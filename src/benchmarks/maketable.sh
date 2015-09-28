#!/bin/bash
SET="FLL10NY"
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

make_sed() {
    cd ../benchmarks
    for BENCH_MODE in BCTL MIX ORIG
    do
	export BENCH_MODE
	for n in - ""; do
	    echo "s,$BENCH_MODE FLL10$n""NY,$n`./FLL10_formulas.sh f N Y`," 
	    echo "s,$BENCH_MODE FLL10$n""YN,$n`./FLL10_formulas.sh f Y N`," 
	done
    done
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


full_tables() {
for BENCH_MODE in BCTL MIX ORIG
do
for SET in FLL10NY FLL10YN FLL10-NY FLL10-YN
do
echo ------------------------------------------------------------------------
echo $BENCH_MODE $SET
echo "BCTL+BCTL*  combined tableau     |   BCTL* tableau"
echo "i colors hues col/hue time MB "
#paste -d \| <(get_ct|tail -n1) <(cd ../v1.0/src/output && get_ct|tail -n1) | sed 's/|$/|? ? ? ? ? ?/
paste -d \| <(get_ct) <(cd ../v1.0/src/output && get_ct) | sed 's/|$/|? ? ? ? ? ?/
s/^|/? ? ? ? ? ?|/
s/|/ | /' | column -t
done 
done
}

summary() {
T="\$i\$ colors hues c/h seconds MB"
echo ____
echo "\\phi $T $T"
echo ____
for BENCH_MODE in BCTL MIX ORIG
do
for SET in FLL10NY FLL10YN FLL10-NY FLL10-YN
do
echo -n $BENCH_MODE $SET\ 
paste -d \| <(get_ct|tail -n1) <(cd ../v1.0/src/output && get_ct|tail -n1) | sed 's/|$/|? ? ? ? ? ?/
s/^|/? ? ? ? ? ?|/
s/|/ | /' 
done 
echo ____
done
}

#make_sed

cd output 
cd ../output
#exit
#summary | sed 's/[	 ]/	\&/g'
#paste <( grep olour FLL10NY*out | sed 's/[^.]*.//' | sort -n | grep -v '+' | tr -dc '0123456789\n ' ) <(grep user FLL10NY*time | sed 's/[^.]*.//' | sort -n | sed s/.*:// | sed s/user.*data/ | sed s/)
#paste <(get_colours) <(get_time)
#get_time
#get_colours

#summary | sed -f <(make_sed) | column -t
full_tables
