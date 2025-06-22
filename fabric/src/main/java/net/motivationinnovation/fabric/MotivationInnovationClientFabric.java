package net.motivationinnovation.fabric;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.motivationinnovation.MotivationInnovationClient;
import net.motivationinnovation.WhipItem;

public class MotivationInnovationClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MotivationInnovationClient.init();

        WorldRenderEvents.BEFORE_ENTITIES.register((context) -> {
            Player player = Minecraft.getInstance().player;
            if (player == null) {
                return;
            }

            ItemStack stackMain = player.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack stackOff = player.getItemInHand(InteractionHand.MAIN_HAND);

            // Prioritize the whip in the main hand for rendering
            if (stackMain.getItem() instanceof WhipItem whip) {
                MotivationInnovationClient.internalRenderVillager(whip, whip.getTargetedVillager());
            } else if (stackOff.getItem() instanceof WhipItem whip) {
                MotivationInnovationClient.internalRenderVillager(whip, whip.getTargetedVillager());
            }

            MotivationInnovationClient.renderVillagerPOIs();
        });
    }
}
