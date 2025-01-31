package fl205.advancedtrommels.gui.screens;

import fl205.advancedtrommels.gui.menus.MenuWaterTrommel;
import fl205.advancedtrommels.tileEntities.TileEntityWaterTrommel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class ScreenWaterTrommel extends ScreenContainerAbstract {
	private TileEntityWaterTrommel trommelInventory;

	public ScreenWaterTrommel(ContainerInventory inventoryplayer, TileEntityWaterTrommel tileentitywatertrommel) {
		super(new MenuWaterTrommel(inventoryplayer, tileentitywatertrommel));
		this.ySize = 192;
		this.trommelInventory = tileentitywatertrommel;
	}

	protected void drawGuiContainerForegroundLayer() {
		this.font.drawString(I18n.getInstance().translateKey("gui.trommel.label.trommel"), 60, 6, 4210752);
		this.font.drawString(I18n.getInstance().translateKey("gui.trommel.label.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	protected void drawGuiContainerBackgroundLayer(float f) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.textureManager.loadTexture("/assets/advancedtrommels/textures/gui/container/waterTrommel.png").bind();
		int j = (this.width - this.xSize) / 2;
		int k = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
		if (this.trommelInventory.isBurning()) {
			int l = this.trommelInventory.getBurnTimeRemainingScaled(12);
			this.drawTexturedModalRect(j + 33, k + 33 + 12 - l, 176, 12 - l, 14, l + 2);
		}

		int i1 = (int)this.trommelInventory.getCookProgressPercent(8) % 4;
		GL11.glPushMatrix();
		GL11.glTranslatef((float)(j + 105), (float)(k + 50), 0.0F);
		this.drawTexturedModalRect(0, 0, 176 + i1 * 16, 16, 16, 16);
		GL11.glPopMatrix();
	}
}
