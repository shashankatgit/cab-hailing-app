cd test_suites/self/
var1=""
echo "Please ensure services are running and press a key to start testing!"
read var1

i=1
for FILE in *; 
do
	echo "Press any key to start test case $i"
	bash $FILE; 
	read var1
	i=i+1
done
