package com.hea3ven.tweaks.asmtweaks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class ASMTweaksConfig {

	public class ASMTweakConfig {

		private ASMTweak tweak;

		public ASMTweakConfig(ASMTweak tweak) {
			this.tweak = tweak;
		}

		public Boolean getBoolean(String name, Boolean defaultValue) {
			return Boolean.parseBoolean(getTweakConfigValue(tweak, name, defaultValue.toString()));
		}

		public String getString(String name, String defaultValue) {
			return getTweakConfigValue(tweak, name, defaultValue.toString());
		}

		public Integer getInt(String name, Integer defaultValue) {
			return Integer.parseInt(getTweakConfigValue(tweak, name, defaultValue.toString()));
		}

		public Float getFloat(String name, Float defaultValue) {
			return Float.parseFloat(getTweakConfigValue(tweak, name, defaultValue.toString()));
		}

		public Double getDouble(String name, Double defaultValue) {
			return Double.parseDouble(getTweakConfigValue(tweak, name, defaultValue.toString()));
		}
	}

	private static File getConfigFile() {
		String args = System.getProperty("sun.java.command");
		int gdIndex = args.indexOf("--gameDir");
		if (gdIndex == -1)
			return new File(".", new File("config", "asmtweaks.conf").toString());
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
		return Boolean.parseBoolean(getTweakConfigValue(tweak, "enabled", "true"));
	}

	private String getTweakConfig(ASMTweak tweak, String configName) {
		return tweak.getName() + "." + configName;
	}

	private String getTweakConfigValue(ASMTweak tweak, String configName, String defaultValue) {
		if (!props.containsKey(getTweakConfig(tweak, configName))) {
			props.setProperty(getTweakConfig(tweak, configName), defaultValue.toString());
			save();
		}
		return props.getProperty(getTweakConfig(tweak, configName));
	}

	public void setConfig(String configName, String value) {
		props.setProperty(configName, value);
		save();
	}

	public ASMTweakConfig getTweakConfig(ASMTweak tweak) {
		return new ASMTweakConfig(tweak);
	}
}
