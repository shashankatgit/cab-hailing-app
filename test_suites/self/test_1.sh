#!/bin/bash

source ../header.sh 
tst_wallet_reset
tst_ride_reset

# tst_cab_signIn 101 1

# br
# rideId="-1"
# tst_ride_requestRide rideId 201 4 8 
# echo "Returned ride id : $rideId"

# br
# test_print
# tst_cab_signIn 102 1
# echo "Return value $?"
# tst_cab_signOut 104
# echo "Return value $?"

# br
# rideId="-1"
# tst_ride_requestRide rideId 202 4 8
# echo "Returned ride id : $rideId"

# numRides="0"
# tst_cab_numRides numRides 103
# echo "Returned numRides : $numRides"

# cabStatus=""
# tst_ride_getCabStatus cabStatus 103
# echo "Returned cabStatus : $cabStatus"

# br
# tst_wallet_reset 

br
balance="0"
tst_wallet_getBalance balance 201
echo "Returned balance : $balance"

br
tst_wallet_addAmount 201 100
echo "Return value $?"

br
balance="0"
tst_wallet_getBalance balance 201
echo "Returned balance : $balance"
