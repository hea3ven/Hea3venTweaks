package com.hea3ven.tweaks;

import java.util.Set;

import com.google.common.collect.Sets;

import com.hea3ven.tools.asmtweaks.ASMMod;
import com.hea3ven.tools.asmtweaks.ASMTweak;
import com.hea3ven.tools.asmtweaks.ASMTweaksConfig.ASMTweakConfig;
import com.hea3ven.tools.asmtweaks.editors.ASMContext;
import com.hea3ven.tools.asmtweaks.editors.LabelRef;
import com.hea3ven.tools.asmtweaks.editors.MethodEditor;
import com.hea3ven.tools.asmtweaks.editors.opcodes.*;
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
				"net/minecraft/block/Block.addCollisionBoxToList",
				"(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/entity/Entity;)V") {
			@Override
			protected void handle(MethodEditor editor) {

				ASMContext ctx = new ASMContext();
				ctx.addImport("java/util/List");
				ctx.addImport("net/minecraft/block/Block");
				ctx.addImport("net/minecraft/block/state/IBlockState");
				ctx.addImport("net/minecraft/entity/Entity");
				ctx.addImport("net/minecraft/entity/EntityLivingBase");
				ctx.addImport("net/minecraft/util/math/AxisAlignedBB");
				ctx.addImport("net/minecraft/util/math/BlockPos");
				ctx.addImport("net/minecraft/world/World");

				LabelRef lbl1 = editor.createLabel();

				editor.setInsertMode();
				editor.apply(editor.newInstructionBuilder(ctx).varInsn(VarInsnOpcodes.ALOAD, 6)
						.typeInsn(TypeInsnOpcodes.INSTANCEOF, "LEntityLivingBase;")
						.jumpInsn(JumpInsnOpcodes.IFNE, lbl1)

						.varInsn(VarInsnOpcodes.ALOAD, 0)
						.varInsn(VarInsnOpcodes.ALOAD, 1)
						.varInsn(VarInsnOpcodes.ALOAD, 2)
						.varInsn(VarInsnOpcodes.ALOAD, 3)
						.varInsn(VarInsnOpcodes.ALOAD, 4)
						.varInsn(VarInsnOpcodes.ALOAD, 5)
						.varInsn(VarInsnOpcodes.ALOAD, 6)
						.methodInsn(MethodInsnOpcodes.INVOKESPECIAL, "Block", "Block.addCollisionBoxToList",
								"(LIBlockState;LWorld;LBlockPos;LAxisAlignedBB;LList;LEntity;)V")

						.label(lbl1)
						.insn(InsnOpcodes.RETURN));
			}
		});
	}
}
