# Pterodactyl SSH Tunnels
A simple server to spawn SSH tunnels when servers are starting, and close them when servers are stopping on a pterodactyl node.

Support for UDP will be added in the future, however currently it only supports TCP through SSH tunnels.

# Building
```bash
mvn clean compile assembly:single
```

# HOW TO INSTALL (Using crontab and screen)
1. Clone the repository
2. Install java 11 and maven (`apt install openjdk-11-jre-headless mvn`)
3. Build the server (`cd pssht && mvn clean compile assembly:single`)
4. Open crontab `crontab -e`
5. Insert `@reboot screen -S pssht_server -dm bash -c "cd ~/PSSHT_LOCATION/target/ && java -jar pssht.jar"`
6. Reboot

A screen called pssht_server with pssht running inside of it should now be running.

# HOW TO USE
This server should be installed on a server which has access to the pterodactyl node(s). An obvious place would be the node itself.\
\
Once you run the server once, it creates a `config.properties` file. Edit this to your suite needs. Make sure to copy SSH keys for the user `tunnel-user` on the server `tunnel-remote`.\
This can be done with 2 easy steps:
1. `ssh-keygen -t rsa` (if no ssh keys exist on the server yet)
2. `ssh-copy-id tunnel-user@tunnel-remote` (copy the ssh key)

Now run the server, and it should work automagically.
