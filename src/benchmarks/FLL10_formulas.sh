#!/bin/bash
if [ "$BENCH_MODE" = "MIX" ]
then
	alpha=Fq
	beta=q
else
	alpha=q
	beta=q
fi

if [ "$BENCH_MODE" = "ORIG" ]
then
	alphaI=AFG
else
	alphaI=AXAFAG
fi


if [ $1 = 'f' ]
then
	psi="(($alphaI)^i$alpha>(AFAG)^i$beta)"
	phi="(($alphaI)^i$alpha<(AFAG)^i$beta)"
	#phi="((AFAG)^i$beta>($alphaI)^i$alpha)"
	if [ "$2" != "N" ] ;then echo $psi; fi
	if [ "$3" != "N" ] ;then echo $phi; fi
	exit
fi

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
		if [ "$2" != "N" ] ;then echo $psi; fi
		if [ "$3" != "N" ] ;then echo $phi; fi
	fi

	alpha=$alphaI$alpha
	beta=AFAG$beta
	
done

exit
if [ $2 != sed ]
then 
	echo $psi
	echo $phi
fi
