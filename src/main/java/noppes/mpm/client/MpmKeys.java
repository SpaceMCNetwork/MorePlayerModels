/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.KeyMapping
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.Mod$EventBusSubscriber
 *  net.neoforged.fml.common.Mod$EventBusSubscriber$Bus
 */
package noppes.mpm.client;

import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@OnlyIn(value=Dist.CLIENT)
@EventBusSubscriber(bus=EventBusSubscriber.Bus.MOD, modid="moreplayermodels", value={Dist.CLIENT})
public class MpmKeys {
    public static KeyMapping Screen;
    public static KeyMapping MPM1;
    public static KeyMapping MPM2;
    public static KeyMapping MPM3;
    public static KeyMapping MPM4;
    public static KeyMapping MPM5;
    public static KeyMapping Camera;

    @OnlyIn(value=Dist.CLIENT)
    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        Screen = new KeyMapping("CharacterScreen", 301, "key.categories.gameplay");
        event.register(Screen);
        MPM1 = new KeyMapping("MPM 1", 90, "key.categories.gameplay");
        event.register(MPM1);
        MPM2 = new KeyMapping("MPM 2", -1, "key.categories.gameplay");
        event.register(MPM2);
        MPM3 = new KeyMapping("MPM 3", -1, "key.categories.gameplay");
        event.register(MPM3);
        MPM4 = new KeyMapping("MPM 4", -1, "key.categories.gameplay");
        event.register(MPM4);
        MPM5 = new KeyMapping("MPM 5", -1, "key.categories.gameplay");
        event.register(MPM5);
        Camera = new KeyMapping("MPM Camera", 341, "key.categories.gameplay");
        event.register(Camera);
    }
}
