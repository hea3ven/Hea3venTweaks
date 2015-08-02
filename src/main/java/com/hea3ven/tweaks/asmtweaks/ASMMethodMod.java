package com.hea3ven.tweaks.asmtweaks;

import org.objectweb.asm.tree.MethodNode;

public interface ASMMethodMod extends ASMMod {

	String getClassName();

	String getMethodName();

	void handle(ASMTweaksManager mgr, MethodNode method);

}
