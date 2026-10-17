package dev.towka.visuals.mixin;

import dev.towka.visuals.PlayerVisualProfile;
import dev.towka.visuals.PlayerVisualStore;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityModel.class)
public abstract class PlayerModelMixin {
    @Inject(method = "setAngles(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;)V", at = @At("TAIL"))
    private void towka$bodyScale(PlayerEntityRenderState state, CallbackInfo ci) {
        PlayerVisualProfile p = PlayerVisualStore.getForName(state.name);
        float head = p == null ? 1f : p.head;
        float body = p == null ? 1f : p.body;
        float arms = p == null ? 1f : p.arms;
        float legs = p == null ? 1f : p.legs;
        PlayerEntityModel model = (PlayerEntityModel)(Object)this;
        scale(model.head, head);
        scale(model.hat, head);
        scale(model.body, body);
        scale(model.jacket, body);
        scale(model.leftArm, arms);
        scale(model.rightArm, arms);
        scale(model.leftSleeve, arms);
        scale(model.rightSleeve, arms);
        scale(model.leftLeg, legs);
        scale(model.rightLeg, legs);
        scale(model.leftPants, legs);
        scale(model.rightPants, legs);
    }

    private static void scale(ModelPart part, float amount) {
        part.xScale = amount;
        part.yScale = amount;
        part.zScale = amount;
    }
}
