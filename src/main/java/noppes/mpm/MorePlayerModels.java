/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.GameRules
 *  net.minecraft.world.level.GameRules$BooleanValue
 *  net.minecraft.world.level.GameRules$Category
 *  net.minecraft.world.level.GameRules$Key
 *  net.minecraft.world.level.GameRules$Type
 *  net.neoforged.neoforge.common.NeoForge
 *  net.neoforged.fml.DistExecutor
 *  net.neoforged.fml.ModList
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent
 *  net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext
 *  net.neoforged.fml.loading.FMLPaths
 */
package noppes.mpm;

import java.io.File;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import noppes.mpm.CommonProxy;
import noppes.mpm.LogWriter;
import noppes.mpm.ServerEventHandler;
import noppes.mpm.ServerTickHandler;
import noppes.mpm.client.ClientProxy;
import noppes.mpm.config.ConfigLoader;
import noppes.mpm.config.ConfigProp;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.packets.Packets;
import noppes.mpm.util.PixelmonHelper;

@Mod(value="moreplayermodels")
public class MorePlayerModels {
    public static final String MODID = "moreplayermodels";
    public static final String VERSION = "1.21.1";
    @ConfigProp
    public static int Tooltips = 2;
    public static CommonProxy proxy;
    public static MorePlayerModels instance;
    public static int Version;
    public static File dir;
    public static File skinCache;
    public static boolean HasServerSide;
    @ConfigProp(info="Enable different perspective heights for different model sizes")
    public static boolean EnablePOV;
    @ConfigProp(info="Enables the item on your back")
    public static boolean EnableBackItem;
    @ConfigProp(info="Enables chat bubbles")
    public static boolean EnableChatBubbles;
    @ConfigProp(info="Enables MorePlayerModels startup update message")
    public static boolean EnableUpdateChecker;
    @ConfigProp(info="Set to false if you dont want to see player particles")
    public static boolean EnableParticles;
    @ConfigProp(info="Set to true if you dont want to see hide player names")
    public static boolean HidePlayerNames;
    @ConfigProp(info="Set to true if you dont want to see hide selection boxes when pointing to blocks")
    public static boolean HideSelectionBox;
    @ConfigProp(info="Set to true if you want no flying animation")
    public static boolean DisableFlyingAnimation;
    @ConfigProp(info="Type 0 = Normal, Type 1 = Solid")
    public static int HeadWearType;
    @ConfigProp(info="Minimum scaling size, default 0.5. This only changes it for you, other wont see smaller than their min size")
    public static float ScaleSizeMin;
    @ConfigProp(info="Maximum scaling size, default 1.5. This only changes it for you, other wont see larger than their max size")
    public static float ScaleSizeMax;
    @ConfigProp(info="Disables scaling and animations for more compatibilty with other mods")
    public static boolean Compatibility;
    @ConfigProp(info="On competitive servers like hipixel you dont want people going around in invisible skins")
    public static boolean AllowFullyInvisibleSkins;
    @ConfigProp(info="Used to register buttons to animations")
    public static int button1;
    @ConfigProp(info="Used to register buttons to animations")
    public static int button2;
    @ConfigProp(info="Used to register buttons to animations")
    public static int button3;
    @ConfigProp(info="Used to register buttons to animations")
    public static int button4;
    @ConfigProp(info="Used to register buttons to animations")
    public static int button5;
    public ConfigLoader configLoader;
    public static GameRules.Key<GameRules.BooleanValue> ALLOW_ENTITY_MODELS;

    public MorePlayerModels(IEventBus modEventBus, ModContainer modContainer) {
        instance = this;
        proxy = FMLEnvironment.dist == Dist.CLIENT ? new ClientProxy() : new CommonProxy();
        MPMRegistries.ATTACHMENTS.register(modEventBus);
        modEventBus.addListener(Packets::register);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(ClientProxy::registerReloadListeners);
            modEventBus.addListener(ClientProxy::addPlayerLayers);
        }
        modEventBus.addListener(this::setupClient);
        modEventBus.addListener(this::setup);
        this.configLoader = new ConfigLoader(this.getClass(), new File(dir, "config"), "MorePlayerModels");
        this.configLoader.loadConfig();
        if (normalizeScaleBounds()) {
            this.configLoader.updateConfig();
        }
    }

    private void setupClient(FMLLoadCompleteEvent event) {
        proxy.postLoad();
    }

    private void setup(FMLCommonSetupEvent event) {
        LogWriter.info("Loading");
        if (ModList.get().isLoaded("Morph")) {
            EnablePOV = false;
        }
        PixelmonHelper.load();
        proxy.load();
        NeoForge.EVENT_BUS.register(new ServerEventHandler());
        NeoForge.EVENT_BUS.register(new ServerTickHandler());
    }

    private static GameRules.Key<GameRules.BooleanValue> create(String key, boolean val) {
        GameRules.Type type = GameRules.BooleanValue.create((boolean)val);
        return GameRules.register((String)key, (GameRules.Category)GameRules.Category.MISC, (GameRules.Type)type);
    }

    /** Keeps the local or dedicated-server scale policy usable and finite. */
    public static boolean normalizeScaleBounds() {
        float min = Float.isFinite(ScaleSizeMin) && ScaleSizeMin > 0.0f ? ScaleSizeMin : 0.2f;
        float max = Float.isFinite(ScaleSizeMax) && ScaleSizeMax > 0.0f ? ScaleSizeMax : 2.0f;
        if (max < min) {
            max = min;
        }
        boolean changed = min != ScaleSizeMin || max != ScaleSizeMax;
        ScaleSizeMin = min;
        ScaleSizeMax = max;
        return changed;
    }

    static {
        Version = 8;
        HasServerSide = false;
        EnablePOV = true;
        EnableBackItem = true;
        EnableChatBubbles = true;
        EnableUpdateChecker = true;
        EnableParticles = true;
        HidePlayerNames = false;
        HideSelectionBox = false;
        DisableFlyingAnimation = false;
        HeadWearType = 1;
        ScaleSizeMin = 0.2f;
        ScaleSizeMax = 2.0f;
        Compatibility = false;
        AllowFullyInvisibleSkins = false;
        button1 = EnumAnimation.SLEEP.ordinal();
        button2 = EnumAnimation.SIT.ordinal();
        button3 = EnumAnimation.CRAWL.ordinal();
        button4 = EnumAnimation.HUG.ordinal();
        button5 = EnumAnimation.DANCE.ordinal();
        ALLOW_ENTITY_MODELS = MorePlayerModels.create("mpmAllowEntityModels", true);
        File dir = new File(FMLPaths.CONFIGDIR.get().toFile(), "..");
        MorePlayerModels.dir = new File(dir, MODID);
        if (!MorePlayerModels.dir.exists()) {
            MorePlayerModels.dir.mkdir();
        }
        if (!(skinCache = new File(MorePlayerModels.dir, "skincache")).exists()) {
            skinCache.mkdir();
        }
    }
}
