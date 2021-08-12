package me.woutergritter.pssht;

import me.woutergritter.pssht.allocation.Allocation;
import me.woutergritter.pssht.allocation.AllocationProtocol;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class PterodactylAPI {
    private String serverAddress;
    private String apiKey;

    public PterodactylAPI(String serverAddress, String apiKey) {
        this.serverAddress = serverAddress;
        this.apiKey = apiKey;
    }

    public Set<Allocation> getActiveAllocations() {
        Set<Allocation> res = new HashSet<>();

        JSONArray jsonServers = request("/api/client")
                .getJSONArray("data");

        for(int i = 0; i < jsonServers.length(); i++) {
            JSONObject jsonServer = jsonServers.getJSONObject(i)
                    .getJSONObject("attributes");

            JSONObject jsonResources = request("/api/client/servers/" + jsonServer.getString("identifier") + "/resources");
            boolean running = jsonResources.getJSONObject("attributes")
                    .getString("current_state")
                    .equals("running");

            if(!running) {
                continue;
            }

            JSONArray jsonAllocations = jsonServer
                    .getJSONObject("relationships")
                    .getJSONObject("allocations")
                    .getJSONArray("data");

            for(int j = 0; j < jsonAllocations.length(); j++) {
                JSONObject jsonAllocation = jsonAllocations.getJSONObject(j)
                        .getJSONObject("attributes");

                String ip = jsonAllocation.getString("ip");
                int port = jsonAllocation.getInt("port");
                AllocationProtocol protocol = AllocationProtocol.TCP;

                if(!jsonAllocation.isNull("notes") &&
                        jsonAllocation.getString("notes").toLowerCase().contains("udp")) {
                    protocol = AllocationProtocol.UDP;
                }

                res.add(new Allocation(ip, port, protocol));
            }
        }

        return res;
    }

    private JSONObject request(String path) {
        try{
            URL url = new URL(serverAddress + path);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            StringBuilder lines = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                lines.append(line);
                lines.append('\n');
            }

            reader.close();

            return new JSONObject(lines.toString());
        }catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
