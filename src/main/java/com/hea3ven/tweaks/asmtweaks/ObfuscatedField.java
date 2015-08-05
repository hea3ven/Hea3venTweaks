package com.hea3ven.tweaks.asmtweaks;

public class ObfuscatedField extends Obfuscation {

	private String desc;

	public ObfuscatedField(ASMTweaksManager mgr, String name, VersionMapping obfNames,
			String desc) {
		super(mgr, name, obfNames);
		this.desc = desc;
	}

	@Override
	public String getIdentifier() {
		String name = super.getIdentifier();
		return (!name.contains(".")) ? name : name.substring(name.lastIndexOf('.') + 1);
	}

	public String getDesc() {
		return ASMUtils.obfuscateDesc(mgr, desc);
	}
}
