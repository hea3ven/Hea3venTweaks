package com.hea3ven.tweaks;

import java.util.Iterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.hea3ven.tweaks.asmtweaks.ASMMethodTweak;
import com.hea3ven.tweaks.asmtweaks.ASMTweaksManager;
import com.hea3ven.tweaks.asmtweaks.ObfuscatedClass;
import com.hea3ven.tweaks.asmtweaks.ObfuscatedField;
import com.hea3ven.tweaks.asmtweaks.ObfuscatedMethod;

public class PreventBoatBreak implements ASMMethodTweak {

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

		ObfuscatedMethod moveEntityMethod = mgr.getMethod("net.minecraft.entity.Entity.moveEntity");
		ObfuscatedField collHorizAttr = mgr
				.getField("net.minecraft.entity.Entity.isCollidedHorizontally");
		ObfuscatedClass entityClass = mgr.getClass("net.minecraft.entity.Entity");
		ObfuscatedClass boatClass = mgr.getClass("net.minecraft.entity.item.EntityBoat");

		Iterator<AbstractInsnNode> iter = method.instructions.iterator();

		int startIndex = 0;
		while (iter.hasNext()) {
			AbstractInsnNode currentNode = iter.next();

			if (currentNode.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				MethodInsnNode methodInsnNode = (MethodInsnNode) currentNode;
				if (boatClass.getIdentifier().equals(methodInsnNode.owner)
						&& moveEntityMethod.matches(methodInsnNode.name, methodInsnNode.desc)) {
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
				new FieldInsnNode(Opcodes.GETFIELD, entityClass.getIdentifier(),
						collHorizAttr.getIdentifier(), collHorizAttr.getDesc()));
		method.instructions.insert(method.instructions.get(startIndex + 2),
				new JumpInsnNode(Opcodes.IFNE, elseLbl));
	}

}
