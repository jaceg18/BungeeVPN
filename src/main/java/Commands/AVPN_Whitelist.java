package Commands;

import io.github.jaceg18.bungeevpn.BungeeVPN;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.*;

public class AVPN_Whitelist extends Command implements TabExecutor {

    BungeeVPN instance;

    public AVPN_Whitelist() {
        super("avpn", "bungeecord.avpn.command");

        instance = BungeeVPN.instance;
    }

    @SuppressWarnings("all")
    @Override
    public void execute(CommandSender sender, String[] args) {
        boolean isSenderPlayer = sender instanceof ProxiedPlayer;
        UUID uuid;

        if (args.length != 0) uuid = UUID.fromString(args[1]);
        else {instance.getLogger().info("Please follow correct format. /avpn <add/remove> <UUID>"); return;}


        switch (args[0]) {
            case "add" -> {
                instance.getMemory().addWhitelistedPlayer(uuid);

                if (isSenderPlayer) sender.sendMessage(new TextComponent("Added player " + uuid + " to whitelist."));
                else instance.getLogger().info("Added player " + uuid + " to whitelist.");
            }
            case "remove" -> {
                instance.getMemory().removeWhitelistedPlayer(uuid);

                if (isSenderPlayer) sender.sendMessage(new TextComponent("Attempting to remove player " + uuid + " from whitelist."));
                else instance.getLogger().info("Attempting to remove player " + uuid + " from whitelist.");
            }
        }

        instance.getMemory().saveAndReloadWhitelist();
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {

        if (args.length == 1)
            return () -> new ArrayList<>(List.of("add", "remove")).iterator();

        if (args.length == 2) {
            List<String> playerNames = new ArrayList<>();

            for (ProxiedPlayer player : instance.getProxy().getPlayers())
                playerNames.add(player.getName());

            return playerNames;
        }

        return Collections.emptyList();
    }
}
