package com.a3ot.disastermod.mixin;

import com.a3ot.disastermod.api.event.FluidCollisionEvent;
import com.a3ot.disastermod.events.server.WideMobsEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Entity.class)
public class EntityMixin {

    /**
     * Original source: <a href="https://github.com/Octo-Studios/relics/blob/1.21.0/src/main/java/it/hurts/sskirillss/relics/mixin/EntityMixin.java ">Octo-Studios/relics (EntityMixin.java)</a>
     **/
    @ModifyVariable(method = "move", ordinal = 1, index = 3, name = "vec32",
            at = @At(value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/entity/Entity;collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"))
    public Vec3 fluidCollision(Vec3 original) {
        if (!((Entity) (Object) this instanceof LivingEntity entity))
            return original;

        Level level = entity.level();
        BlockPos sourcePos = entity.blockPosition();

        if (original.y > 0) return original;
        BlockPos entityPos = entity.blockPosition();
        BlockPos feetPos = entityPos.below();

        FluidState fluidAtEntity = level.getFluidState(entityPos);

        if (fluidAtEntity.isEmpty()) return original;

        int[][] offsets = {
                {1, 0, 1}, {1, 0, 0}, {1, -1, 0}, {1, 0, -1},
                {0, 0, 1}, {0, 0, 0}, {0, -1, 0}, {0, 0, -1},
                {-1, 0, 1}, {-1, 0, 0}, {-1, -1, 0}, {-1, 0, -1}
        };

        double highestValue = original.y;
        FluidState highestFluid = null;

        for (int[] offset : offsets) {
            BlockPos pos = sourcePos.offset(offset[0], offset[1], offset[2]);
            FluidState fluidState = level.getFluidState(pos);

            if (fluidState.isEmpty()) continue;

            VoxelShape shape = Shapes.block().move(pos.getX(), pos.getY() + fluidState.getOwnHeight(), pos.getZ());
            if (Shapes.joinIsNotEmpty(shape, Shapes.create(entity.getBoundingBox().inflate(0.5)), BooleanOp.AND)) {
                double height = shape.max(Direction.Axis.Y) - entity.getY() - 1;
                if (highestValue < height) {
                    highestValue = height;
                    highestFluid = fluidState;
                }
            }
        }

        if (highestFluid == null) return original;

        FluidCollisionEvent event = new FluidCollisionEvent(entity, highestFluid);
        NeoForge.EVENT_BUS.post(event);

        if (event.isCanceled() && !entity.isShiftKeyDown()) {
            entity.fallDistance = 0F;
            entity.setOnGround(true);
            return new Vec3(original.x, highestValue, original.z);
        }

        return original;
    }

    @Inject(method = "getBoundingBox", at = @At("RETURN"), cancellable = true)
    public void modifyHitbox(CallbackInfoReturnable<AABB> cir) {
        Entity entity = (Entity) (Object) this;
        WideMobsEvent.modifyHitbox(cir, entity);
    }
}
