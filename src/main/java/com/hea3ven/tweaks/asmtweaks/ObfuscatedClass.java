package com.hea3ven.tweaks.asmtweaks;

public class ObfuscatedClass extends Obfuscation {
	public ObfuscatedClass(ASMTweaksManager mgr, String name, VersionMapping obfNames) {
		super(mgr, name, obfNames);
	}

	public String getPath() {
		return getIdentifier().replace('.', '/');
	}
}