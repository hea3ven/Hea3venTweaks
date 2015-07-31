package com.hea3ven.tweaks.asmtweaks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class ASMTweaksManager {

	private static Logger logger = LogManager.getLogger("asmtweaks.ASMTweaksManager");

	private String currentVersion;
	private HashSet<ObfuscatedClass> classes = new HashSet<ObfuscatedClass>();
	private HashSet<ObfuscatedMethod> methods = new HashSet<ObfuscatedMethod>();
	private HashMap<ObfuscatedClass, HashMap<ObfuscatedMethod, ASMTweak>> tweaks = new HashMap<ObfuscatedClass, HashMap<ObfuscatedMethod, ASMTweak>>();

	private boolean detectedObfuscation = false;
	private boolean obfuscated = false;

	public ASMTweaksManager(String currentVersion) {
		logger.info("using mappings for version %s", currentVersion);
		this.currentVersion = currentVersion;
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

	public void add(ObfuscatedMethod cls) {
		methods.add(cls);
	}

	public ObfuscatedClass getClass(String className) {
		for (ObfuscatedClass cls : classes) {
			if (className.equals(cls.getName()) || className.equals(cls.getObfName())) {
				return cls;
			}
		}
		return null;
	}

	public ObfuscatedMethod getMethod(ObfuscatedClass cls, String methodName) {
		for (ObfuscatedMethod method : methods) {
			if (method.getOwner() == cls && (methodName.equals(method.getName())
					|| methodName.equals(method.getObfName()))) {
				return method;
			}
		}
		return null;
	}

	public void addTweak(ObfuscatedClass cls, ObfuscatedMethod method, ASMTweak tweak) {
		if (!tweaks.containsKey(cls))
			tweaks.put(cls, new HashMap<ObfuscatedMethod, ASMTweak>());
		if (tweaks.get(cls).containsKey(method)) {
			// TODO: Handle conflict of two tweaks modifying the same method
		}
		tweaks.get(cls).put(method, tweak);
	}

	public byte[] handle(String name, String transformedName, byte[] basicClass) {
		for (Entry<ObfuscatedClass, HashMap<ObfuscatedMethod, ASMTweak>> clsEntry : tweaks
				.entrySet()) {
			if (clsEntry.getKey().matchesName(name)) {
				if (!detectedObfuscation) {
					obfuscated = name.equals(clsEntry.getKey().getObfName());
					logger.info("detected that obfuscation is {}", obfuscated);
					detectedObfuscation = true;
				}
				logger.info("applying patches to {}({})", name, clsEntry.getKey().getName());
				return writeClass(handle(clsEntry.getValue(), readClass(basicClass)));
			}
		}
		return basicClass;
	}

	private ClassNode handle(HashMap<ObfuscatedMethod, ASMTweak> clsTweaks, ClassNode cls) {
		int i = 0;
		for (Entry<ObfuscatedMethod, ASMTweak> entry : clsTweaks.entrySet()) {
			MethodNode method = ASMUtils.getMethod(cls, entry.getKey().getIdentifier(),
					entry.getKey().getDesc());
			if (method == null) {
				logger.error("could not find method {} {} for tweak {}",
						entry.getKey().getIdentifier(), entry.getKey().getDesc(),
						entry.getValue().getClass().getSimpleName());
				throw new RuntimeException("failed patching a class");
			}
			logger.info("applying tweak ({}/{}) {} to method {}({})", i, clsTweaks.size(),
					entry.getValue().getClass().getSimpleName(), entry.getKey().getIdentifier(),
					entry.getKey().getName());
			entry.getValue().handle(this, method);
		}
		return cls;
	}

	private ClassNode readClass(byte[] basicClass) {
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		return classNode;
	}

	private byte[] writeClass(ClassNode classNode) {
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
}
