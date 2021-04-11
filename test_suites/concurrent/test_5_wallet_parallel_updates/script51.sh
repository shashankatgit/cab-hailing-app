#! /bin/bash
source ../../header.sh 

for i in {0..10}
do
	echo "Shell 1:" $i
	# resp=$(curl -s "http://10.108.239.182:8080/addAmount?custId=201&amount=100")
    tst_wallet_addAmount 201 100
done