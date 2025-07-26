package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.data.ModDamageTypes;
import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.subclasses.IActiveStateEvent;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class NyctophobiaEvent implements AbstractEvent, IActiveStateEvent {
    private static boolean active = false;

    @Override
    public void setActive() {
        active = true;
    }

    @Override
    public void setInactive() {
        active = false;
    }

    public static boolean isActive() {
        return active;
    }

    @Override
    public void onStart(ServerLevel level) {
        level.players().stream().filter(Utils::isPlayerValid).forEach(player -> {
            BlockPos pos = player.getOnPos();
            if (!(level.dimension() == Level.NETHER) && Utils.lightManager(pos.above(), level) < 10)
                Utils.blockFilling(level, pos, pos, Blocks.SHROOMLIGHT);
        });
        this.setActive();
    }

    @Override
    public void onEnd(ServerLevel level) {
        this.setInactive();
    }

    public static void playerTick(Player player, Level level){
        if(!active) return;
        if(!Utils.isPlayerValid((ServerPlayer) player)) return;
        BlockPos playerPos = player.blockPosition();
        int lightLevel = Utils.lightManager(playerPos, level);
        if (lightLevel < 3) {
            DamageSource botanophobiaDamage = level.damageSources().source(ModDamageTypes.NYCTOPHOBIA);
            player.hurt(botanophobiaDamage, 2.0F);
        }
    }

    @Override
    public void playerRespawnOrJoin(Player player, Level level){
        if(!(Utils.isPlayerValid(player))) return;
        BlockPos pos = player.getOnPos();
        if (!(level.dimension() == Level.NETHER) && Utils.lightManager(pos.above(), level) < 10)
            Utils.blockFilling((ServerLevel) level, pos, pos, Blocks.SHROOMLIGHT);
    }

    @Override
    public boolean conflictsWith(AbstractEvent other) {
        return other instanceof PhotosensitizationEvent;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.DARK_GRAY;
    }

    public float getPitch() {
        return 0.7F;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.WARDEN_AGITATED;
    }
}
