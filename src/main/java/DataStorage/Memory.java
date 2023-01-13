package DataStorage;
import io.github.jaceg18.bungeevpn.BungeeVPN;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Memory {

    private String kickMessage;
    private String API_Key;
    private final BungeeVPN instance;

    private static List<WhitelistedPlayer> whitelist;

    private Configuration defaultConfig;

    private double rejoinTime;

    boolean whitelistEnabled;

    String tag;

    private final File whitelistFile;


    public Memory() {
        instance = BungeeVPN.instance;
        whitelist = new ArrayList<>();
        whitelistFile = new File(instance.getDataFolder(), "whitelist.bin");
        checkFiles();
    }

    private void checkFiles() {

        if (!instance.getDataFolder().exists())
            instance.getLogger().info("Generating plugins folder..." + instance.getDataFolder().mkdir());

        File configFile = new File(instance.getDataFolder(), "config.yml");

        if (!configFile.exists())
            try {
                instance.getResourceAsStream("config.yml").transferTo(new FileOutputStream(configFile));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        if (!whitelistFile.exists())
            try {
                instance.getResourceAsStream("whitelist.bin").transferTo(new FileOutputStream(whitelistFile));
            } catch (IOException e){
                throw new RuntimeException(e);
            }

        loadWhitelist();
        loadConfiguration();

        if (whitelist.isEmpty()) {
            saveAndReloadWhitelist();
            instance.getLogger().info("Whitelist is empty - uploading empty list...");
        }
    }

    private void loadConfiguration() {
        try {
            defaultConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(BungeeVPN.instance.getDataFolder(), "config.yml"));
        } catch (Exception e) {e.printStackTrace();}

        kickMessage = defaultConfig.getString("KICK-MESSAGE");
        API_Key = defaultConfig.getString("API-KEY");
        rejoinTime = defaultConfig.getDouble("Check-On-Rejoin-Time");
        whitelistEnabled = defaultConfig.getBoolean("WHITELIST");
        tag = defaultConfig.getString("TAG");

    }

    public String getAPIKey() {
        return API_Key;
    }

    public String getKickMessage() {
        return kickMessage;
    }

    public double getRejoinTime(){
        return rejoinTime;
    }

    public void saveWhitelist(){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(whitelistFile));
            oos.flush();
            oos.writeObject(whitelist);
            oos.close();
        } catch (IOException e) {instance.getLogger().info("Failed to save whitelisted players.");}
    }

    @SuppressWarnings("unchecked")
    public void loadWhitelist(){
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(whitelistFile));
            whitelist = (List<WhitelistedPlayer>) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {instance.getLogger().info("Whitelist had trouble loading." +
                " If this is the first time executing. This is no cause for alarm");}
    }

    public List<UUID> getWhitelistedPlayers(){
        return whitelist.stream().map(WhitelistedPlayer::getPlayerUUID).toList();
    }
    public void addWhitelistedPlayer(UUID player){
        if (player != null)
            whitelist.add(new WhitelistedPlayer(player));
        else
            instance.getLogger().info("Are you sure this player exists?");
    }
    public void removeWhitelistedPlayer(UUID player){
        if (player != null)
            whitelist.removeIf(whitelistedPlayer -> whitelistedPlayer.getPlayerUUID().equals(player));
        else
            instance.getLogger().info("Are you sure this player exists?");
    }

    public void saveAndReloadWhitelist(){
        saveWhitelist();
        loadWhitelist();
    }

    public boolean isWhitelistEnabled(){
        return whitelistEnabled;
    }

    public void reload(){
        checkFiles();
        instance.setMemory(this);
    }

    public String getTag(){
        return tag;
    }
}
