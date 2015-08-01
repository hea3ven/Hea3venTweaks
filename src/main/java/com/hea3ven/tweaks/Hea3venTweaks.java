package com.hea3ven.tweaks;

import java.io.File;
import java.util.List;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import com.hea3ven.tweaks.asmtweaks.ASMTweaksManager;
import com.hea3ven.tweaks.asmtweaks.ASMTweaksManagerBuilder;

public class Hea3venTweaks implements ITweaker, IClassTransformer {

	private static ASMTweaksManager asmTweaksManager = new ASMTweaksManagerBuilder("15w31c")
			.startClass("net.minecraft.entity.Entity")
			.withMapping("15w31[abc]", "pr")
			.startMethod("onUpdate")
			.withMapping("15w31[abc]", "t_")
			.withDesc(".*", "()V")
			.endMethod()
			.startMethod("moveEntity")
			.withMapping("15w31[abc]", "d")
			.withDesc(".*", "(DDD)V")
			.endMethod()
			.startField("isCollidedHorizontally", "Z")
			.withMapping("15w31[abc]", "D")
			.endField()
			.endClass()
			.startClass("net.minecraft.entity.item.EntityBoat")
			.withMapping("15w31[abc]", "vk")
			.endClass()
			.startClass("net.minecraft.world.World")
			.withMapping("1.7.10", "ahb")
			.withMapping("15w31[ab]", "aen")
			.withMapping("15w31c", "aeo")
			.endClass()
			.startClass("net.minecraft.util.BlockPos")
			.withMapping("15w31[abc]", "cj")
			.endClass()
			.startClass("net.minecraft.util.AxisAlignedBB")
			.withMapping("1.7.10", "azt")
			.withMapping("15w31[ab]", "awf")
			.withMapping("15w31c", "awg")
			.endClass()
			.startClass("net.minecraft.block.state.IBlockState")
			.withMapping("15w31[ab]", "anl")
			.withMapping("15w31c", "anm")
			.endClass()
			.startClass("net.minecraft.entity.EntityLivingBase")
			.withMapping("1.7.10", "sv")
			.withMapping("15w31[abc]", "qa")
			.endClass()
			.startClass("net.minecraft.block.Block")
			.withMapping("1.7.10", "aji")
			.withMapping("15w31[ab]", "agj")
			.withMapping("15w31c", "agk")
			.startMethod("addCollisionBoxesToList")
			.withMapping("1.7.10", "a")
			.withMapping("15w31[abc]", "a")
			.withDesc("1.7.10", "(Lnet.minecraft.world.World;IIILnet.minecraft.util.AxisAlignedBB;"
					+ "Ljava/util/List;Lnet.minecraft.entity.Entity;)V")
			.withDesc("15w31[abc]",
					"(Lnet.minecraft.world.World;Lnet.minecraft.util.BlockPos;"
							+ "Lnet.minecraft.block.state.IBlockState;"
							+ "Lnet.minecraft.util.AxisAlignedBB;"
							+ "Ljava/util/List;Lnet.minecraft.entity.Entity;)V")
			.endMethod()
			.endClass()
			.startClass("net.minecraft.block.BlockLeavesBase")
			.withMapping("15w31[ab]", "alq")
			.withMapping("15w31c", "alr")
			.endClass()
			.addTweak("net.minecraft.entity.item.EntityBoat", "net.minecraft.entity.Entity.onUpdate", new PreventBoatBreak())
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
