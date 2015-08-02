package com.hea3ven.tweaks.asmtweaks;

import org.objectweb.asm.tree.ClassNode;

public interface ASMClassMod extends ASMMod {

	String getClassName();

	void handle(ASMTweaksManager mgr, ClassNode cls);

}
