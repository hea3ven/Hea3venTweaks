package com.hea3ven.tweaks.asmtweaks;

import java.util.HashMap;

public class ObfuscatedClass extends Obfuscation {
	public ObfuscatedClass(ASMTweaksManager mgr, String name, HashMap<String, String> obfNames) {
		super(mgr, name, obfNames);
	}

	public String getPath() {
		return getIdentifier().replace('.', '/');
	}
}