package me.woutergritter.pssht;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main {
    public static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1);

    private static final File CONFIG_FILE = new File("config.properties");

    public static void main(String[] args) {
        createDefaultConfig();

        Properties config = new Properties();
        try{
            config.load(new FileInputStream(CONFIG_FILE));
        }catch(IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        PterodactylAPI pterodactylAPI = new PterodactylAPI(
                config.getProperty("pterodactyl-server-address"),
                config.getProperty("pterodactyl-api-key")
        );

        TunnelManager tunnelManager = new TunnelManager(
                pterodactylAPI,
                config.getProperty("tunnel-user"),
                config.getProperty("tunnel-remote")
        );

        tunnelManager.start();
    }

    private static void createDefaultConfig() {
        if(!CONFIG_FILE.exists()) {
            try{
                FileOutputStream fos = new FileOutputStream(CONFIG_FILE);
                InputStream in = Main.class.getResourceAsStream("/config.properties");

                byte[] buf = new byte[512];
                int read;
                while((read = in.read(buf)) > 0) {
                    fos.write(buf, 0, read);
                }

                fos.close();
                in.close();
            }catch(IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }
}
