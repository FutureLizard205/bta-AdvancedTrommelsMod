package fl205.advancedtrommels.blockLogics;

import fl205.advancedtrommels.mixinInterfaces.PlayerMixinInterface;
import fl205.advancedtrommels.tileEntities.TileEntityWaterTrommel;
import net.minecraft.core.Global;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicRotatable;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

import java.util.Random;

import static fl205.advancedtrommels.AdvancedTrommels.*;

public class BlockLogicWaterTrommel extends BlockLogicRotatable {
	private boolean isActive;
	public static boolean keepTrommelInventory = false;

	public BlockLogicWaterTrommel(Block<?> block, boolean isActive) {
		super(block, Material.stone);
		this.isActive = isActive;
		block.withEntity(TileEntityWaterTrommel::new);
	}

	public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
		switch (dropCause) {
			case PICK_BLOCK:
			case EXPLOSION:
			case PROPER_TOOL:
			case SILK_TOUCH:
				return new ItemStack[]{new ItemStack(waterTrommelIdle)};
			default:
				return null;
		}
	}

	public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
		if (!world.isClientSide) {
			TileEntityWaterTrommel tileEntityWaterTrommel = (TileEntityWaterTrommel) world.getTileEntity(x, y, z);
			((PlayerMixinInterface) player).bta_AdvancedTrommelsMod$displayWaterTrommelScreen(tileEntityWaterTrommel);
		}
		return true;
	}

	public void animationTick(World world, int x, int y, int z, Random rand) {
		if (this.isActive) {
			double xPos = (double)x + (double)rand.nextFloat();
			double yPos = (double)y + (double)rand.nextFloat() * (double)0.5F + (double)1.0F;
			double zPos = (double)z + (double)rand.nextFloat();
			world.spawnParticle("smoke", xPos, yPos, zPos, 0.0F, 0.0F, 0.0F, 0);
		}
	}

	public static void updateTrommelBlockState(boolean lit, World world, int x, int y, int z) {
		int l = world.getBlockMetadata(x, y, z);
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity == null) {
			String msg = "Water Trommel is missing Tile Entity at x: " + x + " y: " + y + " z: " + z + ", block will be removed!";
			if (Global.BUILD_CHANNEL.isUnstableBuild()) {
				throw new RuntimeException(msg);
			} else {
				world.setBlockWithNotify(x, y, z, 0);
				LOGGER.warn(msg);
			}
		} else {
			keepTrommelInventory = true;
			if (lit) {
				world.setBlockWithNotify(x, y, z, waterTrommelActive.id());
			} else {
				world.setBlockWithNotify(x, y, z, waterTrommelIdle.id());
			}

			keepTrommelInventory = false;
			world.setBlockMetadataWithNotify(x, y, z, l);
			tileEntity.validate();
			world.setTileEntity(x, y, z, tileEntity);
		}
	}
}
