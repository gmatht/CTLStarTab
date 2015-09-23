SECS=60
SECS=3600

killset() {
    killall do_set.sh
    killall java
    killall time
    sleep 1
    killall do_set.sh
    killall java
    killall time
}
 
for BENCH_MODE in BCTL MIX ORIG
do
export BENCH_MODE
for BENCHMARK_RUNDIR in .. ../v1.0/src/
do
export BENCHMARK_RUNDIR
killset
./FLL10_formulas.sh 99 N Y  | sed s/^/-/ | timeout $SECS ./do_set.sh FLL10-NY ; killset
./FLL10_formulas.sh 99 Y N  | sed s/^/-/ | timeout $SECS ./do_set.sh FLL10-YN ; killset
./FLL10_formulas.sh 99 Y N  |              timeout $SECS ./do_set.sh FLL10YN  ; killset
./FLL10_formulas.sh 99 N Y  |              timeout $SECS ./do_set.sh FLL10NY  ; killset
done
done
