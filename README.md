# Pterodactyl SSH Tunnels
A simple server to spawn SSH tunnels when servers are starting, and close them when servers are stopping on a pterodactyl node.

Support for UDP will be added in the future, however currently it only supports TCP through SSH tunnels.

# Building
```bash
mvn clean compile assembly:single
```
