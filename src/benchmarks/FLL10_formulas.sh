#!/bin/bash
alpha=q
beta=q

for i in `seq 0 "$1"`
do
	psi="($alpha>$beta)"
	phi="($beta>$alpha)"


	if [ "$2" = sed ]
	then
		if [ $i != 0 ]
		then
			echo "s/\\b$alpha/\\\\alpha_$i/g"
			echo "s/\\b$beta/\\\\beta_$i/g"
		fi
	else
		if [ "$2" != "N" ]
		then	
			echo $psi
		fi
		if [ "$3" != "N" ]
		then
			echo $phi
		fi
	fi
	
	alpha=AFG$alpha
	beta=AFAG$beta
	
done

exit
if [ $2 != sed ]
then 
	echo $psi
	echo $phi
fi
