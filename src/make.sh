cd formulas && javac *java
javac *java
#jar cm formulas/*class <(echo Permissions: sandbox) *class > formulas.jar
jar cm <(echo -e "Manifest-Version: 1.0\nPermissions: sandbox") *class formulas/*class > formulas.jar

