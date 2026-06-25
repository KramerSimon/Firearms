package com.sio.firearms.registry;

import com.sio.firearms.Firearms;
import com.sio.firearms.entity.BulletEntity;
import com.sio.firearms.entity.GrenadeEntity;
import com.sio.firearms.entity.SeaMineEntity;
import com.sio.firearms.entity.SmokeGrenadeEntity;
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
}
