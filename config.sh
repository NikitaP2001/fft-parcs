gcloud config set project fft-java
gcloud config set compute/zone europe-west4-b
gcloud config set compute/region europe-west4

gcloud compute instances create hosts-server daemon-1 daemon-2 app

gcloud compute firewall-rules create allow-all --direction=INGRESS --priority=1000 --network=default --action=ALLOW --rules=all --source-ranges=0.0.0.0/0

#host:
sudo apt update
sudo apt install openjdk-11-jdk
sudo apt install wget
wget https://github.com/lionell/labs/raw/master/parcs/HostsServer/TCPHostsServer.jar 
cat > hosts.list
SELF_IP_DM
SELF_IP_DM
java -jar TCPHostsServer.jar&

#daemon:
sudo apt update
sudo apt install openjdk-11-jdk
sudo apt install wget
wget https://github.com/lionell/labs/raw/master/parcs/Daemon/Daemon.jar
java -jar Daemon.jar&

#app:
sudo apt-get update
sudo apt install openjdk-11-jdk
sudo apt install git make
git clone <this_repo>
cat > out/server
SELF_IP_HS

# How to run
# 1. Start daemon, and host server, fill ip config files
# 2. Then just $make FIN=in.wav FO=res.wav -m=123 -M=22050
# Range of frequences between 123 and 22050 will be 
# written in res.wav sample 
# plot & play new sample $make FO=res.wav