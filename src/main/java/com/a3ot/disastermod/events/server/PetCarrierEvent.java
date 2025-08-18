package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventType;
import com.a3ot.disastermod.events.utils.ModCatVariants;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;

import java.util.List;


public class PetCarrierEvent implements AbstractEvent { //todo add Ender Backpack Layer and Upgrade Cats Textures
    private static final List<Holder<CatVariant>> CAT_VARIANTS = ModCatVariants.getAllCatVariants();
    public static final String PET_CARRIER_CAT_TAG = "disastermod:pet_carrier_cat";

    @Override
    public void onStart(ServerLevel level) {
        level.players().stream().filter(Utils::isValidPlayer).forEach(player -> {
            Cat cat = new Cat(EntityType.CAT, player.level());
            cat.setPos(player.position());
            cat.tame(player);
            cat.setOwnerUUID(player.getUUID());
            cat.setVariant(CAT_VARIANTS.get(level.getRandom().nextInt(CAT_VARIANTS.size())));
            cat.addTag(PET_CARRIER_CAT_TAG);
            level.addFreshEntity(cat);
        });
    }

    public static boolean isPetCarrierCat(Cat cat) {
        return cat.getTags().contains(PET_CARRIER_CAT_TAG);
    }

    public static void openOwnerEnderChest(Cat cat, Player player) {
        if (!isPetCarrierCat(cat)) return;
        PlayerEnderChestContainer enderChestInventory = player.getEnderChestInventory();
        player.openMenu(new SimpleMenuProvider((containerId, inventory, player1) ->
                ChestMenu.threeRows(containerId, inventory, enderChestInventory), Component.translatable("container.enderchest")));
        cat.level().playSound(null, cat.getX(), cat.getY(), cat.getZ(), SoundEvents.ENDER_CHEST_OPEN, SoundSource.MASTER);
        player.awardStat(Stats.OPEN_ENDERCHEST);
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 0;
    }

    @Override
    public EventType getType() {
        return EventType.POSITIVE;
    }
}