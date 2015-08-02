package com.hea3ven.tweaks.asmtweaks;

public class ASMTweaksManagerBuilder {

	private ASMTweaksManager mgr;
	private String className;
	VersionMapping classObfNames;
	private String methodName;
	VersionMapping methodObfNames;
	VersionMapping methodObfDescs;

	VersionMapping obfNames;
	private String fieldName;
	private String fieldDesc;
	private VersionMapping fieldObfNames;

	public ASMTweaksManagerBuilder(String currentVersion) {
		this.mgr = new ASMTweaksManager(currentVersion);
	}

	public ASMTweaksManagerBuilder startClass(String name) {
		className = name;
		classObfNames = new VersionMapping();

		obfNames = classObfNames;
		return this;
	}

	public ASMTweaksManagerBuilder endClass() {
		mgr.add(new ObfuscatedClass(mgr, className, classObfNames));
		return this;
	}

	public ASMTweaksManagerBuilder startMethod(String name) {
		methodName = className + "." + name;
		methodObfNames = new VersionMapping();
		methodObfDescs = new VersionMapping();

		obfNames = methodObfNames;
		return this;
	}

	public ASMTweaksManagerBuilder endMethod() {
		mgr.add(new ObfuscatedMethod(mgr, methodName, methodObfNames, methodObfDescs));

		obfNames = classObfNames;
		return this;
	}

	public ASMTweaksManagerBuilder startField(String name, String desc) {
		fieldName = className + "." + name;
		fieldDesc = desc;
		fieldObfNames = new VersionMapping();

		obfNames = fieldObfNames;
		return this;
	}

	public ASMTweaksManagerBuilder endField() {
		mgr.add(new ObfuscatedField(mgr, fieldName, fieldObfNames, fieldDesc));

		obfNames = classObfNames;
		return this;
	}

	public ASMTweaksManagerBuilder withMapping(String version, String obfName) {
		obfNames.add(version, obfName);
		return this;
	}

	public ASMTweaksManagerBuilder withDesc(String version, String desc) {
		methodObfDescs.add(version, desc);
		return this;
	}

	public ASMTweaksManagerBuilder addTweak(ASMTweak tweak) {
		mgr.addTweak(tweak);
		return this;
	}

	public ASMTweaksManager build() {
		return mgr;
	}
}
