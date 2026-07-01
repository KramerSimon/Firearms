package com.sio.firearms.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.sio.firearms.entity.TankEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class TankRenderer extends EntityRenderer<TankEntity> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("firearms", "textures/entity/tank.png");

    // Dark green (OD green palette)
    private static final int HULL_R = 45, HULL_G = 65, HULL_B = 30;
    private static final int TURR_R = 55, TURR_G = 75, TURR_B = 38;

    public TankRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 2.5f;
    }

    @Override
    public ResourceLocation getTextureLocation(TankEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(TankEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // Align model: rotate so "front" of hull faces entity yaw direction
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - entityYaw));

        // Sink the hull slightly below the entity origin so the driver's eye position
        // (seated near the origin) sits above the model instead of inside/behind it
        poseStack.translate(0, -0.5, 0);

        VertexConsumer vc = buffer.getBuffer(RenderType.entitySolid(TEXTURE));

        // Hull: 7 wide × 3 tall × 10 long, origin at entity foot position
        renderBox(poseStack, vc, -3.5f, 0f, -5f, 3.5f, 3f, 5f,
                  HULL_R, HULL_G, HULL_B, packedLight);

        // Turret sits on top of hull, rotated separately by turretYaw.
        // turretYaw is already relative to the hull's yaw (see TankTurretPayload), so rotating it
        // here — inside the pose stack already rotated by the hull's (180 - entityYaw) — composes
        // to an absolute turret facing of entityYaw + turretYaw, matching the player's look direction.
        poseStack.pushPose();
        poseStack.translate(0, 3, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getTurretYaw()));

        // Turret box: 3w × 1.5h × 4l
        renderBox(poseStack, vc, -1.5f, 0f, -2f, 1.5f, 1.5f, 2f,
                  TURR_R, TURR_G, TURR_B, packedLight);

        // Cannon barrel: 0.4 wide × 0.4 tall × 5 long, extending forward (−Z)
        renderBox(poseStack, vc, -0.2f, 0.55f, -7f, 0.2f, 0.95f, -2f,
                  HULL_R + 5, HULL_G + 5, HULL_B, packedLight);
        poseStack.popPose();

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    /** Renders a solid-colour axis-aligned box. x1<x2, y1<y2, z1<z2. */
    private static void renderBox(PoseStack poseStack, VertexConsumer vc,
                                  float x1, float y1, float z1,
                                  float x2, float y2, float z2,
                                  int r, int g, int b, int light) {
        PoseStack.Pose pose = poseStack.last();
        // −Y bottom
        quad(pose, vc, x1,y1,z2, x2,y1,z2, x2,y1,z1, x1,y1,z1, r,g,b,  0,-1, 0, light);
        // +Y top
        quad(pose, vc, x1,y2,z1, x2,y2,z1, x2,y2,z2, x1,y2,z2, r,g,b,  0, 1, 0, light);
        // −Z north
        quad(pose, vc, x2,y1,z1, x2,y2,z1, x1,y2,z1, x1,y1,z1, r,g,b,  0, 0,-1, light);
        // +Z south
        quad(pose, vc, x1,y1,z2, x1,y2,z2, x2,y2,z2, x2,y1,z2, r,g,b,  0, 0, 1, light);
        // −X west
        quad(pose, vc, x1,y1,z1, x1,y2,z1, x1,y2,z2, x1,y1,z2, r,g,b, -1, 0, 0, light);
        // +X east
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
