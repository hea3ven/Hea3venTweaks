package com.hea3ven.tweaks;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.hea3ven.tweaks.asmtweaks.ASMClassTweak;
import com.hea3ven.tweaks.asmtweaks.ASMTweaksManager;

public class NonSolidLeaves implements ASMClassTweak {

	@Override
	public void handle(ASMTweaksManager mgr, ClassNode cls) {
		String desc = "(L" + mgr.getClass("net.minecraft.world.World").getPath() + ";L"
				+ mgr.getClass("net.minecraft.util.BlockPos").getPath() + ";L"
				+ mgr.getClass("net.minecraft.block.state.IBlockState").getPath() + ";L"
				+ mgr.getClass("net.minecraft.util.AxisAlignedBB").getPath() + ";Ljava/util/List;L"
				+ mgr.getClass("net.minecraft.entity.Entity").getPath() + ";)V";

		MethodNode method = new MethodNode(Opcodes.ASM4, Opcodes.ACC_PUBLIC, mgr
				.getMethod(mgr.getClass("net.minecraft.block.Block"), "addCollisionBoxesToList")
				.getIdentifier(), desc, null, null);

		LabelNode lbl1 = new LabelNode();

		method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 6));
		method.instructions.add(new TypeInsnNode(Opcodes.INSTANCEOF,
				mgr.getClass("net.minecraft.entity.EntityLivingBase").getPath()));
		method.instructions.add(new JumpInsnNode(Opcodes.IFNE, lbl1));

		method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
		method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
		method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 4));
		method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 5));
		method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 6));
		method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
				mgr.getClass("net.minecraft.block.Block").getIdentifier(), mgr
						.getMethod(mgr.getClass("net.minecraft.block.Block"),
								"addCollisionBoxesToList")
						.getIdentifier(),
				desc, false));

		method.instructions.add(lbl1);
		method.instructions.add(new InsnNode(Opcodes.RETURN));

		cls.methods.add(method);
	}
}
