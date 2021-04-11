#! /bin/bash
# every test case should begin with these two steps
curl -s http://10.106.15.166:8080/reset
curl -s http://10.108.239.182:8080/reset

balanceBefore=$(curl -s "http://10.108.239.182:8080/getBalance?custId=201")

sleep 2

echo "Balance Before:" $balanceBefore

bash wa1 & 
bash wa2 &

sleep 5	

balanceAfter=$(curl -s "http://10.108.239.182:8080/getBalance?custId=201")

echo "Balance After:" $balanceAfter

