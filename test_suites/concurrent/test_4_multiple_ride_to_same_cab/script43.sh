#! /bin/bash
source ../../header.sh 
rm -f sh3out

#Step 1: customer 203 requests a cab
rideDetails="-1"
tst_ride_requestRide rideDetails 203 10 20
echo "$rideDetails"
br
rideId=$(echo $rideDetails | cut -d' ' -f 1)

rideStarted=0
if [ "$rideId" != "-1" ];
then
    rideStarted=1
fi

echo $rideStarted>>sh3out