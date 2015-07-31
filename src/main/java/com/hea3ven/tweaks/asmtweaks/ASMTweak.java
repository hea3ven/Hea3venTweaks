package com.hea3ven.tweaks.asmtweaks;

import org.objectweb.asm.tree.MethodNode;

public interface ASMTweak {

	void handle(ASMTweaksManager mgr, MethodNode method);

}
