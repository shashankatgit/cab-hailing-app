#! /bin/bash
source ../header.sh 

var=""

br
cd test_1_*
echo "------------ Press any key to start Test 1 : ${PWD##*/} -------"
read var1
./main1.sh

br
cd ../test_2_*
echo "------------ Press any key to start Test 2 : ${PWD##*/} -------"
read var1
./main2.sh

br
cd ../test_3_*
echo "------------ Press any key to start Test 3 : ${PWD##*/} -------"
read var1
./main3.sh

br
cd ../test_4_*
echo "------------ Press any key to start Test 4 : ${PWD##*/} -------"
read var1
./main4.sh

br
cd ../test_5_*
echo "------------ Press any key to start Test 5 : ${PWD##*/} -------"
read var1
./main5.sh