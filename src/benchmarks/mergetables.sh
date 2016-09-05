#!/bin/bash
#First run maketables.sh and do_all.sh
if [ ! -e benchmarks ]
then 
    cd ..
fi
human() {
echo "Benchmarks of $ID" | sed '
s/ORIG/original BCTL*/
s/MIX/almost BCTL/
s/-NY/(negated, $\\longleftarrow$)/
s/-YN/(negated, $\\longrightarrow$)/
s/NY/($\\longleftarrow$)/
s/YN/($\\longrightarrow$)/
s/FLL10/ formulas /
'
}

header(){
cat <<"EOF"
\begin{sidewaystable}
{\vspace{5in}
\begin{tabular}{|r||l l l l l l|l l l l l l|}

\hline
 & \multicolumn{6}{c|}{BCTL\&BCTL* Combined Tableau}& \multicolumn{6}{c|}{Existing BCTL* Tableau}\\
\hline
\hline
Input formula $\phi                                                                   $&  $i$  &  colors  &  hues    &  h/c  &  seconds  &  MB   &  $i$  &  colors  &  hues    &  c/h  &  seconds  &  MB\\
\hline
EOF
}

footer(){
echo "\\end{tabular}}"
echo "\\caption{$1}"
echo "\\end{sidewaystable}"
}

(
echo "%Made by CTLStarTab/src/benchmarks/mergetables.sh"
header
for ID in ORIG.FLL10-NY MIX.FLL10-NY BCTL.FLL10-NY BCTL.FLL10-YN ORIG.FLL10-YN MIX.FLL10-YN
do
	cat $ID.tex
	echo "\\hline"
done
footer "\\label{tab:bench} Hard asymptotic benchmarks"

header
cat ORIG.FLL10YN.tex
echo "\\hline"
cat ORIG.FLL10NY.tex | grep -v '[$][&]  1[13579]'
echo "\\hline"
footer "\\label{tab:bench2} Asymptotic benchmarks (Original BCTL* formulas)"

for ID in BCTL.FLL10NY MIX.FLL10NY 
do
	header
	cat $ID.tex 
	echo "\\hline"
	footer "\\label{tab:$ID} $ID: `human $ID`"
done

for ID in BCTL.FLL10YN MIX.FLL10YN
do
	header
	cat $ID.tex | head -n 30
	echo "\\hline"
	##< $ID.tex grep -vF '$&  55' | tail -n2
	##echo "\\hline"
	footer "\\label{tab:$ID} $ID: `human $ID`"
done
) | tee output/mergetables.tex
