package com.hea3ven.tweaks.asmtweaks;

import java.security.InvalidParameterException;
import java.util.HashMap;

public class ASMTweaksManagerBuilder {

	private ASMTweaksManager mgr;

	public ASMTweaksManagerBuilder(String currentVersion) {
		this.mgr = new ASMTweaksManager(currentVersion);
	}

	public ASMTweaksManagerBuilder addClass(String name, String[] obfNamesDesc) {
		mgr.add(new ObfuscatedClass(mgr, name, convertObfNamesDesc(obfNamesDesc)));
		return this;
	}

	public ASMTweaksManagerBuilder addMethod(String className, String name, String desc,
			String[] obfNamesDesc) {
		mgr.add(new ObfuscatedMethod(mgr, mgr.getClass(className), name, desc,
				convertObfNamesDesc(obfNamesDesc)));
		return this;
	}

	public ASMTweaksManagerBuilder addTweak(String className, String methodName, ASMTweak tweak) {
		ObfuscatedClass cls = mgr.getClass(className);
		mgr.addTweak(cls, mgr.getMethod(cls, methodName), tweak);
		return this;
	}

	public ASMTweaksManager build() {
		return mgr;
	}

	private HashMap<String, String> convertObfNamesDesc(String[] obfNamesDesc) {
		if (obfNamesDesc.length % 2 != 0)
			throw new InvalidParameterException("obfNamesDesc is not an even amount of strings");
		HashMap<String, String> obfNames = new HashMap<String, String>();
		for (int i = 0; i < obfNamesDesc.length / 2; i++) {
			obfNames.put(obfNamesDesc[i * 2], obfNamesDesc[i * 2 + 1]);
		}
		return obfNames;
	}
}
