package com.Da_Technomancer.essentials.blocks.redstone;

import com.Da_Technomancer.essentials.blocks.ESBlocks;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.tileentities.ILinkTE;
import com.Da_Technomancer.essentials.tileentities.RedstoneTransmitterTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class RedstoneTransmitter extends ContainerBlock implements IWireConnect{

	public RedstoneTransmitter(){
		super(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.5F).sound(SoundType.STONE));
		String name = "redstone_transmitter";
		setRegistryName(name);
		ESBlocks.toRegister.add(this);
		ESBlocks.blockAddQue(this);
		setDefaultState(getDefaultState().with(ESProperties.COLOR, DyeColor.WHITE));
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		worldIn.getPendingBlockTicks().scheduleTick(pos, this, RedstoneUtil.DELAY, TickPriority.HIGH);

		if(blockIn != Blocks.REDSTONE_WIRE && !(blockIn instanceof RedstoneDiodeBlock)){
			//Simple optimization- if the source of the block update is just a redstone signal changing, we don't need to force a full connection rebuild
			TileEntity te = worldIn.getTileEntity(pos);
			if(te instanceof RedstoneTransmitterTileEntity){
				((RedstoneTransmitterTileEntity) te).buildConnections();
			}
		}
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
	}

	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side){
		return true;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		neighborChanged(state, worldIn, pos, this, pos, false);
	}

	@Override
	public void tick(BlockState state, World worldIn, BlockPos pos, Random rand){
		TileEntity rawTE = worldIn.getTileEntity(pos);
		if(rawTE instanceof RedstoneTransmitterTileEntity){
			((RedstoneTransmitterTileEntity) rawTE).refreshOutput();
		}
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		//Handle linking and dyeing
		ItemStack heldItem = playerIn.getHeldItem(hand);
		TileEntity te = worldIn.getTileEntity(pos);
		Item item;
		if(ILinkTE.isLinkTool(heldItem) && te instanceof RedstoneTransmitterTileEntity){
			if(!worldIn.isRemote){
				((RedstoneTransmitterTileEntity) te).wrench(heldItem, playerIn);
			}
			return true;
		}else if((item = heldItem.getItem()) instanceof DyeItem && te instanceof RedstoneTransmitterTileEntity){
			if(!worldIn.isRemote){
				((RedstoneTransmitterTileEntity) te).dye(((DyeItem) item).getDyeColor());
			}
			return true;
		}
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.essentials.reds_trans.desc"));
		tooltip.add(new TranslationTextComponent("tt.essentials.reds_trans.linking"));
		tooltip.add(new TranslationTextComponent("tt.essentials.reds_trans.dyes"));
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new RedstoneTransmitterTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> container){
		container.add(ESProperties.COLOR);
	}

	@Override
	public boolean canConnect(Direction side, BlockState state){
		return true;
	}
}
