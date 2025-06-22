package net.motivationinnovation;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.api.EnvType;
import net.motivationinnovation.network.VillagerSyncPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MotivationInnovation {
    public static final String MOD_ID = "motivationinnovation";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registries.ITEM);
    public static final RegistrySupplier<Item> WHIP = ITEMS.register(
            "whip",
            () -> new WhipItem(new Item.Properties()
                    .rarity(Rarity.RARE)
                    .stacksTo(1)
                    .arch$tab(CreativeModeTabs.TOOLS_AND_UTILITIES)));

    public static void init() {
        ITEMS.register();
        InteractionEvent.INTERACT_ENTITY.register((WhipItem::handleWhipInteraction));

        if (Platform.getEnv() == EnvType.SERVER) {
            NetworkManager.registerS2CPayloadType(VillagerSyncPacket.TYPE, VillagerSyncPacket.STREAM_CODEC);
        }
    }
}
