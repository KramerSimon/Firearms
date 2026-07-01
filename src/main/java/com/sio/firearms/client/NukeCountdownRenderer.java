package com.sio.firearms.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sio.firearms.entity.NukeBombEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class NukeCountdownRenderer extends EntityRenderer<NukeBombEntity> {

    // Placeholder shape — no dedicated model yet, so bind to a texture guaranteed to exist
    // (the render type only needs a valid binding; body colour comes from per-vertex colour).
    private static final ResourceLocation TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/misc/unknown_pack.png");

    private static final int BODY_R = 42, BODY_G = 42, BODY_B = 46;
    private static final int BAND_R = 230, BAND_G = 200, BAND_B = 20;

    public NukeCountdownRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 0.7f;
    }

    @Override
    public ResourceLocation getTextureLocation(NukeBombEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(NukeBombEntity entity, float entityYaw, float partialTick,
                        PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        VertexConsumer vc = buffer.getBuffer(RenderType.entitySolid(TEXTURE));

        // Body: 1 wide × 2 tall × 1 deep, origin at feet
        renderBox(poseStack, vc, -0.5f, 0f, -0.5f, 0.5f, 2.0f, 0.5f,
                BODY_R, BODY_G, BODY_B, packedLight);
        // Radiation-warning band around the middle
        renderBox(poseStack, vc, -0.51f, 0.9f, -0.51f, 0.51f, 1.1f, 0.51f,
                BAND_R, BAND_G, BAND_B, packedLight);

        if (entity.isArmed()) {
            spawnWarningGlow(entity);
            renderCountdownText(entity, poseStack, buffer);
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private void spawnWarningGlow(NukeBombEntity entity) {
        if (entity.level().random.nextInt(3) != 0) return;
        double ox = (entity.level().random.nextDouble() - 0.5) * 1.4;
        double oy = entity.level().random.nextDouble() * 2.2;
        double oz = (entity.level().random.nextDouble() - 0.5) * 1.4;
        entity.level().addParticle(new DustParticleOptions(new Vector3f(1.0f, 0.15f, 0.05f), 1.4f),
                entity.getX() + ox, entity.getY() + oy, entity.getZ() + oz, 0, 0.02, 0);
    }

    private void renderCountdownText(NukeBombEntity entity, PoseStack poseStack, MultiBufferSource buffer) {
        // Blink twice per second
        if ((entity.getCountdown() / 5) % 2 != 0) return;

        int secondsLeft = (entity.getCountdown() + 19) / 20;
        String text = String.valueOf(secondsLeft);

        poseStack.pushPose();
        poseStack.translate(0, 2.6, 0);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.scale(-0.03f, -0.03f, 0.03f);

        Font font = Minecraft.getInstance().font;
        Matrix4f matrix = poseStack.last().pose();
        float halfWidth = font.width(text) / 2f;
        font.drawInBatch(text, -halfWidth, 0, 0xFF2020, false, matrix, buffer,
                Font.DisplayMode.NORMAL, 0, 0xF000F0);

        poseStack.popPose();
    }

    /** Renders a solid-colour axis-aligned box. x1<x2, y1<y2, z1<z2. */
    private static void renderBox(PoseStack poseStack, VertexConsumer vc,
                                  float x1, float y1, float z1,
                                  float x2, float y2, float z2,
                                  int r, int g, int b, int light) {
        PoseStack.Pose pose = poseStack.last();
        quad(pose, vc, x1,y1,z2, x2,y1,z2, x2,y1,z1, x1,y1,z1, r,g,b,  0,-1, 0, light);
        quad(pose, vc, x1,y2,z1, x2,y2,z1, x2,y2,z2, x1,y2,z2, r,g,b,  0, 1, 0, light);
        quad(pose, vc, x2,y1,z1, x2,y2,z1, x1,y2,z1, x1,y1,z1, r,g,b,  0, 0,-1, light);
        quad(pose, vc, x1,y1,z2, x1,y2,z2, x2,y2,z2, x2,y1,z2, r,g,b,  0, 0, 1, light);
        quad(pose, vc, x1,y1,z1, x1,y2,z1, x1,y2,z2, x1,y1,z2, r,g,b, -1, 0, 0, light);
        quad(pose, vc, x2,y1,z2, x2,y2,z2, x2,y2,z1, x2,y1,z1, r,g,b,  1, 0, 0, light);
    }

    private static void quad(PoseStack.Pose pose, VertexConsumer vc,
                             float x0, float y0, float z0,
                             float x1, float y1, float z1,
                             float x2, float y2, float z2,
                             float x3, float y3, float z3,
                             int r, int g, int b,
                             float nx, float ny, float nz, int light) {
        vtx(pose, vc, x0, y0, z0, r, g, b, 0, 0, nx, ny, nz, light);
        vtx(pose, vc, x1, y1, z1, r, g, b, 0, 1, nx, ny, nz, light);
        vtx(pose, vc, x2, y2, z2, r, g, b, 1, 1, nx, ny, nz, light);
        vtx(pose, vc, x3, y3, z3, r, g, b, 1, 0, nx, ny, nz, light);
    }

    private static void vtx(PoseStack.Pose pose, VertexConsumer vc,
                            float x, float y, float z,
                            int r, int g, int b, float u, float v,
                            float nx, float ny, float nz, int light) {
        vc.addVertex(pose, x, y, z)
          .setColor(r, g, b, 255)
          .setUv(u, v)
          .setOverlay(OverlayTexture.NO_OVERLAY)
          .setLight(light)
          .setNormal(pose, nx, ny, nz);
    }
}
