package com.hea3ven.tweaks;

import java.util.Set;

import com.google.common.collect.Sets;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.LabelNode;

import com.hea3ven.tools.asmtweaks.ASMMod;
import com.hea3ven.tools.asmtweaks.ASMTweak;
import com.hea3ven.tools.asmtweaks.ASMTweaksConfig.ASMTweakConfig;
import com.hea3ven.tools.asmtweaks.editors.MethodEditor;
import com.hea3ven.tools.asmtweaks.tweaks.ASMClassModAddMethod;

public class NonSolidLeaves implements ASMTweak {

	private static Set<ASMMod> modifications = Sets.newHashSet();

	@Override
	public String getName() {
		return "NonSolidLeaves";
	}

	@Override
	public Set<ASMMod> getModifications() {
		return modifications;
	}

	@Override
	public void configure(ASMTweakConfig conf) {
	}

	static {
		modifications.add(new ASMClassModAddMethod("net/minecraft/block/BlockLeaves",
				"net/minecraft/block/Block/addCollisionBoxToList",
				"(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/entity/Entity;)V") {
			@Override
			protected void handle(MethodEditor editor) {

				editor.addImport("java/util/List");
				editor.addImport("net/minecraft/block/Block");
				editor.addImport("net/minecraft/block/state/IBlockState");
				editor.addImport("net/minecraft/entity/Entity");
				editor.addImport("net/minecraft/entity/EntityLivingBase");
				editor.addImport("net/minecraft/util/math/AxisAlignedBB");
				editor.addImport("net/minecraft/util/math/BlockPos");
				editor.addImport("net/minecraft/world/World");

				LabelNode lbl1 = new LabelNode();

				editor.setInsertMode();
					editor.varInsn(Opcodes.ALOAD, 6);
				editor.typeInsn(Opcodes.INSTANCEOF, "EntityLivingBase");
				editor.jumpInsn(Opcodes.IFNE, lbl1);

				editor.varInsn(Opcodes.ALOAD, 0);
				editor.varInsn(Opcodes.ALOAD, 1);
				editor.varInsn(Opcodes.ALOAD, 2);
				editor.varInsn(Opcodes.ALOAD, 3);
				editor.varInsn(Opcodes.ALOAD, 4);
				editor.varInsn(Opcodes.ALOAD, 5);
				editor.varInsn(Opcodes.ALOAD, 6);
				editor.methodInsn(Opcodes.INVOKESPECIAL, "Block", "Block/addCollisionBoxToList",
						"(LIBlockState;LWorld;LBlockPos;LAxisAlignedBB;LList;LEntity;)V");

				editor.labelInsn(lbl1);
				editor.insn(Opcodes.RETURN);
			}
		});
	}
}
