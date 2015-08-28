package com.hea3ven.tweaks;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.hea3ven.tools.asmtweaks.ASMMethodMod;
import com.hea3ven.tools.asmtweaks.ASMMod;
import com.hea3ven.tools.asmtweaks.ASMTweak;
import com.hea3ven.tools.asmtweaks.ASMTweaksConfig.ASMTweakConfig;
import com.hea3ven.tools.asmtweaks.ASMTweaksManager;
import com.hea3ven.tools.mappings.ClsMapping;
import com.hea3ven.tools.mappings.FldMapping;
import com.hea3ven.tools.mappings.MthdMapping;

public class PreventBoatBreak implements ASMTweak {

	private static Set<ASMMod> modifications = Sets.newHashSet();

	@Override
	public String getName() {
		return "PreventBoatBreak";
	}

	@Override
	public Set<ASMMod> getModifications() {
		return modifications;
	}

	@Override
	public void configure(ASMTweakConfig conf) {
	}

	static {
		modifications.add(new ASMMethodMod() {
			@Override
			public String getClassName() {
				return "net/minecraft/entity/item/EntityBoat";
			}

			@Override
			public String getMethodName() {
				return "net/minecraft/entity/item/EntityBoat/onUpdate";
			}

			@Override
			public void handle(ASMTweaksManager mgr, MethodNode method) {
				// Replace
				//
				// this.moveEntity(this.motionX, this.motionY, this.motionZ);
				// if (this.isCollidedHorizontally && d9 > 0.2D) {
				//     // ...
				// }
				// else
				// {
				//     // ...
				// }
				//
				// To
				//
				// this.moveEntity(this.motionX, this.motionY, this.motionZ);
				// if (!this.isCollidedHorizontally && this.isCollidedHorizontally && d9 > 0.2D) {
				//     // ...
				// }
				// else
				// {
				//     // ...
				// }

				MthdMapping moveEntityMethod = mgr
						.getMethod("net/minecraft/entity/item/EntityBoat/moveEntity");
				FldMapping collHorizAttr = mgr
						.getField("net/minecraft/entity/Entity/isCollidedHorizontally");
				ClsMapping entityClass = mgr.getClass("net/minecraft/entity/Entity");
				ClsMapping boatClass = mgr.getClass("net/minecraft/entity/item/EntityBoat");

				Iterator<AbstractInsnNode> iter = method.instructions.iterator();

				int startIndex = 0;
				while (iter.hasNext()) {
					AbstractInsnNode currentNode = iter.next();

					if (currentNode.getOpcode() == Opcodes.INVOKEVIRTUAL) {
						MethodInsnNode methodInsnNode = (MethodInsnNode) currentNode;
						if (moveEntityMethod.matches(methodInsnNode.owner, methodInsnNode.name,
								methodInsnNode.desc)) {
							startIndex = method.instructions.indexOf(currentNode) + 1;
						}
					}
				}
				if (startIndex == 0)
					mgr.error("could not find patch position");

				AbstractInsnNode jumpNode = method.instructions.get(startIndex + 4);
				if (jumpNode.getOpcode() != Opcodes.IFEQ)
					mgr.error("unexpected asm " + jumpNode.getOpcode());

				LabelNode elseLbl = ((JumpInsnNode) jumpNode).label;

				method.instructions.insert(method.instructions.get(startIndex + 0),
						new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.insert(method.instructions.get(startIndex + 1),
						new FieldInsnNode(Opcodes.GETFIELD, entityClass.getSrcPath(),
								collHorizAttr.getSrcName(), collHorizAttr.getDesc().getSrc()));
				method.instructions.insert(method.instructions.get(startIndex + 2),
						new JumpInsnNode(Opcodes.IFNE, elseLbl));
			}

		});
	}
}
