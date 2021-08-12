package me.woutergritter.pssht;

import me.woutergritter.pssht.allocation.Allocation;
import me.woutergritter.pssht.allocation.AllocationProtocol;
import me.woutergritter.pssht.tunnel.TCPTunnel;
import me.woutergritter.pssht.tunnel.Tunnel;
import me.woutergritter.pssht.tunnel.UDPTunnel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TunnelManager {
    private final PterodactylAPI pterodactylAPI;
    private final String tunnelUser;
    private final String tunnelRemote;

    private final Map<Allocation, Tunnel> activeTunnels = new HashMap<>();

    public TunnelManager(PterodactylAPI pterodactylAPI, String tunnelUser, String tunnelRemote) {
        this.pterodactylAPI = pterodactylAPI;
        this.tunnelUser = tunnelUser;
        this.tunnelRemote = tunnelRemote;
    }

    public void start() {
        System.out.println("Starting update loop.");
        Main.EXECUTOR.scheduleAtFixedRate(this::update, 1, 5, TimeUnit.SECONDS);
    }

    private void update() {
        Set<Allocation> openAllocations;

        try{
            openAllocations = pterodactylAPI.getActiveAllocations();
        }catch(Exception e) {
            System.out.println("Error when trying to fetch active allocations: " + e.toString());
            e.printStackTrace();

            return;
        }

        activeTunnels.keySet().removeIf(allocation -> {
            if(!openAllocations.contains(allocation)) {
                Tunnel tunnel = activeTunnels.get(allocation);
                System.out.println("Closing tunnel '" + tunnel + "'");
                tunnel.close();

                return true; // remove
            }

            return false;
        });

        openAllocations.forEach(allocation -> {
            if(!activeTunnels.containsKey(allocation)) {
                // add

                Tunnel tunnel;
                switch(allocation.getProtocol()) {
                    case TCP:
                        tunnel = new TCPTunnel(tunnelUser, tunnelRemote, allocation);
                        break;
                    case UDP:
                        tunnel = new UDPTunnel(allocation);
                        break;
                    default:
                        System.out.println("Unsupported allocation protocol '" + allocation.getProtocol() + "'.");
                        return;
                }

                System.out.println("Opening tunnel '" + tunnel + "'");
                tunnel.open();

                activeTunnels.put(allocation, tunnel);
            }
        });
    }
}
