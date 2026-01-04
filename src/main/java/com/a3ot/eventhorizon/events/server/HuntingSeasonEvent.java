package com.a3ot.eventhorizon.events.server;

import com.a3ot.eventhorizon.EventHorizon;
import com.a3ot.eventhorizon.data.CustomRabbitVariant;
import com.a3ot.eventhorizon.data.ModDataComponents;
import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.EventType;
import com.a3ot.eventhorizon.events.utils.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

@EventBusSubscriber(modid = EventHorizon.MOD_ID)
public class HuntingSeasonEvent implements AbstractEvent {
    public enum PreciousRabbitType {
        IRON(
                ResourceLocation.fromNamespaceAndPath(EventHorizon.MOD_ID, "textures/entity/rabbit/iron.png"),
                Items.IRON_INGOT,
                5, 10
        ),
        GOLD(
                ResourceLocation.fromNamespaceAndPath(EventHorizon.MOD_ID, "textures/entity/rabbit/gold.png"),
                Items.GOLD_INGOT,
                5, 10
        ),
        DIAMOND(
                ResourceLocation.fromNamespaceAndPath(EventHorizon.MOD_ID, "textures/entity/rabbit/diamond.png"),
                Items.DIAMOND,
                5, 10
        ),
        EMERALD(
                ResourceLocation.fromNamespaceAndPath(EventHorizon.MOD_ID, "textures/entity/rabbit/emerald.png"),
                Items.EMERALD,
                5, 10
        ),
        NETHERITE(
                ResourceLocation.fromNamespaceAndPath(EventHorizon.MOD_ID, "textures/entity/rabbit/netherite.png"),
                Items.NETHERITE_INGOT,
                5, 10
        );

        private final ResourceLocation textureLocation;
        private final Item dropItem;
        private final int minDrops;
        private final int maxDrops;

        PreciousRabbitType(ResourceLocation textureLocation, Item dropItem, int minDrops, int maxDrops) {
            this.textureLocation = textureLocation;
            this.dropItem = dropItem;
            this.minDrops = minDrops;
            this.maxDrops = maxDrops;
        }

        public ResourceLocation getTextureLocation() {
            return textureLocation;
        }

        public Item getDropItem() {
            return dropItem;
        }

        public int getMinCountDrops() {
            return minDrops;
        }

        public int getMaxCountDrops() {
            return maxDrops;
        }

        public int getRandomCountDrops(RandomSource random){
            return random.nextInt(this.minDrops, this.maxDrops + 1);
        }
    }

    @Override
    public void onStart(ServerLevel level) {
        RandomSource random = level.getRandom();
        level.players().stream().filter(Utils::isValidPlayer).forEach(player -> {
            for (int i = 0; i < 12; i++) {
                PreciousRabbitType selectedType = PreciousRabbitType.values()[random.nextInt(PreciousRabbitType.values().length)];
                Rabbit rabbit = new Rabbit(EntityType.RABBIT, level);
                rabbit.setPos(player.position());
                CustomRabbitVariant customVariant = new CustomRabbitVariant(selectedType.getTextureLocation());
                rabbit.setData(ModDataComponents.CUSTOM_RABBIT_VARIANT.get(), customVariant);
                ItemStack dropIndicator = new ItemStack(selectedType.getDropItem(), selectedType.getRandomCountDrops(random));
                rabbit.setItemSlot(EquipmentSlot.MAINHAND, dropIndicator);
                AttributeInstance safeFallDistanceAttribute = rabbit.getAttribute(Attributes.SAFE_FALL_DISTANCE);
                if (safeFallDistanceAttribute != null) {
                    AttributeModifier modifier = new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath(EventHorizon.MOD_ID, "precious_rabbit"),
                            1024.0,
                            AttributeModifier.Operation.ADD_VALUE
                    );
                    safeFallDistanceAttribute.addTransientModifier(modifier);
                }
                level.addFreshEntity(rabbit);
            }
        });
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity() instanceof Rabbit rabbit) {
            ItemStack dropIndicator = rabbit.getItemBySlot(EquipmentSlot.MAINHAND);
            if (!dropIndicator.isEmpty()) {
                ItemStack specialDrop = new ItemStack(dropIndicator.getItem(), dropIndicator.getCount());
                ItemEntity dropItemEntity = new net.minecraft.world.entity.item.ItemEntity(
                        rabbit.level(),
                        rabbit.getX(),
                        rabbit.getY(),
                        rabbit.getZ(),
                        specialDrop
                );
                event.getDrops().add(dropItemEntity);
            }
        }
    }

    public static void livingDamage(Entity entity){
        if (entity instanceof Rabbit rabbit && rabbit.hasData(ModDataComponents.CUSTOM_RABBIT_VARIANT.get()) && rabbit.isAlive()) {
            if (Utils.isOnCooldown(rabbit)) return;
            Utils.ChorusTeleport(rabbit, rabbit.level(), 16, 24);
            MobEffectInstance glowing = new MobEffectInstance(MobEffects.GLOWING, 60, 0, false, false);
            rabbit.addEffect(glowing);
        }
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 0.0;
    }

    @Override
    public EventType getType() {
        return EventType.POSITIVE;
    }
}