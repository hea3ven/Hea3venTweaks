package com.hea3ven.tweaks.asmtweaks;

import java.util.HashMap;

public class ObfuscatedMethod extends Obfuscation {

	private ObfuscatedClass owner;
	private String desc;

	public ObfuscatedMethod(ASMTweaksManager mgr, ObfuscatedClass owner, String name, String desc,
			HashMap<String, String> obfNames) {
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

	public boolean matches(String owner, String name, String desc2) {
		return name.equals(getIdentifier()) && owner.equals(this.owner.getIdentifier())
				&& desc.equals(this.desc);

	}
}
