package com.hea3ven.tweaks.asmtweaks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class ASMTweaksConfig {

	private static File getConfigFile() {
		String args = System.getProperty("sun.java.command");
		int gdIndex = args.indexOf("--gameDir");
		if (gdIndex == -1)
			return new File(".");
		gdIndex += 1;
		int gdEnd = args.indexOf(" --", gdIndex + 9);
		if (gdEnd == -1)
			gdEnd = args.length() - 1;

		File gameDir = new File(args.substring(gdIndex + 9, gdEnd));
		return new File(gameDir, new File("config", "asmtweaks.conf").toString());
	}

	private File configPath;
	private Properties props;

	public ASMTweaksConfig() {
		configPath = getConfigFile();
		parseConfig();
	}

	public void save() {
		File parent = new File(configPath.getParent());
		if (!parent.exists())
			parent.mkdirs();
		try {
			props.store(new FileOutputStream(configPath, false), "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseConfig() {
		props = new Properties();
		try {
			props.load(new FileInputStream(configPath));
		} catch (Exception e) {
			// Use default configuration
		}
	}

	public boolean isEnabled(ASMTweak tweak) {
		if (!props.containsKey(getTweakConfig(tweak, "enabled"))) {
			props.setProperty(getTweakConfig(tweak, "enabled"), "true");
			save();
		}
		return Boolean.parseBoolean(props.getProperty(getTweakConfig(tweak, "enabled")));
	}

	private String getTweakConfig(ASMTweak tweak, String configName) {
		return tweak.getName() + "." + configName;
	}

}
