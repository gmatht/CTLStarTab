(cd formulas && javac *java)
javac *java
jar cm <(echo Permissions: sandbox) formulas/*class *class > formulas.jar

