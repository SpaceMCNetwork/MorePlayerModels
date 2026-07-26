/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 */
package noppes.mpm;

import net.minecraft.world.entity.player.Player;
import noppes.mpm.client.parts.MpmPartData;

public class CommonProxy {
    public void load() {
    }

    public void postLoad() {
    }

    public void executor(Player player, Runnable runnable) {
        player.getServer().execute(runnable);
    }

    public void createMpmPartData(MpmPartData data) {
    }
}

