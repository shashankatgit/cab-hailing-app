#!/bin/bash

source ../header.sh 

tst_wallet_reset
tst_ride_reset

tst_cab_signIn 103 1
# echo "Return value $?"

# tst_cab_signOut 104
# echo "Return value $?"

rideId="-1"
tst_ride_requestRide rideId 201 4 8
# echo "Returned ride id : $rideId"