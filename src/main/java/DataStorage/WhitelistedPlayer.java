package DataStorage;


import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class WhitelistedPlayer implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    UUID playerUUID;

    public WhitelistedPlayer(UUID playerUUID){
        this.playerUUID = playerUUID;
    }

    public UUID getPlayerUUID(){
        return playerUUID;
    }

}