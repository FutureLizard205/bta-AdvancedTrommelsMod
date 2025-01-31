package fl205.advancedtrommels;

import net.minecraft.client.render.block.model.BlockModelIce;
import turniplabs.halplibe.helper.ModelHelper;
import turniplabs.halplibe.util.ModelEntrypoint;

import static fl205.advancedtrommels.AdvancedTrommels.waterTrommelIdle;

public class AdvancedTrommelsBlockModels implements ModelEntrypoint {
	@Override
	public void initBlockModels() {
		ModelHelper.setBlockModel(waterTrommelIdle, () -> new BlockModelIce<>(waterTrommelIdle));
	}

	@Override
	public void initItemModels() {

	}

	@Override
	public void initEntityModels() {

	}

	@Override
	public void initTileEntityModels() {

	}

	@Override
	public void initBlockColors() {

	}
}
