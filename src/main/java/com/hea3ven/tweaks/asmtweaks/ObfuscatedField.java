package com.hea3ven.tweaks.asmtweaks;

import java.util.HashMap;

public class ObfuscatedField extends Obfuscation {

	private ObfuscatedClass owner;
	private String desc;

	public ObfuscatedField(ASMTweaksManager mgr, String name,
			VersionMapping obfNames, String desc) {
		super(mgr, name, obfNames);
		this.owner = owner;
		this.desc = desc;
	}

	public ObfuscatedClass getOwner() {
		return owner;
	}

	public String getDesc() {
		return desc;
	}
}
