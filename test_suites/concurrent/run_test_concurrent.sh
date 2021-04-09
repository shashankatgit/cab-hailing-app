#! /bin/bash
source ../header.sh 

var=""

br
echo "------------ Press any key to start Test Case 1 -------"
read var1
cd test_1_wallet_balance_check
./main1.sh

br
echo "------------ Press any key to start Test Case 2 -------"
read var1
cd ../test_2_multiple_ride_requests
./main2.sh

br
echo "------------ Press any key to start Test Case 3 -------"
read var1
cd ../test_3_signin
./main3.sh