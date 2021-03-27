#!/bin/bash

cd ride-service

echo "-----------------------RideService : Maven Build Start-----------------------"
./mvnw -DskipTests -q clean package
echo "-----------------------RideService : Maven Build End-----------------------"

docker build -q --tag pods/ride-service .

cd ..

docker run -p 8081:8081 \
	--rm \
	--name rideservice-container \
	-v $(pwd)/init:/root/init \
	--network="host" \
	pods/ride-service &
