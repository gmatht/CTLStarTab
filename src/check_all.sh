#./check.sh < /home/john/uni/PhD/code/ocaml/parser3/sample_rewrite_rules.txt
#./check.sh < /home/john/uni/PhD/code/ocaml/parser3/mark_formulas.txt
#git pull&&  make *java&& (cd formulas && javac *java) &&
n=1
< problems.txt fgrep -v '|  !=' | cut -f2 | sort -u | tr '~N' '-X' | PROBLEMS=problems_filtered.txt ./check.sh $n | tee -a check.out3
#n=953000
n=1500000
n="$(($(cat all/all.txt | cut -f1 | sort -nr | head -n1)+1))"
ocaml ~/uni/PhD/code/ocaml/parser3/make_random_ctls_formulas.ml 15 2 $n 9000000 | tr '~N' '-X' | PROBLEMS=problems2.txt nice -14 ./check.sh $n | tee -a check.out3
#cat problems.txt | awk '{print $2}' | tr '~N' '-X' | PROBLEMS=problems_filtered.txt ./check.sh $n | tee -a check.out3
#n=1750000
#ocaml ~/uni/PhD/code/ocaml/parser3/make_random_ctls_formulas.ml 10 2 $n 2000000 | tr '~N' '-X' | ./check.sh $n | tee -a check.out3
