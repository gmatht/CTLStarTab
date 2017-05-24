rm *.class */*.class
set -e
mkdir -p jar
javac quicktab/QuickTab.java -d jar
cd jar
jar cm <(echo -e "Manifest-Version: 1.0\nPermissions: sandbox\nCodebase: *.uwa.edu.au\nMain-Class: quicktab.QuickTab") `find . -iname '*.class'` > ../quicktab.jar

