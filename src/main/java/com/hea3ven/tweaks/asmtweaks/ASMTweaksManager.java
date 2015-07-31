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
	private HashSet<ObfuscatedField> fields = new HashSet<ObfuscatedField>();
	private HashMap<ObfuscatedClass, ASMClassTweak> classTweaks = new HashMap<ObfuscatedClass, ASMClassTweak>();
	private HashMap<ObfuscatedClass, HashMap<ObfuscatedMethod, ASMMethodTweak>> methodTweaks = new HashMap<ObfuscatedClass, HashMap<ObfuscatedMethod, ASMMethodTweak>>();

	private boolean detectedObfuscation = false;
	private boolean obfuscated = false;

	public ASMTweaksManager(String currentVersion) {
		logger.info("using mappings for version {}", currentVersion);
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

	public ObfuscatedMethod getMethod(ObfuscatedClass cls, String methodName) {
		for (ObfuscatedMethod method : methods) {
			if (method.getOwner() == cls && (methodName.equals(method.getName())
					|| methodName.equals(method.getObfName()))) {
				return method;
			}
		}
		return null;
	}

	public ObfuscatedField getField(ObfuscatedClass cls, String fieldName) {
		for (ObfuscatedField field : fields) {
			if (field.getOwner() == cls && (fieldName.equals(field.getName())
					|| fieldName.equals(field.getObfName()))) {
				return field;
			}
		}
		return null;
	}

	public void addTweak(ObfuscatedClass cls, ASMClassTweak tweak) {
		if (classTweaks.containsKey(cls)) {
			// TODO: Handle conflict of two tweaks modifying the same class
		}
		classTweaks.put(cls, tweak);
	}

	public void addTweak(ObfuscatedClass cls, ObfuscatedMethod method, ASMMethodTweak tweak) {
		if (!methodTweaks.containsKey(cls))
			methodTweaks.put(cls, new HashMap<ObfuscatedMethod, ASMMethodTweak>());
		if (methodTweaks.get(cls).containsKey(method)) {
			// TODO: Handle conflict of two tweaks modifying the same method
		}
		methodTweaks.get(cls).put(method, tweak);
	}

	public byte[] handle(String name, String transformedName, byte[] basicClass) {
		ClassNode cls = null;
		for (Entry<ObfuscatedClass, ASMClassTweak> clsEntry : classTweaks.entrySet()) {
			if (clsEntry.getKey().matchesName(name)) {
				if (!detectedObfuscation) {
					obfuscated = name.equals(clsEntry.getKey().getObfName());
					logger.info("detected that obfuscation is {}", obfuscated);
					detectedObfuscation = true;
				}
				logger.info("applying tweak {} to {}({})",
						clsEntry.getValue().getClass().getSimpleName(), name,
						clsEntry.getKey().getName());
				if (cls == null)
					cls = readClass(basicClass);
				clsEntry.getValue().handle(this, cls);
			}
		}
		for (Entry<ObfuscatedClass, HashMap<ObfuscatedMethod, ASMMethodTweak>> clsEntry : methodTweaks
				.entrySet()) {
			if (clsEntry.getKey().matchesName(name)) {
				if (!detectedObfuscation) {
					obfuscated = name.equals(clsEntry.getKey().getObfName());
					logger.info("detected that obfuscation is {}", obfuscated);
					detectedObfuscation = true;
				}
				logger.info("applying patches to {}({})", name, clsEntry.getKey().getName());
				if (cls == null)
					cls = readClass(basicClass);
				cls = handle(clsEntry.getValue(), cls);
			}
		}
		if (cls != null)
			return writeClass(cls);
		else
			return basicClass;
	}

	public void error(String msg) {
		logger.error(msg);
		throw new RuntimeException("failed patching a class");
	}

	private ClassNode handle(HashMap<ObfuscatedMethod, ASMMethodTweak> clsTweaks, ClassNode cls) {
		int i = 1;
		for (Entry<ObfuscatedMethod, ASMMethodTweak> entry : clsTweaks.entrySet()) {
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
			i++;
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
