package de.aetherkit.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.aetherkit.entity.WispEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

/** Irrlicht: leuchtender Kern, durchscheinende Hülle und zwei flatternde Flügelchen. 32x32-Textur. */
public class WispModel extends EntityModel<WispEntity> {
    private final ModelRenderer core;
    private final ModelRenderer shell;
    private final ModelRenderer leftWing;
    private final ModelRenderer rightWing;

    public WispModel() {
        super(RenderType::entityTranslucent);
        texWidth = 32;
        texHeight = 32;

        core = new ModelRenderer(this, 0, 0);
        core.addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4);
        core.setPos(0.0F, 18.0F, 0.0F);

        shell = new ModelRenderer(this, 0, 8);
        shell.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6);
        shell.setPos(0.0F, 18.0F, 0.0F);

        leftWing = new ModelRenderer(this, 16, 0);
        leftWing.addBox(0.0F, -2.0F, 0.0F, 5, 4, 0);
        leftWing.setPos(2.0F, 17.0F, 1.0F);

        rightWing = new ModelRenderer(this, 16, 4);
        rightWing.addBox(-5.0F, -2.0F, 0.0F, 5, 4, 0);
        rightWing.setPos(-2.0F, 17.0F, 1.0F);
    }

    @Override
    public void setupAnim(WispEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float bob = MathHelper.sin(ageInTicks * 0.15F) * 1.2F;
        core.y = 18.0F + bob;
        shell.y = 18.0F + bob;
        shell.yRot = ageInTicks * 0.05F;
        shell.xRot = ageInTicks * 0.03F;
        float flap = MathHelper.cos(ageInTicks * 1.3F) * 0.6F;
        leftWing.y = 17.0F + bob;
        rightWing.y = 17.0F + bob;
        leftWing.yRot = -0.4F + flap;
        rightWing.yRot = 0.4F - flap;
    }

    @Override
    public void renderToBuffer(MatrixStack stack, IVertexBuilder buffer, int light, int overlay, float r, float g, float b, float a) {
        core.render(stack, buffer, light, overlay, r, g, b, a);
        shell.render(stack, buffer, light, overlay, r, g, b, a * 0.55F);
        leftWing.render(stack, buffer, light, overlay, r, g, b, a * 0.8F);
        rightWing.render(stack, buffer, light, overlay, r, g, b, a * 0.8F);
    }
}
