/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.PlayerModel
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.layers.CapeLayer
 *  net.minecraft.client.renderer.entity.layers.ElytraLayer
 *  net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer
 *  net.minecraft.client.renderer.entity.layers.RenderLayer
 *  net.minecraft.client.renderer.entity.player.PlayerRenderer
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.SimpleTexture
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.minecraft.server.packs.resources.ReloadableResourceManager
 *  net.minecraft.server.packs.resources.ResourceManagerReloadListener
 *  net.minecraft.server.packs.resources.ResourceProvider
 *  net.minecraft.world.entity.player.Player
 *  net.neoforged.neoforge.common.NeoForge
 *  net.neoforged.neoforge.registries.ForgeRegistries
 */
package noppes.mpm.client;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.core.registries.BuiltInRegistries;
import noppes.mpm.CommonProxy;
import noppes.mpm.ModelData;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.client.ClientEventHandler;
import noppes.mpm.client.PresetController;
import noppes.mpm.client.RenderEvent;
import noppes.mpm.client.SkinUtil;
import noppes.mpm.client.VersionChecker;
import noppes.mpm.client.fx.EntityEnderFX;
import noppes.mpm.client.gui.select.GuiTextureSelection;
import noppes.mpm.client.layer.LayerBackItem;
import noppes.mpm.client.layer.LayerCapeMPM;
import noppes.mpm.client.layer.LayerElytraAlt;
import noppes.mpm.client.layer.LayerHeadwear;
import noppes.mpm.client.layer.LayerInterface;
import noppes.mpm.client.layer.LayerParts;
import noppes.mpm.client.model.animation.AnimationHandler;
import noppes.mpm.client.parts.MpmPartData;
import noppes.mpm.client.parts.MpmPartDataClient;
import noppes.mpm.client.parts.MpmPartReader;
import noppes.mpm.mixin.ArmorLayerMixin;
import noppes.mpm.mixin.LivingRenderer2Mixin;
import noppes.mpm.mixin.ParticleManagerMixin;
import noppes.mpm.shared.client.model.util.CustomRenderStates;

public class ClientProxy
extends CommonProxy {
    public static ModelData data;
    public static PlayerModel playerModel;
    public static ArmorLayerMixin armorLayer;
    public static ArmorLayerMixin armorLayerSlim;
    private static final ResourceManagerReloadListener RELOAD_LISTENER = manager -> {
        EntityEnderFX.portalSprite = ((ParticleManagerMixin)Minecraft.getInstance().particleEngine).getPacks().get(BuiltInRegistries.PARTICLE_TYPE.getKey(ParticleTypes.PORTAL));
        MpmPartReader.reload();
        SkinUtil.reloadSkins();
        GuiTextureSelection.clear();
        RenderSystem.recordRenderCall(() -> {
            try {
                CustomRenderStates.posTexNormalShader = new ShaderInstance((ResourceProvider)manager, "moreplayermodels:position_tex_normal", CustomRenderStates.POS_TEX_NORMAL);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
    };

    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((PreparableReloadListener)RELOAD_LISTENER);
    }

    @Override
    public void load() {
        this.createFolders();
        NeoForge.EVENT_BUS.register(new RenderEvent());
        NeoForge.EVENT_BUS.register(new ClientEventHandler());
        new PresetController(MorePlayerModels.dir);
        AnimationHandler.initAnimations();
        if (MorePlayerModels.EnableUpdateChecker) {
            VersionChecker checker = new VersionChecker();
            checker.start();
        }
    }

    @Override
    public void postLoad() {
        // Player renderers are populated after load-complete. They are wired
        // through EntityRenderersEvent.AddLayers below instead of racing an
        // empty skin-renderer map here.
    }

    /** Installs MPM layers after NeoForge has created both player renderers. */
    public static void addPlayerLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model skin : event.getSkins()) {
            PlayerRenderer renderer = event.getSkin(skin);
            if (renderer != null) {
                ClientProxy.addLayers(renderer, skin == PlayerSkin.Model.SLIM, event.getEntityModels());
            }
        }
    }

    public static void fixModels() {
        Minecraft mc = Minecraft.getInstance();
        EntityRenderDispatcher manager = mc.getEntityRenderDispatcher();
        Map map = manager.getSkinMap();
        for (Object key : map.keySet()) {
            EntityRenderer render = (EntityRenderer)map.get(key);
            if (render instanceof PlayerRenderer playerRenderer) {
                ClientProxy.addLayers(playerRenderer, key.toString().toLowerCase().contains("slim"), mc.getEntityModels());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void addLayers(PlayerRenderer playerRender, boolean slim, EntityModelSet entityModels) {
        List list;
        boolean hasMPMLayers = false;
        List list2 = list = ((LivingRenderer2Mixin)playerRender).getLayers();
        synchronized (list2) {
            for (Object object : new ArrayList(list)) {
                RenderLayer layer2 = (RenderLayer)object;
                if (!(layer2 instanceof LayerInterface)) continue;
                ((LayerInterface)layer2).setBase((PlayerModel)playerRender.getModel());
                hasMPMLayers = true;
            }
            ArmorLayerMixin foundArmorLayer = (ArmorLayerMixin)list.stream()
                    .filter(t -> t instanceof HumanoidArmorLayer)
                    .findAny()
                    .orElse(null);
            if (slim) {
                armorLayerSlim = foundArmorLayer;
            } else {
                armorLayer = foundArmorLayer;
            }
            if (hasMPMLayers) {
                return;
            }
            list.removeIf(layer -> layer instanceof CapeLayer);
            list.removeIf(layer -> layer instanceof ElytraLayer);
            list.add(Math.min(1, list.size()), new LayerHeadwear(playerRender));
            list.add(new LayerCapeMPM(playerRender));
            list.add(new LayerBackItem(playerRender));
            list.add(new LayerElytraAlt(playerRender, entityModels));
            list.add(new LayerParts(playerRender));
        }
    }

    public static void bindTexture2(ResourceLocation location) {
        if (location == null) {
            return;
        }
        TextureManager manager = Minecraft.getInstance().getTextureManager();
        AbstractTexture textureObject = manager.getTexture(location);
        if (textureObject == null) {
            textureObject = new SimpleTexture(location);
            manager.register(location, textureObject);
        }
        textureObject.bind();
    }

    @Override
    public void executor(Player player, Runnable runnable) {
        Minecraft.getInstance().execute(runnable);
    }

    private void createFolders() {
        File meta;
        File json;
        File check;
        File file = new File(MorePlayerModels.dir, "assets/moreplayermodels");
        if (!file.exists()) {
            file.mkdirs();
        }
        if (!(check = new File(file, "parts")).exists()) {
            check.mkdir();
        }
        if (!(check = new File(file, "textures")).exists()) {
            check.mkdir();
        }
        if (!(check = new File(file, "sounds")).exists()) {
            check.mkdir();
        }
        if (!(json = new File(file, "sounds.json")).exists()) {
            try {
                json.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(json));
                writer.write("{\n\n}");
                writer.close();
            }
            catch (IOException writer) {
                // empty catch block
            }
        }
        if (!(meta = new File(MorePlayerModels.dir, "pack.mcmeta")).exists()) {
            try {
                meta.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(meta));
                writer.write("{\n    \"pack\": {\n        \"description\": \"moreplayermodels map resource pack\",\n        \"pack_format\": 34\n    }\n}");
                writer.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void createMpmPartData(MpmPartData data) {
        data.clientData = new MpmPartDataClient();
    }
}
