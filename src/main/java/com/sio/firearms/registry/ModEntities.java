package com.sio.firearms.registry;

import com.sio.firearms.Firearms;
import com.sio.firearms.entity.BulletEntity;
import com.sio.firearms.entity.FirePatchEntity;
import com.sio.firearms.entity.FlameEntity;
import com.sio.firearms.entity.GrenadeEntity;
import com.sio.firearms.entity.MolotovEntity;
import com.sio.firearms.entity.NitroglycerinEntity;
import com.sio.firearms.entity.SeaMineEntity;
import com.sio.firearms.entity.SmokeGrenadeEntity;
import com.sio.firearms.entity.TankCannonShellEntity;
import com.sio.firearms.entity.TankEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Firearms.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<BulletEntity>> BULLET =
            ENTITY_TYPES.register("bullet", () -> EntityType.Builder.<BulletEntity>of(BulletEntity::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .clientTrackingRange(64)
                    .build(Firearms.MOD_ID + ":bullet"));

    public static final DeferredHolder<EntityType<?>, EntityType<GrenadeEntity>> GRENADE =
            ENTITY_TYPES.register("grenade", () -> EntityType.Builder.<GrenadeEntity>of(GrenadeEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(64)
                    .build(Firearms.MOD_ID + ":grenade"));

    public static final DeferredHolder<EntityType<?>, EntityType<SmokeGrenadeEntity>> SMOKE_GRENADE =
            ENTITY_TYPES.register("smoke_grenade", () -> EntityType.Builder.<SmokeGrenadeEntity>of(SmokeGrenadeEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(64)
                    .build(Firearms.MOD_ID + ":smoke_grenade"));

    public static final DeferredHolder<EntityType<?>, EntityType<SeaMineEntity>> SEA_MINE =
            ENTITY_TYPES.register("sea_mine", () -> EntityType.Builder.<SeaMineEntity>of(SeaMineEntity::new, MobCategory.MISC)
                    .sized(0.8F, 0.8F)
                    .clientTrackingRange(64)
                    .build(Firearms.MOD_ID + ":sea_mine"));

    public static final DeferredHolder<EntityType<?>, EntityType<TankEntity>> TANK =
            ENTITY_TYPES.register("tank", () -> EntityType.Builder.<TankEntity>of(TankEntity::new, MobCategory.MISC)
                    .sized(5.0F, 3.0F)
                    .clientTrackingRange(128)
                    .updateInterval(1)
                    .build(Firearms.MOD_ID + ":tank"));

    public static final DeferredHolder<EntityType<?>, EntityType<TankCannonShellEntity>> TANK_CANNON_SHELL =
            ENTITY_TYPES.register("tank_cannon_shell", () -> EntityType.Builder.<TankCannonShellEntity>of(TankCannonShellEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(64)
                    .build(Firearms.MOD_ID + ":tank_cannon_shell"));

    public static final DeferredHolder<EntityType<?>, EntityType<FlameEntity>> FLAME =
            ENTITY_TYPES.register("flame", () -> EntityType.Builder.<FlameEntity>of(FlameEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build(Firearms.MOD_ID + ":flame"));

    public static final DeferredHolder<EntityType<?>, EntityType<MolotovEntity>> MOLOTOV =
            ENTITY_TYPES.register("molotov", () -> EntityType.Builder.<MolotovEntity>of(MolotovEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(64)
                    .build(Firearms.MOD_ID + ":molotov"));

    public static final DeferredHolder<EntityType<?>, EntityType<FirePatchEntity>> FIRE_PATCH =
            ENTITY_TYPES.register("fire_patch", () -> EntityType.Builder.<FirePatchEntity>of(FirePatchEntity::new, MobCategory.MISC)
                    .sized(3.0F, 1.5F)
                    .clientTrackingRange(64)
                    .build(Firearms.MOD_ID + ":fire_patch"));

    public static final DeferredHolder<EntityType<?>, EntityType<NitroglycerinEntity>> NITROGLYCERIN =
            ENTITY_TYPES.register("nitroglycerin", () -> EntityType.Builder.<NitroglycerinEntity>of(NitroglycerinEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(64)
                    .build(Firearms.MOD_ID + ":nitroglycerin"));
}
