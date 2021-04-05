#! /bin/bash
source ../../header.sh 

# Scenario Description
# Check if the current total balance in all wallets is equal to 
# original total balance in all wallets (which is a constant) MINUS totalFare.

# every test case should begin with these two steps
tst_wallet_reset
br
tst_ride_reset
br

# Run two test scripts in parallel
x-terminal-emulator -e "bash -c $(pwd)/script1.sh;bash" 2>/dev/null &
x-terminal-emulator -e "bash -c $(pwd)/script2.sh;bash" 2>/dev/null &
x-terminal-emulator -e "bash -c $(pwd)/script3.sh;bash" 2>/dev/null &
sleep 5

totalFare=0
for i in $(cat sh1out sh2out);
do
  totalFare=$(expr $totalFare + $i)
done
echo "Total Fare: $totalFare"
br

totalWalletBalance=0
for custID in 201 202 203
do
  balance=-1
  tst_wallet_getBalance balance $custID
  echo "Balance for Customer $custID : $balance"
  totalWalletBalance=$(expr $totalWalletBalance + $balance)
  br
done
echo "Total Wallet Balance: $totalWalletBalance"

initBalance=30000
actualBalance=$(expr $totalFare + $totalWalletBalance)

echo "$initBalance"
echo "$actualBalance"

if [ "$actualBalance" != "$initBalance" ];
then
    echo "Inconsistent wallet balance."
    quit "no"
fi

quit "yes"



