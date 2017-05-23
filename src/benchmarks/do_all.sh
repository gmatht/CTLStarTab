SECS=${1:-3600}

cd benchmarks

killset() {
    echo TERMINATE SET
    killall do_set.sh 2> /dev/null
    killall java 2> /dev/null
    killall time 2> /dev/null
    killall mlsolver 2> /dev/null
    sleep 1
    killall -9 mlsolver  2> /dev/null
    killall -9 do_set.sh 2> /dev/null
    killall -9 java 2> /dev/null
    killall -9 time 2> /dev/null
}

for BENCH_MODE in BCTL MIX ORIG
do
export BENCH_MODE
for SOLVER in bshade mlsolver
do
export SOLVER
killset
pwd
./FLL10_formulas.sh 999999 N Y  | sed s/^/-/ | timeout $SECS ./do_set.sh FLL10-NY ; killset 2> /dev/null
./FLL10_formulas.sh 999999 Y N  | sed s/^/-/ | timeout $SECS ./do_set.sh FLL10-YN ; killset 2> /dev/null
./FLL10_formulas.sh 999999 Y N  |              timeout $SECS ./do_set.sh FLL10YN  ; killset 2> /dev/null
./FLL10_formulas.sh 999999 N Y  |              timeout $SECS ./do_set.sh FLL10NY  ; killset 2> /dev/null
done
done

