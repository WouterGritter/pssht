package me.woutergritter.pssht.allocation;

import java.util.Objects;

public class Allocation {
    private final String ip;
    private final int port;
    private final AllocationProtocol protocol;

    public Allocation(String ip, int port, AllocationProtocol protocol) {
        this.ip = ip;
        this.port = port;
        this.protocol = protocol;
    }

    public String getIP() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public AllocationProtocol getProtocol() {
        return protocol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Allocation that = (Allocation) o;
        return port == that.port &&
                Objects.equals(ip, that.ip) &&
                protocol == that.protocol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port, protocol);
    }
}
