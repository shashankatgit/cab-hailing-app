#! /bin/bash
source ../../header.sh 

rm -f sh1out

#Step 1 : cab 101 signs in
tst_cab_signIn 101 0
br
tst_cab_signIn 102 0
br

totalRides=0

# Step 2: customer 201 requests a cab
rideDetails="-1"
tst_ride_requestRide rideDetails 201 1 100
echo "$rideDetails"
br
rideId=$(echo $rideDetails | cut -d' ' -f 1)
cabId=$(echo $rideDetails | cut -d' ' -f 2)
fare=$(echo $rideDetails | cut -d' ' -f 3)

if [ "$rideId" != "-1" ];
then
    totalRides=$(expr $totalRides + 1)
fi

# Step 3: customer 201 requests a cab
rideDetails="-1"
tst_ride_requestRide rideDetails 201 101 200
echo "$rideDetails"
br
rideId=$(echo $rideDetails | cut -d' ' -f 1)
cabId=$(echo $rideDetails | cut -d' ' -f 2)
fare=$(echo $rideDetails | cut -d' ' -f 3)

if [ "$rideId" != "-1" ];
then
    totalRides=$(expr $totalRides + 1)
fi

# Step 4: customer 201 requests a cab
rideDetails="-1"
tst_ride_requestRide rideDetails 201 201 300
echo "$rideDetails"
br
rideId=$(echo $rideDetails | cut -d' ' -f 1)
cabId=$(echo $rideDetails | cut -d' ' -f 2)
fare=$(echo $rideDetails | cut -d' ' -f 3)

if [ "$rideId" != "-1" ];
then
    totalRides=$(expr $totalRides + 1)
fi

echo $totalRides >> sh1out