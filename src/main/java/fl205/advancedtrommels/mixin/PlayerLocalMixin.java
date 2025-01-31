package fl205.advancedtrommels.mixin;

import fl205.advancedtrommels.gui.screens.ScreenWaterTrommel;
import fl205.advancedtrommels.tileEntities.TileEntityWaterTrommel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerLocal.class)
public abstract class PlayerLocalMixin {

	@Shadow
	protected Minecraft mc;

	@Unique
	public void bta_AdvancedTrommelsMod$displayWaterTrommelScreen(TileEntityWaterTrommel tileEntity) {
		this.mc.displayScreen(new ScreenWaterTrommel(((PlayerLocal) (Object) this).inventory, tileEntity));
	}
}
