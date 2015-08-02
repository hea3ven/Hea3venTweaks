package com.hea3ven.tweaks.asmtweaks;

import java.util.HashSet;

import com.google.common.collect.Sets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class ASMTweaksManager {

	private static Logger logger = LogManager.getLogger("asmtweaks.ASMTweaksManager");

	private String currentVersion;
	private HashSet<ObfuscatedClass> classes = new HashSet<ObfuscatedClass>();
	private HashSet<ObfuscatedMethod> methods = new HashSet<ObfuscatedMethod>();
	private HashSet<ObfuscatedField> fields = new HashSet<ObfuscatedField>();

	private boolean detectedObfuscation = false;
	private boolean obfuscated = false;

	private ASMTweaksConfig config;

	private HashSet<ASMTweak> tweaks = Sets.newHashSet();

	public ASMTweaksManager(String currentVersion) {
		logger.info("using mappings for version {}", currentVersion);
		this.currentVersion = currentVersion;
		config = new ASMTweaksConfig();
	}

	public String getCurrentVersion() {
		return currentVersion;
	}

	public boolean isObfuscated() {
		if (!detectedObfuscation)
			throw new RuntimeException("could not detect if running in an obfuscated environment");
		return obfuscated;
	}

	public void add(ObfuscatedClass cls) {
		classes.add(cls);
	}

	public void add(ObfuscatedMethod method) {
		methods.add(method);
	}

	public void add(ObfuscatedField field) {
		fields.add(field);
	}

	public ObfuscatedClass getClass(String className) {
		for (ObfuscatedClass cls : classes) {
			if (className.equals(cls.getName()) || className.equals(cls.getObfName())) {
				return cls;
			}
		}
		return null;
	}

	public ObfuscatedMethod getMethod(String methodName) {
		for (ObfuscatedMethod method : methods) {
			if (methodName.equals(method.getName()) || methodName.equals(method.getObfName())) {
				return method;
			}
		}
		return null;
	}

	public ObfuscatedField getField(String fieldName) {
		for (ObfuscatedField field : fields) {
			if (fieldName.equals(field.getName()) || fieldName.equals(field.getObfName())) {
				return field;
			}
		}
		return null;
	}

	public void addTweak(ASMTweak tweak) {
		if (config.isEnabled(tweak)) {
			tweaks.add(tweak);
		}
	}

	public byte[] handle(String name, String transformedName, byte[] basicClass) {
		ObfuscatedClass clsName = null;
		for (ObfuscatedClass potentialClsName : classes) {
			if (potentialClsName.matchesName(name)) {
				if (!detectedObfuscation) {
					obfuscated = name.equals(potentialClsName.getObfName());
					logger.info("detected that obfuscation is {}", obfuscated);
					detectedObfuscation = true;
				}
				clsName = potentialClsName;
				break;
			}

		}
		if (clsName == null)
			return basicClass;

		ClassNode cls = null;
		for (ASMTweak tweak : tweaks) {
			for (ASMMod mod : tweak.getModifications()) {
				if (mod instanceof ASMClassMod) {
					cls = handleClassMod(tweak, (ASMClassMod) mod, clsName, cls, basicClass);
				} else if (mod instanceof ASMMethodMod) {
					cls = handleMethodMod(tweak, (ASMMethodMod) mod, clsName, cls, basicClass);
				}
			}
		}

		if (cls != null) {
			return ASMUtils.writeClass(cls);
		} else
			return basicClass;
	}

	private ClassNode handleClassMod(ASMTweak tweak, ASMClassMod mod, ObfuscatedClass clsName,
			ClassNode cls, byte[] basicClass) {
		if (clsName.matchesName(mod.getClassName())) {
			if (cls == null)
				cls = ASMUtils.readClass(basicClass);
			logger.info("applying class modification from {} to {}({})", tweak.getName(),
					clsName.getIdentifier(), clsName.getName());
			mod.handle(this, cls);
		}
		return cls;

	}

	private ClassNode handleMethodMod(ASMTweak tweak, ASMMethodMod mod, ObfuscatedClass clsName,
			ClassNode cls, byte[] basicClass) {
		if (clsName.matchesName(mod.getClassName())) {
			if (cls == null)
				cls = ASMUtils.readClass(basicClass);
			ObfuscatedMethod mthdName = getMethod(mod.getMethodName());
			MethodNode mthd = ASMUtils.getMethod(cls, mthdName.getIdentifier(), mthdName.getDesc());
			if (mthd == null) {
				logger.error("could not find method {} {} for tweak {}", mthdName.getIdentifier(),
						mthdName.getDesc(), tweak.getName());
				throw new RuntimeException("failed patching a class");
			}
			logger.info("applying method modification from {} to {}({})", tweak.getName(),
					mthdName.getIdentifier(), mthdName.getName());
			mod.handle(this, mthd);
		}
		return cls;
	}

	public void error(String msg) {
		logger.error(msg);
		throw new RuntimeException("failed patching a class");
	}
}
