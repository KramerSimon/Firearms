package com.sio.firearms.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.sio.firearms.entity.AircraftEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class AircraftRenderer extends EntityRenderer<AircraftEntity> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/entity/aircraft.png");

    // Dark grey stealth paint
    private static final int R = 55, G = 58, B = 60;
    // Slightly lighter for wings/canards
    private static final int WR = 65, WG = 68, WB = 72;

    public AircraftRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 3.0f;
    }

    @Override
    public ResourceLocation getTextureLocation(AircraftEntity entity) { return TEXTURE; }

    @Override
    public void render(AircraftEntity entity, float entityYaw, float partialTick,
                       PoseStack ps, MultiBufferSource buf, int light) {
        ps.pushPose();

        // Rotate so nose of aircraft faces yaw direction
        ps.mulPose(Axis.YP.rotationDegrees(180f - entityYaw));

        // Apply flight pitch tilt
        float pitch = entity.getAircraftPitch();
        ps.mulPose(Axis.XP.rotationDegrees(pitch));

        VertexConsumer vc = buf.getBuffer(RenderType.entitySolid(TEXTURE));

        // ── Fuselage: 1.2W × 1.5H × 8L (slender fighter body) ───────────────
        renderBox(ps, vc, -0.6f, 0f, -4f, 0.6f, 1.5f, 4f, R, G, B, light);

        // ── Main delta wings: 5W × 0.3H × 4L each, swept back ────────────────
        // Left wing (−X)
        renderBox(ps, vc, -5f,  0.5f, -1f, -0.6f, 0.8f,  3f, WR, WG, WB, light);
        // Right wing (+X)
        renderBox(ps, vc,  0.6f, 0.5f, -1f,  5f,  0.8f,  3f, WR, WG, WB, light);

        // ── Canards (small forward wings) ─────────────────────────────────────
        // Left canard
        renderBox(ps, vc, -2.5f, 0.6f, -3.5f, -0.6f, 0.9f, -2.5f, WR, WG, WB, light);
        // Right canard
        renderBox(ps, vc,  0.6f, 0.6f, -3.5f,  2.5f, 0.9f, -2.5f, WR, WG, WB, light);

        // ── Twin tail fins (vertical stabilizers) ─────────────────────────────
        // Left fin
        renderBox(ps, vc, -1.2f, 1.5f, 2f, -0.8f, 3.0f, 4f, R, G, B, light);
        // Right fin
        renderBox(ps, vc,  0.8f, 1.5f, 2f,  1.2f, 3.0f, 4f, R, G, B, light);

        // ── Twin engines (nacelles under wing roots) ──────────────────────────
        // Left engine
        renderBox(ps, vc, -1.5f, 0f, 0f, -0.8f, 0.8f, 4.2f, R - 5, G - 5, B, light);
        // Right engine
        renderBox(ps, vc,  0.8f, 0f, 0f,  1.5f, 0.8f, 4.2f, R - 5, G - 5, B, light);

        // ── Nose cone ─────────────────────────────────────────────────────────
        renderBox(ps, vc, -0.35f, 0.3f, -5f, 0.35f, 1.1f, -4f, R + 8, G + 8, B + 8, light);

        ps.popPose();
        super.render(entity, entityYaw, partialTick, ps, buf, light);
    }

    private static void renderBox(PoseStack ps, VertexConsumer vc,
                                  float x1, float y1, float z1,
                                  float x2, float y2, float z2,
                                  int r, int g, int b, int light) {
        PoseStack.Pose pose = ps.last();
        quad(pose, vc, x1,y1,z2, x2,y1,z2, x2,y1,z1, x1,y1,z1, r,g,b,  0,-1, 0, light);
        quad(pose, vc, x1,y2,z1, x2,y2,z1, x2,y2,z2, x1,y2,z2, r,g,b,  0, 1, 0, light);
        quad(pose, vc, x2,y1,z1, x2,y2,z1, x1,y2,z1, x1,y1,z1, r,g,b,  0, 0,-1, light);
        quad(pose, vc, x1,y1,z2, x1,y2,z2, x2,y2,z2, x2,y1,z2, r,g,b,  0, 0, 1, light);
        quad(pose, vc, x1,y1,z1, x1,y2,z1, x1,y2,z2, x1,y1,z2, r,g,b, -1, 0, 0, light);
        quad(pose, vc, x2,y1,z2, x2,y2,z2, x2,y2,z1, x2,y1,z1, r,g,b,  1, 0, 0, light);
    }

    private static void quad(PoseStack.Pose pose, VertexConsumer vc,
                             float x0, float y0, float z0, float x1, float y1, float z1,
                             float x2, float y2, float z2, float x3, float y3, float z3,
                             int r, int g, int b, float nx, float ny, float nz, int light) {
        vtx(pose, vc, x0,y0,z0, r,g,b, 0,0, nx,ny,nz, light);
        vtx(pose, vc, x1,y1,z1, r,g,b, 0,1, nx,ny,nz, light);
        vtx(pose, vc, x2,y2,z2, r,g,b, 1,1, nx,ny,nz, light);
        vtx(pose, vc, x3,y3,z3, r,g,b, 1,0, nx,ny,nz, light);
    }

    private static void vtx(PoseStack.Pose pose, VertexConsumer vc,
                            float x, float y, float z, int r, int g, int b,
                            float u, float v, float nx, float ny, float nz, int light) {
        vc.addVertex(pose, x, y, z)
          .setColor(r, g, b, 255)
          .setUv(u, v)
          .setOverlay(OverlayTexture.NO_OVERLAY)
          .setLight(light)
          .setNormal(pose, nx, ny, nz);
    }
}
