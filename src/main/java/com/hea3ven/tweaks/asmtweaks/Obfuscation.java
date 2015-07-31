package com.hea3ven.tweaks.asmtweaks;

import java.util.HashMap;

class Obfuscation {

	private ASMTweaksManager mgr;
	private String name;
	private HashMap<String, String> obfNames;

	public Obfuscation(ASMTweaksManager mgr, String name, HashMap<String, String> obfNames) {
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
		return name.equals(getName()) || name.equals(getObfName());
	}

	public String getIdentifier() {
		return mgr.isObfuscated() ? obfNames.get(mgr.getCurrentVersion()) : name;
	}
}
