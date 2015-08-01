package com.hea3ven.tweaks.asmtweaks;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class VersionMapping {

	private HashMap<Pattern, String> map = new HashMap<Pattern, String>();

	public void add(String version, String obfName) {
		map.put(Pattern.compile(version), obfName);
	}

	public String get(String currentVersion) {
		for (Entry<Pattern, String> entry : map.entrySet()) {
			if (entry.getKey().matcher(currentVersion).matches())
				return entry.getValue();
		}
		return null;
	}

}
