package fl205.advancedtrommels;

import fl205.advancedtrommels.blockLogics.BlockLogicWaterTrommel;
import fl205.advancedtrommels.tileEntities.TileEntityWaterTrommel;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.util.collection.NamespaceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.EntityHelper;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

public class AdvancedTrommels implements ModInitializer, GameStartEntrypoint {
    public static final String MOD_ID = "advancedtrommels";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Config TOML file manager

	public static final TomlConfigHandler config;
	static {
		// Config
		Toml toml = new Toml("Advanced Trommels Mod Config\nMore info at https://github.com/FutureLizard205/bta-advancedtrommelsMod");

		toml.addCategory("IDs")
			.addEntry("waterTrommelIdleID", 1234);

		config = new TomlConfigHandler(MOD_ID, toml);
	}


	// Blocks

	public static Block<?> waterTrommelIdle;
	public static Block<?> waterTrommelActive;

	@Override
    public void onInitialize() {
		waterTrommelIdle = new BlockBuilder(MOD_ID)
			.build("water.trommel.idle", "water_trommel_idle", config.getInt("IDs.waterTrommelIdleID"), b -> new BlockLogicWaterTrommel(waterTrommelIdle, false));
		waterTrommelActive = new BlockBuilder(MOD_ID)
			.setTags(BlockTags.NOT_IN_CREATIVE_MENU)
			.build("water.trommel.active", "water_trommel_active", waterTrommelIdle.id() + 1, b -> new BlockLogicWaterTrommel(waterTrommelActive, true));

		LOGGER.info("AdvancedTrommels mod initialized.");
    }

	@Override
	public void beforeGameStart() {
		// Tile Entities
		EntityHelper.createTileEntity(TileEntityWaterTrommel.class,  new NamespaceID(MOD_ID, "water_trommel"));
	}

	@Override
	public void afterGameStart() {

	}
}
