D=`date +%F.%s`
cd benchmarks 2> /dev/null
V1_DIR=$(ls -d `pwd | sed s,k/src/benchmarks$,k_v1/src,` ../v1.0/src 2> /dev/null | head -n1)
tar -zcvf ../benchmarks.$D.tgz ../output $V1_DIR/output
echo done: benchmarks.$D.tgz
if [ "$1" = 'mv' ]
then
    mkdir -p ../output.bak/v1
    mkdir -p ../output.bak/v2
    mv ../output/* ../output.bak/v1/
    mv $V1_DIR/output/* ../output.bak/v2/
fi
