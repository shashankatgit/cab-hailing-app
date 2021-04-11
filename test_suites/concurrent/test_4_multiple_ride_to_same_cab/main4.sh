#! /bin/bash
source ../../header.sh 

# Scenario Description
# Multiple request come to sab cab, and the cab should offer onyl one ride

# every test case should begin with these two steps
tst_wallet_reset
br
tst_ride_reset
br

#Step 1: Cab 101 signs in
tst_cab_signIn 101 10

# Run two test scripts in parallel
# x-terminal-emulator -e "bash -c $(pwd)/script31.sh;bash" 2>/dev/null &
# x-terminal-emulator -e "bash -c $(pwd)/script32.sh;bash" 2>/dev/null &
# x-terminal-emulator -e "bash -c $(pwd)/script33.sh;bash" 2>/dev/null &
# sleep 2
./script41.sh &
./script42.sh &
./script43.sh &
./script44.sh &
./script45.sh &
./script46.sh
sleep 5

totalRides=0
for i in $(cat sh1out sh2out sh3out sh4out sh5out sh6out);
do
  totalRides=$(expr $totalRides + $i)
done
echo "Total Number of ride requests accepted: $totalRides"
br

if [ "$totalRides" -ne "1" ];   
then
    echo "Invalid number of rides : $totalRides"
    quit "no"
fi

quit "yes"
