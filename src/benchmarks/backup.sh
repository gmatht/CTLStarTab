D=`date +%F.%s`
tar -zcvf benchmarks.$D.tgz output v1.0/src/output
echo done: benchmarks.$D.tgz
if [ "$1" = 'mv' ]
then
    mkdir -p output.bak/v1
    mkdir -p output.bak/v2
    mv output/* output.bak/v1/
    mv v1.0/src/output/* output.bak/v2/
fi
