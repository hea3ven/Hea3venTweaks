package com.hea3ven.tweaks.asmtweaks;

import org.objectweb.asm.tree.MethodNode;

public interface ASMMethodTweak {

	void handle(ASMTweaksManager mgr, MethodNode method);

}
