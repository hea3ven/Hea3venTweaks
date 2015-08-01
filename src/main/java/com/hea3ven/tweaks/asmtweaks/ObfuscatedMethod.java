package com.hea3ven.tweaks.asmtweaks;

public class ObfuscatedMethod extends Obfuscation {

	private VersionMapping descs;

	public ObfuscatedMethod(ASMTweaksManager mgr, String name, VersionMapping obfNames,
			VersionMapping descs) {
		super(mgr, name, obfNames);
		this.descs = descs;
	}

	public String getDesc() {
		String deobfDesc = descs.get(mgr.getCurrentVersion());
		StringBuilder obfDesc = new StringBuilder();
		int i = 0;
		while (i < deobfDesc.length()) {
			if (deobfDesc.charAt(i) != 'L') {
				obfDesc.append(deobfDesc.charAt(i));
				i++;
				continue;
			}
			int end = deobfDesc.indexOf(';', i);
			if (end == -1)
				throw new RuntimeException("missing ending ; in desc '" + deobfDesc + "'");
			String className = deobfDesc.substring(i + 1, end);
			ObfuscatedClass cls = mgr.getClass(className);
			obfDesc.append('L');
			obfDesc.append(cls != null ? cls.getPath() : className);
			obfDesc.append(';');
			i = end + 1;
		}
		return obfDesc.toString();
	}

	public boolean matches(String name, String desc) {
		return name.equals(getIdentifier()) && desc.equals(this.getDesc());

	}
}
