package net.motivationinnovation.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.motivationinnovation.MotivationInnovationClient;

public class MotivationInnovationClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MotivationInnovationClient.init();

        WorldRenderEvents.BEFORE_ENTITIES.register((context) -> {
            MotivationInnovationClient.renderVillager();
        });
    }
}
