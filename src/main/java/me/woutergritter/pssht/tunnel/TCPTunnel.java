package me.woutergritter.pssht.tunnel;

import me.woutergritter.pssht.Main;
import me.woutergritter.pssht.allocation.Allocation;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TCPTunnel implements Tunnel {
    private final String user;
    private final String remote;
    private final Allocation target;

    private boolean opened = false;
    private Process process = null;

    public TCPTunnel(String user, String remote, Allocation target) {
        this.user = user;
        this.remote = remote;
        this.target = target;
    }

    @Override
    public void open() {
        opened = true;

        if(process != null && process.isAlive()) {
            // SSH tunnel is already running
            return;
        }

        try {
            String command = "ssh -N -n " + user + "@" + remote + " -R " + target.getPort() + ":" + target.getIP() + ":" + target.getPort();
            this.process = Runtime.getRuntime().exec(command);

            process.onExit().thenRun(() -> {
                if(opened) {
                    System.out.println("SSH tunnel '" + this + "' died, attempting reconnect in 5 seconds.");
                    Main.EXECUTOR.schedule(() -> {
                        if (opened) {
                            open();
                        }
                    }, 5, TimeUnit.SECONDS);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        opened = false;

        if(process != null) {
            process.destroy();
        }
    }

    public String getUser() {
        return user;
    }

    public String getRemote() {
        return remote;
    }

    public Allocation getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "TCP:" + target.getIP() + ":" + target.getPort() + " " + user + "@" + remote;
    }
}
