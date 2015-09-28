check_line() {
cd v1.0/src/ 2> /dev/null
    java JApplet "$3" BCTLNEW VERBc.out 2>/dev/null
    if grep is.sat VERBc.out
    then 
	echo BAD PRUNE: $2
	sleep 1
        exit 1
    fi	
}
[ -e VERB.out ] || cd ..

grep PRUNED VERB.out | while read L
do
    check_line $L
done
