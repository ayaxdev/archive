package com.skidding.atlas.processor.impl.tracker;

import com.skidding.atlas.AtlasClient;
import com.skidding.atlas.processor.Processor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class LatencyTracker extends Processor {

    public long latency = 0L;

    @Override
    public void init() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (Minecraft.getMinecraft().theWorld != null) {
                    try {
                        if (mc.getCurrentServerData() != null) {
                            sendPing(mc.getCurrentServerData());
                        }
                        else{
                            latency = 0;
                        }
                    } catch (Throwable err) {
                        AtlasClient.getInstance().logger.error("Failed to ping current server", err);
                    }
                }
            }
        }, 0, 2000);

        super.init();
    }

    private void sendPing(ServerData server) throws IOException {
        final ServerAddress address = ServerAddress.fromString(server.serverIP);
        final Socket s = new Socket();

        s.setSoTimeout(5000);

        long startTime = System.currentTimeMillis();

        s.connect(new InetSocketAddress(address.getIP(), address.getPort()));
        s.close();

        latency = System.currentTimeMillis() - startTime;
    }


}
