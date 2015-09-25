#!/bin/bash
cd output 
cd ../output
pwd
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

for BENCH_MODE in BCTL MIX ORIG
do
for SET in FLL10NY FLL10YN FLL10-NY FLL10-YN
do
echo ------------------------------------------------------------------------
echo $BENCH_MODE $SET
echo "BCTL+BCTL*  combined tableau     |   BCTL* tableau"
echo "i colors hues col/hue time MB"
paste -d \| <(get_ct) <(cd ../v1.0/src/output && get_ct) | sed 's/|$/|? ? ? ? ? ?/
s/^|/? ? ? ? ? ?|/
s/|/ | /' | column -t
done 
done
#paste <( grep olour FLL10NY*out | sed 's/[^.]*.//' | sort -n | grep -v '+' | tr -dc '0123456789\n ' ) <(grep user FLL10NY*time | sed 's/[^.]*.//' | sort -n | sed s/.*:// | sed s/user.*data/ | sed s/)
#paste <(get_colours) <(get_time)
#get_time
#get_colours
