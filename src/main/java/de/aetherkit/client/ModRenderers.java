package de.aetherkit.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.aetherkit.AetherKit;
import de.aetherkit.entity.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PigModel;
import net.minecraft.client.renderer.entity.model.SkeletonModel;
import net.minecraft.client.renderer.entity.model.ZombieModel;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

/** Alle Mob-Renderer der Mod. */
public final class ModRenderers {
    private ModRenderers() {}

    private static ResourceLocation tex(String name) {
        return new ResourceLocation(AetherKit.MODID, "textures/entity/" + name + ".png");
    }

    public static class RuinGuardianRenderer extends BipedRenderer<RuinGuardianEntity, ZombieModel<RuinGuardianEntity>> {
        private static final ResourceLocation TEX = tex("ruin_guardian");

        public RuinGuardianRenderer(EntityRendererManager manager) {
            super(manager, new ZombieModel<>(0.0F, false), 0.5F);
        }

        @Override
        public ResourceLocation getTextureLocation(RuinGuardianEntity entity) {
            return TEX;
        }
    }

    public static class CrystalSkeletonRenderer extends BipedRenderer<CrystalSkeletonEntity, SkeletonModel<CrystalSkeletonEntity>> {
        private static final ResourceLocation TEX = tex("crystal_skeleton");

        public CrystalSkeletonRenderer(EntityRendererManager manager) {
            super(manager, new SkeletonModel<>(), 0.5F);
        }

        @Override
        public ResourceLocation getTextureLocation(CrystalSkeletonEntity entity) {
            return TEX;
        }
    }

    public static class BoarRenderer extends MobRenderer<MossbackBoarEntity, PigModel<MossbackBoarEntity>> {
        private static final ResourceLocation TEX = tex("mossback_boar");

        public BoarRenderer(EntityRendererManager manager) {
            super(manager, new PigModel<>(), 0.7F);
        }

        @Override
        public ResourceLocation getTextureLocation(MossbackBoarEntity entity) {
            return TEX;
        }

        @Override
        protected void scale(MossbackBoarEntity entity, MatrixStack stack, float partialTicks) {
            stack.scale(1.15F, 1.15F, 1.15F);
        }
    }

    public static class WispRenderer extends MobRenderer<WispEntity, WispModel> {
        private static final ResourceLocation TEX = tex("wisp");

        public WispRenderer(EntityRendererManager manager) {
            super(manager, new WispModel(), 0.15F);
        }

        @Override
        public ResourceLocation getTextureLocation(WispEntity entity) {
            return TEX;
        }

        @Override
        protected int getBlockLightLevel(WispEntity entity, BlockPos pos) {
            return 15;
        }
    }

    public static class VoidStalkerRenderer extends EndermanRenderer {
        private static final ResourceLocation TEX = tex("void_stalker");

        public VoidStalkerRenderer(EntityRendererManager manager) {
            super(manager);
        }

        @Override
        public ResourceLocation getTextureLocation(EndermanEntity entity) {
            return TEX;
        }
    }

    public static class WardenRenderer extends BipedRenderer<AetherWardenEntity, BipedModel<AetherWardenEntity>> {
        private static final ResourceLocation TEX = tex("aether_warden");

        public WardenRenderer(EntityRendererManager manager) {
            super(manager, new BipedModel<>(0.0F, 0.0F, 64, 64), 1.1F);
        }

        @Override
        public ResourceLocation getTextureLocation(AetherWardenEntity entity) {
            return TEX;
        }

        @Override
        protected void scale(AetherWardenEntity entity, MatrixStack stack, float partialTicks) {
            stack.scale(1.8F, 1.8F, 1.8F);
        }

        @Override
        protected int getBlockLightLevel(AetherWardenEntity entity, BlockPos pos) {
            return Math.max(10, super.getBlockLightLevel(entity, pos));
        }
    }
}
