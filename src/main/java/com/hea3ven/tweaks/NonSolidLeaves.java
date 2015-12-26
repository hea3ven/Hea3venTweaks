package com.hea3ven.tweaks;

import java.util.Set;

import com.google.common.collect.Sets;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.hea3ven.tools.asmtweaks.ASMClassMod;
import com.hea3ven.tools.asmtweaks.ASMMod;
import com.hea3ven.tools.asmtweaks.ASMTweak;
import com.hea3ven.tools.asmtweaks.ASMTweaksConfig.ASMTweakConfig;
import com.hea3ven.tools.asmtweaks.ASMTweaksManager;
import com.hea3ven.tools.mappings.MthdMapping;

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
		modifications.add(new ASMClassMod() {
			@Override
			public String getClassName() {
				return "net/minecraft/block/BlockLeavesBase";
			}

			@Override
			public void handle(ASMTweaksManager mgr, ClassNode cls) {
				MthdMapping addCollboxMethod =
						mgr.getMethod("net/minecraft/block/Block/addCollisionBoxesToList",
								"(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/entity/Entity;)V");
				String desc = addCollboxMethod.getDesc().get(mgr.isObfuscated());
				MethodNode method = new MethodNode(Opcodes.ASM4, Opcodes.ACC_PUBLIC,
						addCollboxMethod.getName(mgr.isObfuscated()), desc, null, null);

				LabelNode lbl1 = new LabelNode();

				if (mgr.getCurrentVersion().equals("1.7.10")) {
					method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 7));
				} else {
					method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 6));
				}
				method.instructions.add(new TypeInsnNode(Opcodes.INSTANCEOF,
						mgr.getClass("net/minecraft/entity/EntityLivingBase").getPath(mgr.isObfuscated())));
				method.instructions.add(new JumpInsnNode(Opcodes.IFNE, lbl1));

				if (mgr.getCurrentVersion().equals("1.7.10")) {
					method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
					method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
					method.instructions.add(new IntInsnNode(Opcodes.ILOAD, 2));
					method.instructions.add(new IntInsnNode(Opcodes.ILOAD, 3));
					method.instructions.add(new IntInsnNode(Opcodes.ILOAD, 4));
					method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 5));
					method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 6));
					method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 7));
				} else {
					method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
					method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
					method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
					method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
					method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 4));
					method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 5));
					method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 6));
				}
				method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
						mgr.getClass("net/minecraft/block/Block").getPath(mgr.isObfuscated()),
						addCollboxMethod.getName(mgr.isObfuscated()), desc));

				method.instructions.add(lbl1);
				method.instructions.add(new InsnNode(Opcodes.RETURN));

				cls.methods.add(method);
			}
		});
	}
}
