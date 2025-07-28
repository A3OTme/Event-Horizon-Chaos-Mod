package com.a3ot.disastermod.mixin.client.model;

import com.a3ot.disastermod.handlers.client.ClientVariables;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin {

    @Final
    @Shadow public ModelPart rightArm;
    @Final
    @Shadow public ModelPart leftArm;

    @Inject(method = "setupAnim*", at = @At("TAIL"))
    private void setAngles(LivingEntity livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if(ClientVariables.oldPlayerAnimation){
            this.rightArm.xRot = Mth.cos(f * 0.6662F + 3.1415927F) * 2.0F * g;
            this.leftArm.xRot = Mth.cos(f * 0.6662F) * 2.0F * g;
            this.rightArm.zRot = (Mth.cos(f * 0.2312F) + 1.0F) * 1.0F * g;
            this.leftArm.zRot = (Mth.cos(f * 0.2812F) - 1.0F) * 1.0F * g;
        }
    }
}

