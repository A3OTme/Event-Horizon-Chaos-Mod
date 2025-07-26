package com.a3ot.disastermod.mixin.accessor;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HumanoidModel.class)
public interface HumanoidModelAccessor {
    @Accessor("head") ModelPart getHead();
//    @Accessor("body") ModelPart getBody();
//    @Accessor("rightArm") ModelPart getRightArm();
//    @Accessor("leftArm") ModelPart getLeftArm();
//    @Accessor("rightLeg") ModelPart getRightLeg();
//    @Accessor("leftLeg") ModelPart getLeftLeg();
}
