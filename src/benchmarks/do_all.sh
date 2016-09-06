SECS=3600
SECS=10

cd benchmarks

killset() {
    killall do_set.sh
    killall java
    killall time
    sleep 1
    killall -9 do_set.sh
    killall -9 java
    killall -9 time
}

V1_DIR=$(ls -d `pwd | sed s,k/src/benchmarks$,k_v1/src/formulas,` ../v1.0/src | head -n1)

for BENCH_MODE in BCTL MIX ORIG
do
export BENCH_MODE
#for BENCHMARK_RUNDIR in ../v1.0/src/ # ..
#for BENCHMARK_RUNDIR in .. ../../../mark_v1/src/ # ..
for BENCHMARK_RUNDIR in .. $V1_DIR
do
export BENCHMARK_RUNDIR
killset
pwd
./FLL10_formulas.sh 999999 N Y  | sed s/^/-/ | timeout $SECS ./do_set.sh FLL10-NY ; killset
./FLL10_formulas.sh 999999 Y N  | sed s/^/-/ | timeout $SECS ./do_set.sh FLL10-YN ; killset
./FLL10_formulas.sh 999999 Y N  |              timeout $SECS ./do_set.sh FLL10YN  ; killset
./FLL10_formulas.sh 999999 N Y  |              timeout $SECS ./do_set.sh FLL10NY  ; killset
done
done

