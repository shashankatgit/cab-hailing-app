#! /bin/bash
source ../../header.sh 

rm -f sh2out

#Step 1 : cab 102 signs in
tst_cab_signIn 102 100
br

# Step 2: customer 202 requests a cab
rideDetails="-1"

tst_ride_requestRide rideDetails 201 0 100
echo "$rideDetails"

rideId=$(echo $rideDetails | cut -d' ' -f 1)
cabId=$(echo $rideDetails | cut -d' ' -f 2)
fare=$(echo $rideDetails | cut -d' ' -f 3)

if [ "$rideId" == "-1" ];
then
    echo "Ride to customer 202 denied."
else
    echo "Ride by customer 202 started"
    echo $fare >> sh2out
fi