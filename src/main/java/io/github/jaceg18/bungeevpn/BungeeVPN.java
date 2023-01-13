package io.github.jaceg18.bungeevpn;

import Commands.AVPN_Whitelist;
import DataStorage.Memory;
import Utility.Detection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class BungeeVPN extends Plugin implements Listener {

    public static BungeeVPN instance;

    Memory memory;

    List<UUID> playerCache;


    @Override
    public void onEnable() {
        getLogger().info("Starting Bungee AntiVPN - Version 2.0");
        instance = this;
        playerCache = new ArrayList<>();
        memory = new Memory();

        String toTellWhitelist = (memory.isWhitelistEnabled()) ? "Whitelist is enabled: this is turned on by default." : "Whitelist is disabled.";
        getLogger().info(toTellWhitelist);

        ProxyServer.getInstance().getPluginManager().registerListener(this, this);
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new AVPN_Whitelist());

        if (memory.getAPIKey().equalsIgnoreCase("KEY"))
            getLogger().info("Optional: Specify API-KEY in configuration file.");

        getProxy().getScheduler().schedule(this, () -> {
            playerCache.clear();
            getLogger().info("Cache cleared");
        }, (long) memory.getRejoinTime(), TimeUnit.MINUTES);


    }

    @SuppressWarnings("all")
    @EventHandler
    public void checkUserOnLog(PostLoginEvent e) {

        boolean whitelistBypass = false;
        boolean inCacheTime = (playerCache.contains(e.getPlayer().getUniqueId()));

        if (memory.isWhitelistEnabled())
            whitelistBypass = (memory.getWhitelistedPlayers().contains(e.getPlayer().getUniqueId()));

        if (!whitelistBypass && !inCacheTime) {

            Detection detection = new Detection(null);
            detection.useSSL();
            detection.setUseVpn(true);


            if (!memory.getAPIKey().equalsIgnoreCase("KEY"))
                detection.set_api_key(memory.getAPIKey());


            try {
                detection.parseResults(e.getPlayer().getSocketAddress().toString());
            } catch (ParseException | IOException a) {
                a.printStackTrace();
            }

            if (detection.proxy.equalsIgnoreCase("yes")) {
                getLogger().info(e.getPlayer().getDisplayName() + " Tried connecting with a vpn...");
                e.getPlayer().disconnect(new TextComponent(memory.getKickMessage()));
            } else {
                playerCache.add(e.getPlayer().getUniqueId());
                getProxy().getScheduler().schedule(this, () -> {
                    try {
                        playerCache.remove(e.getPlayer().getUniqueId());
                        getLogger().info("Cache cleared for " + e.getPlayer().getDisplayName());
                    } catch (NullPointerException a){a.printStackTrace();}
                }, (long) memory.getRejoinTime(), TimeUnit.MINUTES);


            }
        }
    }

    public Memory getMemory() {
        return memory;
    }
}
