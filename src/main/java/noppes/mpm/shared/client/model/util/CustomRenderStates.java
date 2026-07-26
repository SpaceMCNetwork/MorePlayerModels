package noppes.mpm.shared.client.model.util;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.joml.Vector4f;

/** Small 1.21-compatible rendering facade used by the legacy model code. */
public class CustomRenderStates extends RenderStateShard {
    public static final Vector4f WHITE = new Vector4f(1, 1, 1, 1);
    public static final VertexFormat POS_TEX_NORMAL = DefaultVertexFormat.NEW_ENTITY;
    public static VertexFormat POS_COL_TEX_LIGHT_FADE_NORMAL = DefaultVertexFormat.NEW_ENTITY;
    public static VertexFormat POS_COL_TEX_NORMAL = DefaultVertexFormat.NEW_ENTITY;
    public static ShaderInstance posTexNormalShader;
    public static final RenderType OBJ_OUTLINE_RENDER_TYPE = RenderType.outline(ResourceLocation.withDefaultNamespace("textures/misc/white.png"));
    private static final RenderType[] OBJ_RENDER_TYPES = new RenderType[BLEND.values().length * 2];

    public CustomRenderStates(String name, Runnable setup, Runnable clear) {
        super(name, setup, clear);
    }

    public static RenderType entityCutout(ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(texture);
    }

    public static RenderType getObjVBORenderType(int blending, boolean glow) {
        return getObjRenderType(ResourceLocation.withDefaultNamespace("textures/misc/white.png"), blending, glow);
    }

    public static RenderType getObjRenderType(ResourceLocation texture, int blending, boolean glow) {
        return RenderType.entityTranslucent(texture);
    }

    public static RenderType getObjColorOnlyRenderType(ResourceLocation texture, int blending, boolean glow) {
        return RenderType.entityTranslucent(texture);
    }

    public static RenderType getObjOutlineRenderType(ResourceLocation texture) {
        return RenderType.outline(texture);
    }

    public static RenderType getSpriteRenderType(ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }

    public enum BLEND {
        NORMAL(0), ADD(1), SUB(2);
        public final int id;
        BLEND(int id) { this.id = id; }
        public int getValue() { return id; }
    }
}
