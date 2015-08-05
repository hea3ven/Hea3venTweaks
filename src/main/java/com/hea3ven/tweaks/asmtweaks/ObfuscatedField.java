package com.hea3ven.tweaks.asmtweaks;

public class ObfuscatedField extends Obfuscation {

	private String desc;

	public ObfuscatedField(ASMTweaksManager mgr, String name, VersionMapping obfNames,
			String desc) {
		super(mgr, name, obfNames);
		this.desc = desc;
	}

	public String getDesc() {
		return ASMUtils.obfuscateDesc(mgr, desc);
	}
}
