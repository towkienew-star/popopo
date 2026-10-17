package dev.towka.visuals.mixin;

import dev.towka.visuals.PlayerVisualProfile;
import dev.towka.visuals.PlayerVisualStore;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerRendererMixin {
    @Inject(method = "scale(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;)V", at = @At("HEAD"))
    private void towka$visualScale(PlayerEntityRenderState state, MatrixStack matrices, CallbackInfo ci) {
        PlayerVisualProfile profile = PlayerVisualStore.getForName(state.name);
        if (profile == null) return;
        matrices.scale(profile.overall * profile.width, profile.overall * profile.height, profile.overall * profile.width);
        if (profile.bounce) {
            double wave = Math.sin(System.currentTimeMillis() / 120.0D) * 0.12D;
            matrices.translate(0.0D, wave, 0.0D);
        }
        if (profile.spin) {
            float angle = (System.currentTimeMillis() % 4000L) / 4000.0F * 360.0F;
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle));
        }
    }
}
