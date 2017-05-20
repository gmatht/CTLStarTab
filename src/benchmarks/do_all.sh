SECS=3600

cd benchmarks

killset() {
    echo TERMINATE SET
    killall do_set.sh 2> /dev/null
    killall java 2> /dev/null
    killall time 2> /dev/null
    sleep 1
    killall -9 do_set.sh 2> /dev/null
    killall -9 java 2> /dev/null
    killall -9 time 2> /dev/null
}

#Depending on whether installed in ~/prg or parser3/work
#Version1 CTLStarTab could be in parser3/work/mark_v1 or ../v1.0/src
V1_DIR=$(ls -d `pwd | sed s,k/src/benchmarks$,k_v1/src,` ../v1.0/src 2> /dev/null | head -n1)
set |grep V1_DIR

for BENCH_MODE in BCTL MIX ORIG
do
export BENCH_MODE
for BENCHMARK_RUNDIR in .. $V1_DIR
do
export BENCHMARK_RUNDIR
killset
pwd
./FLL10_formulas.sh 999999 N Y  | sed s/^/-/ | timeout $SECS ./do_set.sh FLL10-NY ; killset 2> /dev/null
./FLL10_formulas.sh 999999 Y N  | sed s/^/-/ | timeout $SECS ./do_set.sh FLL10-YN ; killset 2> /dev/null
./FLL10_formulas.sh 999999 Y N  |              timeout $SECS ./do_set.sh FLL10YN  ; killset 2> /dev/null
./FLL10_formulas.sh 999999 N Y  |              timeout $SECS ./do_set.sh FLL10NY  ; killset 2> /dev/null
done
done

