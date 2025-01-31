package fl205.advancedtrommels.tileEntities;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import fl205.advancedtrommels.blockLogics.BlockLogicWaterTrommel;
import net.minecraft.core.WeightedRandomBag;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicChest;
import net.minecraft.core.block.BlockLogicTrommel;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.motion.CarriedBlock;
import net.minecraft.core.crafting.LookupFuelFurnace;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryTrommel;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.monster.MobSlime;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import static fl205.advancedtrommels.AdvancedTrommels.*;

public class TileEntityWaterTrommel extends TileEntity implements Container {
	private ItemStack[] itemStacks = new ItemStack[5];
	public int burnTime = 0;
	public int currentItemBurnTime = 0;
	public int itemPopTime = 0;
	private final int maxBurnTime = 50;
	private int nextToSieve = 1;
	private final Random random = new Random();

	public TileEntityWaterTrommel() {
	}

	public int getContainerSize() {
		return this.itemStacks.length;
	}

	public @Nullable ItemStack getItem(int index) {
		return this.itemStacks[index];
	}

	public @Nullable ItemStack removeItem(int index, int takeAmount) {
		if (this.itemStacks[index] != null) {
			if (this.itemStacks[index].stackSize <= takeAmount) {
				ItemStack itemstack = this.itemStacks[index];
				this.itemStacks[index] = null;
				return itemstack;
			} else {
				ItemStack itemstack1 = this.itemStacks[index].splitStack(takeAmount);
				if (this.itemStacks[index].stackSize <= 0) {
					this.itemStacks[index] = null;
				}

				return itemstack1;
			}
		} else {
			return null;
		}
	}

	public void setItem(int index, @Nullable ItemStack itemstack) {
		this.itemStacks[index] = itemstack;
		if (itemstack != null && itemstack.stackSize > this.getMaxStackSize()) {
			itemstack.stackSize = this.getMaxStackSize();
		}

	}

	public String getNameTranslationKey() {
		return "container.trommel.name";
	}

	public void readFromNBT(CompoundTag nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		ListTag nbttaglist = nbttagcompound.getList("Items");
		this.itemStacks = new ItemStack[this.getContainerSize()];

		for(int i = 0; i < nbttaglist.tagCount(); ++i) {
			CompoundTag nbttagcompound1 = (CompoundTag)nbttaglist.tagAt(i);
			byte byte0 = nbttagcompound1.getByte("Slot");
			if (byte0 >= 0 && byte0 < this.itemStacks.length) {
				this.itemStacks[byte0] = ItemStack.readItemStackFromNbt(nbttagcompound1);
			}
		}

		this.burnTime = nbttagcompound.getShort("BurnTime");
		this.itemPopTime = nbttagcompound.getShort("CookTime");
		this.currentItemBurnTime = this.getItemBurnTime(this.itemStacks[1]);
	}

	public void writeToNBT(CompoundTag nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.putShort("BurnTime", (short)this.burnTime);
		nbttagcompound.putShort("CookTime", (short)this.itemPopTime);
		ListTag nbttaglist = new ListTag();

		for(int i = 0; i < this.itemStacks.length; ++i) {
			if (this.itemStacks[i] != null) {
				CompoundTag nbttagcompound1 = new CompoundTag();
				nbttagcompound1.putByte("Slot", (byte)i);
				this.itemStacks[i].writeToNBT(nbttagcompound1);
				nbttaglist.addTag(nbttagcompound1);
			}
		}

		nbttagcompound.put("Items", nbttaglist);
	}

	public int getMaxStackSize() {
		return 64;
	}

	public float getCookProgressPercent(int i) {
		return (float)this.itemPopTime / (float)this.maxBurnTime * (float)i;
	}

	public int getBurnTimeRemainingScaled(int i) {
		if (this.currentItemBurnTime == 0) {
			this.currentItemBurnTime = this.maxBurnTime;
		}

		return this.burnTime * i / this.currentItemBurnTime;
	}

	public boolean isBurning() {
		return this.burnTime > 0;
	}

