s/./& /g
s/N//g
s/~/-/g
s/=/<==>/g
s/</<==/g
s/>/==>/g
s/-/~/g
s/0/((a) \& (~ a))/g
s/1/((a) \| (~ a))/g
#./mlsolver --satisfiability ctl "`echo '-(AFAGq<AXAFAGq)' | sed -f mlsolver.sed `" -pgs recursive 
