package com.hea3ven.tweaks;

import java.io.File;
import java.util.List;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import com.hea3ven.tools.bootstrap.Bootstrap;

public class Hea3venTweaks implements ITweaker, IClassTransformer {

	private static IClassTransformer asmTweaksManager = null;

	static {
		Bootstrap.initLib("mappings", "1.0.0");
		Bootstrap.initLib("asmtweaks", "1.0.0");

		asmTweaksManager = com.hea3ven.tweaks.TweaksBuilder.build();
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		return asmTweaksManager.transform(name, transformedName, basicClass);
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

	public static void setConfig(String name, String value) {
		((com.hea3ven.tools.asmtweaks.ASMTweaksManager) asmTweaksManager)
				.getConfig()
				.setConfig(name, value);
	}
}
