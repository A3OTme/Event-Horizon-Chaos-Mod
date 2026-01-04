package com.a3ot.eventhorizon.api.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class PlayerChangeGameModeEvent extends PlayerEvent {

    public PlayerChangeGameModeEvent(Player player) {
        super(player);
    }

    public static class Post extends PlayerEvent {
        private final GameType oldGameMode;
        private final GameType newGameMode;

        public Post(ServerPlayer player, GameType oldGameMode, GameType newGameMode) {
            super(player);
            this.oldGameMode = oldGameMode;
            this.newGameMode = newGameMode;
        }

        public GameType getOldGameMode() {
            return oldGameMode;
        }

        public GameType getNewGameMode() {
            return newGameMode;
        }
    }
}
