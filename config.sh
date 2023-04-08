gcloud config set project fft-java
gcloud config set compute/zone europe-west4-b
gcloud config set compute/region europe-west4

gcloud compute instances create hosts-server daemon-1 daemon-2 app

gcloud compute firewall-rules create allow-all --direction=INGRESS --priority=1000 --network=default --action=ALLOW --rules=all --source-ranges=0.0.0.0/0

host:
sudo apt update
sudo apt install openjdk-11-jdk
sudo apt install wget
wget https://github.com/lionell/labs/raw/master/parcs/HostsServer/TCPHostsServer.jar 
cat > hosts.list
SELF_IP_DM
SELF_IP_DM
java -jar TCPHostsServer.jar&

daemon:
sudo apt update
sudo apt install openjdk-11-jdk
sudo apt install wget
wget https://github.com/lionell/labs/raw/master/parcs/Daemon/Daemon.jar
java -jar Daemon.jar&

app:
sudo apt-get update
sudo apt install openjdk-11-jdk
cat > out/server
SELF_IP_HS