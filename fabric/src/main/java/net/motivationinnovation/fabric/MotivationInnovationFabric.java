package net.motivationinnovation.fabric;

import net.fabricmc.api.ModInitializer;
import net.motivationinnovation.MotivationInnovation;

public class MotivationInnovationFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MotivationInnovation.init();
    }
}
