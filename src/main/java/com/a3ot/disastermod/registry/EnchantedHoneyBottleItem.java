package com.a3ot.disastermod.registry;

import com.a3ot.disastermod.ServerTick;
import com.a3ot.disastermod.network.packet.ClientEventPacket;
import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import java.util.List;
import java.util.Map;

public class EnchantedHoneyBottleItem extends HoneyBottleItem {

    public EnchantedHoneyBottleItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        ItemStack result = super.finishUsingItem(stack, level, entityLiving);

        if (entityLiving instanceof ServerPlayer serverPlayer) {
            ServerLevel serverLevel = (ServerLevel) level;

            Map<AbstractEvent, Integer> activeEvents = ServerTick.getActiveEvents();

            List<AbstractEvent> negativeEvents = activeEvents.keySet().stream()
                    .filter(event -> event.getType() == EventType.NEGATIVE)
                    .toList();

            if (!negativeEvents.isEmpty()) {
                AbstractEvent eventToRemove = negativeEvents.get(
                        level.random.nextInt(negativeEvents.size())
                );

                activeEvents.remove(eventToRemove);
                eventToRemove.onEnd(serverLevel);

                ClientEventPacket.sendClientEventPacket(
                        eventToRemove,
                        ClientEventPacket.EventType.END,
                        serverPlayer.getServer()
                );

                sendEventMessage(serverLevel, eventToRemove);
            }
        }

        return result;
    }

    private static void sendEventMessage(ServerLevel level, AbstractEvent event) {
        level.getServer().getPlayerList().getPlayers().forEach(player -> {
            player.sendSystemMessage(
                    Component.literal("[Disaster Mod] ").append(Component.translatable("item.disastermod.enchanted_honey_bottle.clear",
                                    Component.translatable(event.getName()).withStyle(event.getColor())))
                    .withStyle(style -> style
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    Component.translatable(event.getDescription()))))
            );
            player.playNotifySound(SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.MASTER, 1, 1);
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.disastermod.enchanted_honey_bottle.description")
                .withStyle(ChatFormatting.GRAY));
    }
}
