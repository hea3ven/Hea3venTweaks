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
			.addTweak("net.minecraft.entity.item.EntityBoat", "onUpdate", new PreventBoatBreak())
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
