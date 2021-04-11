#!/bin/bash

var1=""

echo "Ensure that minikube (minikube --driver=docker start) is running (Press a key)"
read var1

echo "Running minikube docker-env"
eval $(minikube docker-env)


echo "______________________________________________________"

# -------------------------------------------------------------------------------------------------------------------

echo "Do you want to do Maven Build ? (y/n)"
read var1
if [ "$var1" == "y" ]; then
	./maven_build.sh
fi

echo "______________________________________________________"

# -------------------------------------------------------------------------------------------------------------------

echo "Building docker images for minikube (y/n)"
echo "______________________________________________________"
read var1
if [ "$var1" == "y" ]; then
	docker build -q --tag pods/db-service db-service 
	docker build -q --tag pods/cab-service cab-service 
	docker build -q --tag pods/ride-service ride-service 
	docker build -q --tag pods/wallet-service wallet-service 
fi

echo "______________________________________________________"

# -------------------------------------------------------------------------------------------------------------------

echo "Deleting existing deployments for minikube"
echo "______________________________________________________"

minikube kubectl delete deployment db-service
minikube kubectl delete deployment cab-service
minikube kubectl delete deployment ride-service
minikube kubectl delete deployment wallet-service

echo "______________________________________________________"

# -------------------------------------------------------------------------------------------------------------------

echo "Creating deployments for minikube"
echo "______________________________________________________"


kubectl apply -f db-service/db.yml 
kubectl apply -f cab-service/cab.yml 
kubectl apply -f wallet-service/wallet.yml 
sleep 5
kubectl apply -f ride-service/ride.yml 


echo "______________________________________________________"

# -------------------------------------------------------------------------------------------------------------------

echo "Deleting existing services for minikube (y/n)"
echo "______________________________________________________"
read var1
if [ "$var1" == "y" ]; then
	minikube kubectl delete service db-console-service
	minikube kubectl delete service db-tcp-service
	minikube kubectl delete service cab-service
	minikube kubectl delete service ride-service
	minikube kubectl delete service wallet-service
fi

echo "______________________________________________________"

# -------------------------------------------------------------------------------------------------------------------

echo "Exposing deployments for minikube"
echo "______________________________________________________"

minikube kubectl -- expose deployment db-service \
           		 --type=LoadBalancer --port=9091 --target-port=9091  \
           		 --name=db-console-service
                
minikube kubectl -- expose deployment db-service \
           		 --type=LoadBalancer --port=9092 --target-port=9092  \
           		 --name=db-tcp-service
                                
minikube kubectl -- expose deployment cab-service \
           		 --type=LoadBalancer --port=8080 --target-port=8080  
                
minikube kubectl -- expose deployment ride-service \
           		 --type=LoadBalancer --port=8080 --target-port=8081  
     
minikube kubectl -- expose deployment wallet-service \
          		 --type=LoadBalancer --port=8080 --target-port=8082    

echo "______________________________________________________"

# -------------------------------------------------------------------------------------------------------------------

echo "View deployments for minikube"
echo "______________________________________________________"

minikube kubectl -- get deployments

echo "______________________________________________________"

# -------------------------------------------------------------------------------------------------------------------

echo "View pods for minikube"
echo "______________________________________________________"

minikube kubectl -- get pods

echo "______________________________________________________"

# -------------------------------------------------------------------------------------------------------------------

echo "View services for minikube"
echo "______________________________________________________"

kubectl get services

echo "______________________________________________________"

# -------------------------------------------------------------------------------------------------------------------

# #To scale a deployment 
# minikube kubectl -- scale --replicas=0 deployments/cab-service deployments/ride-service deployments/wallet-service
# minikube kubectl -- scale --replicas=1 deployments/cab-service deployments/wallet-service
# minikube kubectl -- scale --replicas=3 deployments/ride-service


# docker container stop $(docker container ls -aq)
# docker container rm $(docker container ls -aq)

