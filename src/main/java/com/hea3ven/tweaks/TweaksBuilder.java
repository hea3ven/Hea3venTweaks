package com.hea3ven.tweaks;

import net.minecraft.launchwrapper.IClassTransformer;

import com.hea3ven.tools.asmtweaks.ASMTweaksManagerBuilder;

public class TweaksBuilder {

	public static IClassTransformer build() {
		return new ASMTweaksManagerBuilder()
				.loadMappings("/mappings")
				.addTweak(new RuntimeObfuscation(
						new String[] {"com/hea3ven/tweaks/DayNightCycleFunctions"}))
				.addTweak(new PreventBoatBreak())
				.addTweak(new NonSolidLeaves())
				.addTweak(new DayNightCycle())
				.build();
	}

}
