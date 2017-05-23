MAIN=$($1:-JApplet)
rm *.class */*.class
set -e
mkdir -p jar
javac JApplet.java -d jar
cd jar
jar cm <(echo -e "Manifest-Version: 1.0\nPermissions: sandbox\nCodebase: *.uwa.edu.au\nMain-Class: JApplet") `find . -iname '*.class'` > ../formulas.jar
exit
(cd formulas && javac *java)
javac *java
jar cm <(echo -e "Manifest-Version: 1.0\nPermissions: sandbox\nCodebase: *.uwa.edu.au\nMain-Class: JApplet") *class formulas/*class > formulas.jar
scp formulas.jar 00061811@staffhome.ecm.uwa.edu.au:/data/www/home/00061811/html/BCTLv2.0

