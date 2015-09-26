#!/bin/bash
#mkdir v1.0; git --work-tree v1.0 checkout v1.0 -- src# (cd v1.0/src/formulas/ && javac *.java; cd .. javac *.java)
i=1
issat(){
    if grep is.sat $1 > /dev/null
    then echo -n Y
    elif grep is.unsat $1 > /dev/null
    then echo -n N
    else echo -n _
    fi
}
    

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
	timeout 5 java JApplet $formula BCTLNEW /tmp/new.$i.out 2> /tmp/new.$i.out.err &
	(cd v1.0/src && timeout 5 java JApplet ''$formula BCTLNEW /tmp/old.$i.out > /tmp/old.$i.out.err) &
	wait
) 2> /dev/null > /dev/null
	#echo
	#echo if  "`grep sat /tmp/old.out`" != "`grep sat /tmp/new.out`" 
	echo "$i	$formula	`issat /tmp/old.$i.out``issat /tmp/new.$i.out`" >> all/all.txt
	mkdir -p all
	if grep unsat /tmp/old.$i.out /tmp/new.$i.out > /dev/null 2> /dev/null
	then
		echo "$i        $formula" >> all/unsatisfiable.txt
	elif grep is.sat /tmp/old.$i.out /tmp/new.$i.out > /dev/null 2> /dev/null
	then
		echo "$i        $formula" >> all/satisfiable.txt
	else
		echo "$i        $formula" >> all/unknown.txt
	fi

	if [ "`grep sat /tmp/old.$i.out`" != "`grep sat /tmp/new.$i.out`" ] 
	then
	    	printf "\r"
		echo "$i	$formula	| `grep sat /tmp/old.$i.out` != `grep sat /tmp/new.$i.out`" | tee -a $PROBLEMS 
		mv /tmp/old.$i.out* /tmp/new.$i.out* problems.dir
		echo
	else
		true
		rm /tmp/old.$i.out* /tmp/new.$i.out*
	fi

    	while [ `xprintidle` -lt 9999 ] ;do sleep 5; done
		
	i=$((i+1))
done
	

