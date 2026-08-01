package noppes.mpm.packets.client;

import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

/** Safe client-world lookup for payloads that can arrive while disconnecting. */
final class ClientPacketHelper {
    private ClientPacketHelper() {
    }

    static Player getPlayer(UUID playerId) {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.level == null ? null : minecraft.level.getPlayerByUUID(playerId);
    }
}
