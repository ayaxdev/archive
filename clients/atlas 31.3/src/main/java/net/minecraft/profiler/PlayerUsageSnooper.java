package net.minecraft.profiler;

import com.google.common.collect.Maps;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Map.Entry;

import net.minecraft.util.HttpUtil;

public class PlayerUsageSnooper {
    private final Map<String, Object> snooperStats = Maps.<String, Object>newHashMap();
    private final Map<String, Object> clientStats = Maps.<String, Object>newHashMap();
    private final String uniqueID = UUID.randomUUID().toString();
    private final IPlayerUsage playerStatsCollector;
    private final Timer threadTrigger = new Timer("Snooper Timer", true);
    private final Object syncLock = new Object();
    private final long minecraftStartTimeMilis;
    private boolean isRunning;

    public PlayerUsageSnooper(IPlayerUsage playerStatCollector, long startTime) {
        this.playerStatsCollector = playerStatCollector;
        this.minecraftStartTimeMilis = startTime;
    }

    public void startSnooper() {
        if (!this.isRunning) {
            this.isRunning = true;
        }
    }

    public void addMemoryStatsToSnooper() {
        this.addStatToSnooper("memory_total", Long.valueOf(Runtime.getRuntime().totalMemory()));
        this.addStatToSnooper("memory_max", Long.valueOf(Runtime.getRuntime().maxMemory()));
        this.addStatToSnooper("memory_free", Long.valueOf(Runtime.getRuntime().freeMemory()));
        this.addStatToSnooper("cpu_cores", Integer.valueOf(Runtime.getRuntime().availableProcessors()));
        this.playerStatsCollector.addServerStatsToSnooper(this);
    }

    public void addClientStat(String statName, Object statValue) {
        synchronized (this.syncLock) {
            this.clientStats.put(statName, statValue);
        }
    }

    public void addStatToSnooper(String statName, Object statValue) {
        synchronized (this.syncLock) {
            this.snooperStats.put(statName, statValue);
        }
    }

    public Map<String, String> getCurrentStats() {
        Map<String, String> map = Maps.<String, String>newLinkedHashMap();

        synchronized (this.syncLock) {
            this.addMemoryStatsToSnooper();

            for (Entry<String, Object> entry : this.snooperStats.entrySet()) {
                map.put(entry.getKey(), entry.getValue().toString());
            }

            for (Entry<String, Object> entry1 : this.clientStats.entrySet()) {
                map.put(entry1.getKey(), entry1.getValue().toString());
            }

            return map;
        }
    }

    public boolean isSnooperRunning() {
        return this.isRunning;
    }

    public void stopSnooper() {
        this.threadTrigger.cancel();
    }

    public String getUniqueID() {
        return this.uniqueID;
    }

    public long getMinecraftStartTimeMillis() {
        return this.minecraftStartTimeMilis;
    }
}
