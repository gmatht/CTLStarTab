i=1
if [ ! -z "$1" ]
then
	i=$1
fi
if [ ! -z "$PROBLEMS" ]
then
	PROBLEMS=problems.txt
fi
mkdir -p problems.dir
while read formula
do
	rm /tmp/old.out /tmp/new.out 2> /dev/null
	#echo ----------------------------------------------------------------
	printf " $i:%s\r" "	$formula              "
(
	java JApplet $formula BCTLNEW /tmp/new.$i.out &
	(cd v1.0/src && java JApplet ''$formula BCTLNEW /tmp/old.$i.out) &
	wait
) 2> /dev/null > /dev/null
	#echo
	#echo if  "`grep sat /tmp/old.out`" != "`grep sat /tmp/new.out`" 
	if [ "`grep sat /tmp/old.$i.out`" != "`grep sat /tmp/new.$i.out`" ] 
	then
		echo "$i	$formula	| `grep sat /tmp/old.$i.out` != `grep sat /tmp/new.$i.out`" | tee -a $PROBLEMS 
		mv /tmp/old.$i.out /tmp/new.$i.out problems.dir
		echo
	fi
	i=$((i+1))
done
	

