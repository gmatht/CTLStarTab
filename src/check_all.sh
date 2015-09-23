#./check.sh < /home/john/uni/PhD/code/ocaml/parser3/sample_rewrite_rules.txt
#./check.sh < /home/john/uni/PhD/code/ocaml/parser3/mark_formulas.txt
#git pull&&  make *java&& (cd formulas && javac *java) &&
n=1
cat problems.txt | awk '{print $2}' | tr '~N' '-X' | PROBLEMS=problems_filtered.txt ./check.sh $n | tee -a check.out3
n=1750000
ocaml ~/uni/PhD/code/ocaml/parser3/make_random_ctls_formulas.ml 10 2 $n 2000000 | tr '~N' '-X' | ./check.sh $n | tee -a check.out3
