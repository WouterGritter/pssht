package me.woutergritter.pssht.tunnel;

import me.woutergritter.pssht.allocation.Allocation;

public class UDPTunnel implements Tunnel {
    private final Allocation target;

    public UDPTunnel(Allocation target) {
        this.target = target;
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
    }

    public Allocation getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "UDP:" + target.getIP() + ":" + target.getPort();
    }
}
