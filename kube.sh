#!/bin/bash

var1=""

echo "Ensure that minikube is running (minikube --driver=docker start) and then press a key"
read var1

echo "Running minikube docker-env"
eval $(minikube docker-env)

echo "Ensure that all services are freshly built or run : mvn -DskipTests -q clean package : and then press a key"
read var1

echo "Building docker images for minikube"
docker build -q --tag pods/db-service db-service 
docker build -q --tag pods/cab-service cab-service 
docker build -q --tag pods/ride-service ride-service 
docker build -q --tag pods/wallet-service wallet-service 



minikube kubectl -- create deployment cab-service \
            --image=pods/cab-service
                                
minikube kubectl -- create deployment db-service \
            --image=pods/db-service
                  
minikube kubectl -- create deployment ride-service \
            --image=pods/ride-service --replicas=3            

minikube kubectl -- create deployment wallet-service \
           --image=pods/wallet-service
                 
                 
                 
minikube kubectl -- expose deployment cab-service \
           --type=LoadBalancer --port=8080 --target-port=8080  
                
minikube kubectl -- expose deployment db-service \
           --type=LoadBalancer --port=9091 --target-port=9091  --name=db-console-service
                
minikube kubectl -- expose deployment db-service \
           --type=LoadBalancer --port=9092 --target-port=9092  --name=db-tcp-service
                
                
minikube kubectl -- expose deployment cab-service \
           --type=LoadBalancer --port=8080 --target-port=8080  
                

minikube kubectl -- expose deployment ride-service \
           --type=LoadBalancer --port=8080 --target-port=8081  
            

     
minikube kubectl -- expose deployment wallet-service \
           --type=LoadBalancer --port=8080 --target-port=8082    
           
           
#To scale a deployment 
minikube kubectl -- scale --replicas=0 deployments/cab-service deployments/ride-service deployments/wallet-service
minikube kubectl -- scale --replicas=1 deployments/cab-service deployments/wallet-service
minikube kubectl -- scale --replicas=3 deployments/ride-service


#To see running pods
minikube kubectl -- get pods

# To view log of a pod
minikube kubectl -- logs <pod-name>              
