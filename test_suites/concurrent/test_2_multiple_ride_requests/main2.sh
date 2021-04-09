source ../../header.sh 

# Scenario Description
# Check if appropriate number of ride requests are accepted

# every test case should begin with these two steps
tst_wallet_reset
br
tst_ride_reset
br

# Run two test scripts in parallel
# x-terminal-emulator -e "bash -c $(pwd)/script21.sh;bash" 2>/dev/null &
# x-terminal-emulator -e "bash -c $(pwd)/script22.sh;bash" 2>/dev/null &
# sleep 2
./script21.sh &
./script22.sh
sleep 2

totalRides=0
for i in $(cat sh1out sh2out);
do
  totalRides=$(expr $totalRides + $i)
done
echo "Total Rides: $totalRides"
br

if [ "$totalRides" != "4" ];
then
    echo "Inconsistent number of rides."
    quit "no"
fi

quit "yes"



