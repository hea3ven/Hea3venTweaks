package com.hea3ven.tweaks.asmtweaks;

class Obfuscation {

	protected ASMTweaksManager mgr;

	private String name;
	private VersionMapping obfNames;

	public Obfuscation(ASMTweaksManager mgr, String name, VersionMapping obfNames) {
		this.mgr = mgr;
		this.name = name;
		this.obfNames = obfNames;
	}

	public String getName() {
		return name;
	}

	public String getObfName() {
		return obfNames.get(mgr.getCurrentVersion());
	}

	public boolean matchesName(String name) {
		name = name.replace('/', '.');
		return name.equals(getName()) || name.equals(getObfName());
	}

	public String getIdentifier() {
		return mgr.isObfuscated() ? obfNames.get(mgr.getCurrentVersion()) : name;
	}
}
