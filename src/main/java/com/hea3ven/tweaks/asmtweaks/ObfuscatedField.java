package com.hea3ven.tweaks.asmtweaks;

import java.util.HashMap;

public class ObfuscatedField extends Obfuscation {

	private ObfuscatedClass owner;

	public ObfuscatedField(ASMTweaksManager mgr, ObfuscatedClass owner, String name,
			HashMap<String, String> obfNames) {
		super(mgr, name, obfNames);
		this.owner = owner;
	}

	public ObfuscatedClass getOwner() {
		return owner;
	}
}
