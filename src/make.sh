(cd formulas && javac *java)
javac *java
jar cm <(echo -e "Manifest-Version: 1.0\nPermissions: sandbox\nCodebase: *.uwa.edu.au") *class formulas/*class > formulas.jar
scp formulas.jar 00061811@staffhome.ecm.uwa.edu.au:/data/www/home/00061811/html/BCTLv2.0

