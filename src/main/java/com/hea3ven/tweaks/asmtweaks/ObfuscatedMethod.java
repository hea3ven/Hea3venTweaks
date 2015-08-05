package com.hea3ven.tweaks.asmtweaks;

public class ObfuscatedMethod extends Obfuscation {

	private VersionMapping descs;

	public ObfuscatedMethod(ASMTweaksManager mgr, String name, VersionMapping obfNames,
			VersionMapping descs) {
		super(mgr, name, obfNames);
		this.descs = descs;
	}

	@Override
	public String getIdentifier() {
		String name = super.getIdentifier();
		return (!name.contains(".")) ? name : name.substring(name.lastIndexOf('.') + 1);
	}

	public String getDesc() {
		return ASMUtils.obfuscateDesc(mgr, descs.get(mgr.getCurrentVersion()));
	}

	public boolean matches(String name, String desc) {
		return name.equals(getIdentifier()) && desc.equals(this.getDesc());

	}
}
