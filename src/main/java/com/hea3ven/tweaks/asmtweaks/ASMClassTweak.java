package com.hea3ven.tweaks.asmtweaks;

import org.objectweb.asm.tree.ClassNode;

public interface ASMClassTweak {

	void handle(ASMTweaksManager mgr, ClassNode cls);

}