	public void tick() {
		if (this.nextToSieve > 4) {
			this.nextToSieve = 1;
		}

		boolean isBurning = this.burnTime > 0;
		boolean flag1 = false;
		if (isBurning) {
			--this.burnTime;
		}

		if (this.worldObj == null || !this.worldObj.isClientSide) {
			if ((this.worldObj == null || this.worldObj.getBlockId(this.x, this.y, this.z) == waterTrommelIdle.id()) && this.currentItemBurnTime == 0 && this.itemStacks[0] == null && this.itemStacks[4] != null && this.itemStacks[4].itemID == Blocks.COBBLE_NETHERRACK.id()) {
				--this.itemStacks[4].stackSize;
				if (this.itemStacks[4].stackSize <= 0) {
					this.itemStacks[4] = null;
				}

				if (this.worldObj != null) {
					BlockLogicWaterTrommel.updateTrommelBlockState(true, this.worldObj, this.x, this.y, this.z);
				} else if (this.carriedBlock != null) {
					this.carriedBlock.blockId = waterTrommelActive.id();
				}

				flag1 = true;
			}

			if (!this.canProduce(this.nextToSieve)) {
				this.nextToSieve = (this.nextToSieve + 1) % 4;
			}

			if (this.burnTime == 0 && this.canProduce(this.nextToSieve)) {
				this.currentItemBurnTime = this.burnTime = this.getItemBurnTime(this.itemStacks[4]);
				if (this.burnTime > 0) {
					flag1 = true;
					if (this.itemStacks[4] != null) {
						if (this.itemStacks[4].getItem() == Items.BUCKET_LAVA) {
							this.itemStacks[4] = new ItemStack(Items.BUCKET);
						} else {
							--this.itemStacks[4].stackSize;
							if (this.itemStacks[4].stackSize <= 0) {
								this.itemStacks[4] = null;
							}
						}
					}
				}
			}

			if (this.isBurning() && this.canProduce(this.nextToSieve)) {
				++this.itemPopTime;
				if (this.itemPopTime >= this.maxBurnTime) {
					this.itemPopTime = 0;
					this.sieveItem(this.nextToSieve);
					this.nextToSieve = (this.nextToSieve + 1) % 4;
					flag1 = true;
				}
			} else {
				this.itemPopTime = 0;
			}

			if (isBurning != this.burnTime > 0) {
				flag1 = true;
				if (this.worldObj != null) {
					BlockLogicWaterTrommel.updateTrommelBlockState(this.burnTime > 0, this.worldObj, this.x, this.y, this.z);
				} else if (this.carriedBlock != null) {
					this.carriedBlock.blockId = this.burnTime > 0 ? waterTrommelIdle.id() : waterTrommelActive.id();
				}
			}
		}

		if (flag1) {
			this.setChanged();
		}

	}

	private boolean canProduce(int slotIndex) {
		return this.itemStacks[slotIndex] != null && this.canItemBeTrommeled(this.itemStacks[slotIndex]);
	}

	public void sieveItem(int slotIndex) {
		if (this.canProduce(slotIndex)) {
			ItemStack itemResult = this.getItemResult(this.itemStacks[slotIndex]);
			--this.itemStacks[slotIndex].stackSize;
			if (this.itemStacks[slotIndex].stackSize <= 0) {
				this.itemStacks[slotIndex] = null;
			}

			if (itemResult != null) {
				int xOffset = 0;
				int zOffset = 0;
				if (this.worldObj != null) {
					int meta = this.worldObj.getBlockMetadata(this.x, this.y, this.z) & 7;
					if (meta == 2) {
						xOffset = -1;
					} else if (meta == 5) {
						zOffset = -1;
					} else if (meta == 3) {
						xOffset = 1;
					} else if (meta == 4) {
						zOffset = 1;
					}
				}

				int adjacentId = this.worldObj != null ? this.worldObj.getBlockId(this.x + xOffset, this.y, this.z + zOffset) : 0;
				Container chest = null;
				if (Block.hasLogicClass(Blocks.blocksList[adjacentId], BlockLogicChest.class)) {
					assert this.worldObj != null;

					chest = BlockLogicChest.getInventory(this.worldObj, this.x + xOffset, this.y, this.z + zOffset);
				}

				if (chest != null) {
					for(int i = 0; i < chest.getContainerSize(); ++i) {
						ItemStack slot = chest.getItem(i);
						if (slot != null && slot.itemID == itemResult.itemID && slot.getMetadata() == itemResult.getMetadata()) {
							while(slot.stackSize + 1 <= slot.getMaxStackSize()) {
								++slot.stackSize;
								chest.setItem(i, slot);
								if (itemResult.stackSize <= 0) {
									return;
								}

								--itemResult.stackSize;
							}
						}
					}

					if (itemResult.stackSize <= 0) {
						return;
					}

					for(int i = 0; i < chest.getContainerSize(); ++i) {
						ItemStack slot = chest.getItem(i);
						if (slot == null) {
							chest.setItem(i, itemResult);
							return;
						}
					}
				}

				if (itemResult.stackSize > 0) {
					if (this.worldObj != null) {
						this.worldObj.dropItem(this.x, this.y, this.z, itemResult);
					} else if (this.carriedBlock != null) {
						this.carriedBlock.world.dropItem(MathHelper.floor(this.carriedBlock.holder.x), MathHelper.floor(this.carriedBlock.holder.y), MathHelper.floor(this.carriedBlock.holder.z), itemResult);
					}
				}
			}

			if (this.random.nextInt(4000) == 0) {
				float f = 0.125F;
				float f1 = 0.125F;
				MobSlime mobSlime = new MobSlime(this.carriedBlock != null ? this.carriedBlock.world : this.worldObj);
				mobSlime.setSlimeSize(1);
				float f3 = 0.05F;
				mobSlime.xd = (float)this.random.nextGaussian() * f3;
				mobSlime.yd = (float)this.random.nextGaussian() * f3 + 0.2F;
				mobSlime.zd = (float)this.random.nextGaussian() * f3;
				if (this.worldObj != null) {
					mobSlime.moveTo((double)this.x + (double)f, (double)this.y + (double)1.0F, (double)this.z + (double)f1, this.random.nextFloat() * 360.0F, 0.0F);
					this.worldObj.entityJoinedWorld(mobSlime);
				} else if (this.carriedBlock != null) {
					mobSlime.moveTo(this.carriedBlock.holder.x + (double)f, this.carriedBlock.holder.y + (double)this.carriedBlock.holder.heightOffset, this.carriedBlock.holder.z + (double)f1, this.random.nextFloat() * 360.0F, 0.0F);
					this.carriedBlock.world.entityJoinedWorld(mobSlime);
				}
			}

		}
	}

