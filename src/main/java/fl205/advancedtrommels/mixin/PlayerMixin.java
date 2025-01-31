package fl205.advancedtrommels.mixin;

import fl205.advancedtrommels.mixinInterfaces.PlayerMixinInterface;
import fl205.advancedtrommels.tileEntities.TileEntityWaterTrommel;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerMixin implements PlayerMixinInterface {

	@Unique
	@Override
	public void bta_AdvancedTrommelsMod$displayWaterTrommelScreen(TileEntityWaterTrommel tileEntity) {
	}
}
