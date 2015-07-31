package com.hea3ven.tweaks;

import java.io.File;
import java.util.List;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import com.hea3ven.tweaks.asmtweaks.ASMTweaksManager;
import com.hea3ven.tweaks.asmtweaks.ASMTweaksManagerBuilder;

public class Hea3venTweaks implements ITweaker, IClassTransformer {

	private static ASMTweaksManager asmTweaksManager = new ASMTweaksManagerBuilder("15w31a")
			.addClass("net.minecraft.entity.item.EntityBoat", new String[] {"15w31a", "vk"})
			.addMethod("net.minecraft.entity.item.EntityBoat", "onUpdate", "()V",
					new String[] {"15w31a", "t_"})
			.addClass("net.minecraft.entity.Entity", new String[] {"15w31a", "pr"})
			.addMethod("net.minecraft.entity.item.EntityBoat", "moveEntity", "(DDD)V",
					new String[] {"15w31a", "d"})
			.addField("net.minecraft.entity.item.EntityBoat", "isCollidedHorizontally", "Z",
					new String[] {"15w31a", "D"})
			.addClass("net.minecraft.world.World", new String[] {"1.7.10", "ahb", "15w31a", "aen"})
			.addClass("net.minecraft.util.BlockPos", new String[] {"15w31a", "cj"})
			.addClass("net.minecraft.util.AxisAlignedBB",
					new String[] {"1.7.10", "azt", "15w31a", "awf"})
			.addClass("net.minecraft.block.state.IBlockState", new String[] {"15w31a", "anl"})
			.addClass("net.minecraft.entity.Entity", new String[] {"1.7.10", "sa", "15w31a", "pr"})
			.addClass("net.minecraft.entity.EntityLivingBase",
					new String[] {"1.7.10", "sv", "15w31a", "qa"})
			.addClass("net.minecraft.block.Block", new String[] {"1.7.10", "aji", "15w31a", "agj"})
			.addMethod("net.minecraft.block.Block", "addCollisionBoxesToList", null,
					new String[] {"1.7.10", "a", "15w31a", "a"})
			.addClass("net.minecraft.block.BlockLeavesBase", new String[] {"15w31a", "alq"})
			.addTweak("net.minecraft.entity.item.EntityBoat", "onUpdate", new PreventBoatBreak())
			.addTweak("net.minecraft.block.BlockLeavesBase", new NonSolidLeaves())
			.build();

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (name.startsWith("com.hea3ven.tweaks"))
			return basicClass;

		return asmTweaksManager.handle(name, transformedName, basicClass);
	}

	@Override
	public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
	}

	@Override
	public void injectIntoClassLoader(LaunchClassLoader classLoader) {
		classLoader.registerTransformer(Hea3venTweaks.class.getName());
	}

	@Override
	public String getLaunchTarget() {
		return null;
	}

	@Override
	public String[] getLaunchArguments() {
		return new String[0];
	}
}