	private ItemStack getItemResult(ItemStack slotItem) {
		if (this.random.nextInt(2) != 0) {
			return null;
		} else {
			for(RecipeEntryTrommel recipe : Registries.RECIPES.getAllTrommelRecipes()) {
				if (recipe.getInput().matches(slotItem)) {
					return ((WeightedRandomLootObject)((WeightedRandomBag)recipe.getOutput()).getRandom()).getItemStack();
				}
			}

			return null;
		}
	}

	private boolean canItemBeTrommeled(ItemStack itemstack) {
		if (itemstack == null) {
			return false;
		} else {
			for(RecipeEntryTrommel recipe : Registries.RECIPES.getAllTrommelRecipes()) {
				if (recipe.getInput().matches(itemstack)) {
					return true;
				}
			}

			return false;
		}
	}

	private int getItemBurnTime(ItemStack itemStack) {
		return itemStack == null ? 0 : LookupFuelFurnace.instance.getFuelYield(itemStack.getItem().id);
	}

	public boolean stillValid(Player entityplayer) {
		if (this.worldObj != null && this.worldObj.getTileEntity(this.x, this.y, this.z) == this) {
			return entityplayer.distanceToSqr((double)this.x + (double)0.5F, (double)this.y + (double)0.5F, (double)this.z + (double)0.5F) <= (double)64.0F;
		} else {
			return false;
		}
	}

	public void sortContainer() {
	}

	public void dropContents(World world, int x, int y, int z) {
		super.dropContents(world, x, y, z);
		if (!BlockLogicWaterTrommel.keepTrommelInventory && world.getTileEntity(x, y, z) != null) {
			for(int l = 0; l < this.getContainerSize(); ++l) {
				ItemStack itemstack = this.getItem(l);
				if (itemstack != null) {
					float f = this.random.nextFloat() * 0.8F + 0.1F;
					float f1 = this.random.nextFloat() * 0.8F + 0.1F;
					float f2 = this.random.nextFloat() * 0.8F + 0.1F;

					while(itemstack.stackSize > 0) {
						int i1 = this.random.nextInt(21) + 10;
						if (i1 > itemstack.stackSize) {
							i1 = itemstack.stackSize;
						}

						itemstack.stackSize -= i1;
						EntityItem item = new EntityItem(world, (float)x + f, (float)y + f1, (float)z + f2, new ItemStack(itemstack.itemID, i1, itemstack.getMetadata()));
						float f3 = 0.05F;
						item.xd = (float)this.random.nextGaussian() * f3;
						item.yd = (float)this.random.nextGaussian() * f3 + 0.2F;
						item.zd = (float)this.random.nextGaussian() * f3;
						world.entityJoinedWorld(item);
					}
				}
			}
		}

	}

	public void heldTick(World world, Entity holder) {
		this.tick();
	}

	public boolean canBeCarried(World world, Entity potentialHolder) {
		return true;
	}

	public CarriedBlock getCarriedEntry(World world, Entity holder, Block<?> currentBlock, int currentMeta) {
		return super.getCarriedEntry(world, holder, currentBlock, currentMeta & -8 | 2);
	}
}
