/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.resources.ResourceLocation
 *  org.joml.Matrix4f
 *  org.lwjgl.system.MemoryUtil
 */
package noppes.mpm.shared.client.model.util;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.FloatBuffer;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import noppes.mpm.shared.util.NopVector2i;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

public class BatchRenderer {
    private static final FloatBuffer MATRIX_BUFFER = MemoryUtil.memAllocFloat((int)16);
    public static RenderType lastType = null;
    private static final BatchRenderer instance = new BatchRenderer();
    private final Map<RenderType, List<Batch>> queue = new LinkedHashMap<RenderType, List<Batch>>();

    public static BatchRenderer getInstance() {
        return instance;
    }

    public void add(RenderType renderType, ResourceLocation resource, int id, VertexFormat format, Matrix4f matrix, int vertexCount, NopVector2i texPos, int light, int overlay, float red, float green, float blue, float alpha) {
        if (renderType == null) {
            renderType = lastType;
        }
        this.queue.computeIfAbsent(renderType, k -> new LinkedList()).add(new Batch(resource, id, format, matrix, vertexCount, texPos, light, overlay, red, green, blue, alpha));
    }

    public void draw() {
        this.queue.clear();
    }

    class Batch {
        final Matrix4f matrix;
        final int vertexCount;
        final ResourceLocation resource;
        final int id;
        final VertexFormat format;
        final int light1;
        final int light2;
        final int overlay1;
        final int overlay2;
        final float red;
        final float green;
        final float blue;
        final float alpha;
        final NopVector2i texPos;

        public Batch(ResourceLocation resource, int id, VertexFormat format, Matrix4f matrix, int vertexCount, NopVector2i texPos, int light, int overlay, float red, float green, float blue, float alpha) {
            this.resource = resource;
            this.id = id;
            this.format = format;
            this.matrix = matrix;
            this.vertexCount = vertexCount;
            this.texPos = texPos;
            this.light1 = light & 0xFFFF;
            this.light2 = light >> 16 & 0xFFFF;
            this.overlay1 = overlay & 0xFFFF;
            this.overlay2 = overlay >> 16 & 0xFFFF;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }
    }
}
