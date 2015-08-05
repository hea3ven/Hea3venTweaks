package com.hea3ven.tweaks.asmtweaks;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class ASMUtils {
	public static ClassNode readClass(InputStream stream) {
		ClassReader classReader;
		try {
			classReader = new ClassReader(stream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return readClass(classReader);
	}

	public static ClassNode readClass(byte[] basicClass) {
		return readClass(new ClassReader(basicClass));
	}

	private static ClassNode readClass(ClassReader classReader) {
		ClassNode classNode = new ClassNode();
		classReader.accept(classNode, 0);
		return classNode;
	}

	public static byte[] writeClass(ClassNode classNode) {
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}

	public static MethodNode getMethod(ClassNode classNode, String methodName, String methodDesc) {
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while (methods.hasNext()) {
			MethodNode m = methods.next();
			if ((m.name.equals(methodName) && m.desc.equals(methodDesc)))
				return m;
		}
		return null;
	}

	public static String obfuscateDesc(ASMTweaksManager mgr, String deobfDesc) {
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
			ObfuscatedClass cls = mgr.getClass(className.replace('/', '.'));
			obfDesc.append('L');
			obfDesc.append(cls != null ? cls.getPath() : className);
			obfDesc.append(';');
			i = end + 1;
		}
		return obfDesc.toString();
	}
}
