package com.hea3ven.tweaks.asmtweaks;

import java.util.Set;

import com.hea3ven.tweaks.asmtweaks.ASMTweaksConfig.ASMTweakConfig;

public interface ASMTweak {

	void configure(ASMTweakConfig conf);

	String getName();
	
	Set<ASMMod> getModifications();


}
