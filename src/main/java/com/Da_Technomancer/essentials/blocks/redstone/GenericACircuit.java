package com.Da_Technomancer.essentials.blocks.redstone;

import com.Da_Technomancer.essentials.Essentials;
import com.Da_Technomancer.essentials.tileentities.CircuitTileEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class GenericACircuit extends AbstractCircuit{

	private final String ttName;
	private final Function<Float, Float> function;

	public GenericACircuit(String name, Function<Float, Float> function){
		super(name +"_circuit");
		this.ttName = "tt." + Essentials.MODID + "." + name + "_circuit";
		this.function = function;
	}

	@Override
	public boolean useInput(CircuitTileEntity.Orient or){
		return or == CircuitTileEntity.Orient.BACK;
	}

	@Override
	public float getOutput(float in0, float in1, float in2, CircuitTileEntity te){
		return function.apply(in1);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent(ttName));
	}
}
