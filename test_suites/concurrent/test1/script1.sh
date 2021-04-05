#! /bin/bash
source ../../header.sh 

rm -f sh1out

#Step 1 : cab 101 signs in
tst_cab_signIn 101 100
br

# Step 2: customer 201 requests a cab
rideDetails="-1"

tst_ride_requestRide rideDetails 201 110 120
echo "$rideDetails"

rideId=$(echo $rideDetails | cut -d' ' -f 1)
cabId=$(echo $rideDetails | cut -d' ' -f 2)
fare=$(echo $rideDetails | cut -d' ' -f 3)

if [ "$rideId" == "-1" ];
then
    echo "Ride to customer 201 denied."
else
    echo "Ride by customer 201 started"
    echo $fare >> sh1out
fi

