package com.Da_Technomancer.essentials;

import com.Da_Technomancer.essentials.blocks.ESBlocks;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.items.ESItems;
import com.Da_Technomancer.essentials.tileentities.BrazierTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ESEventHandlerCommon{

	@SuppressWarnings("unused")
	@SubscribeEvent
	public void blockWitchSpawns(LivingSpawnEvent e){
		//Prevents witch spawning if a nearby brazier has soulsand
		if(e.getEntity() instanceof WitchEntity){
			int RANGE_SQUARED = (int) Math.pow(ESConfig.brazierRange.get(), 2);
			for(TileEntity te : e.getWorld().getWorld().tickableTileEntities){
				World w;
				if(te instanceof BrazierTileEntity && te.getDistanceSq(e.getX(), e.getY(), e.getZ()) <= RANGE_SQUARED && (w = te.getWorld()) != null){
					BlockState state = w.getBlockState(te.getPos());
					if(state.getBlock() == ESBlocks.brazier && state.get(ESProperties.BRAZIER_CONTENTS) == 6){
						e.setResult(Event.Result.DENY);
						return;
					}
				}
			}
		}
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public void preventTeleport(EnderTeleportEvent e){
		if(e.getEntity() instanceof EndermanEntity){
			int RANGE_SQUARED = (int) Math.pow(ESConfig.brazierRange.get(), 2);
			for(TileEntity te : e.getEntity().getEntityWorld().tickableTileEntities){
				World w;
				Vec3d entPos = e.getEntity().getPositionVec();
				if(te instanceof BrazierTileEntity && te.getDistanceSq(entPos.x, entPos.y, entPos.z) <= RANGE_SQUARED && (w = te.getWorld()) != null){
					BlockState state = te.getBlockState();
					if(state.getBlock() == ESBlocks.brazier && state.get(ESProperties.BRAZIER_CONTENTS) == 6){
						e.setCanceled(true);
						return;
					}
				}
			}
		}
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public void feedAnimal(PlayerInteractEvent.EntityInteract e){
		if(e.getTarget() instanceof AnimalEntity && e.getItemStack().getItem() == ESItems.animalFeed && (!(e.getTarget() instanceof TameableEntity) || ((TameableEntity) e.getTarget()).isTamed())){
			e.setResult(Event.Result.DENY);
			e.setCanceled(true);
			AnimalEntity an = (AnimalEntity) e.getTarget();
			if(!e.getWorld().isRemote && an.getGrowingAge() == 0 && an.canBreed()){
				an.setInLove(e.getEntityPlayer());
				if(!e.getEntityPlayer().isCreative()){
					e.getItemStack().shrink(1);
				}
			}
		}
	}
}
