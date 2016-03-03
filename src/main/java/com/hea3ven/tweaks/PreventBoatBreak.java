package com.hea3ven.tweaks;

import java.util.Set;

import com.google.common.collect.Sets;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.JumpInsnNode;

import com.hea3ven.tools.asmtweaks.*;
import com.hea3ven.tools.asmtweaks.ASMTweaksConfig.ASMTweakConfig;
import com.hea3ven.tools.asmtweaks.editors.MethodEditor;
import com.hea3ven.tools.asmtweaks.tweaks.ASMMethodModEditCode;

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
		modifications.add(new ASMMethodModEditCode("net/minecraft/entity/item/EntityBoat/onUpdate", "()V") {

			@Override
			public void handle(MethodEditor editor) {
				// Replace
				//
				// this.moveEntity(this.motionX, this.motionY, this.motionZ);
				// if (this.isCollidedHorizontally && d9 > 0.2D) {
				//     // ...
				// } else {
				//     // ...
				// }
				//
				// To
				//
				// this.moveEntity(this.motionX, this.motionY, this.motionZ);
				// if (!this.isCollidedHorizontally && this.isCollidedHorizontally && d9 > 0.2D) {
				//     // ...
				// } else {
				//     // ...
				// }
				editor.addImport("net/minecraft/entity/item/EntityBoat");
				editor.addImport("net/minecraft/entity/Entity");

				editor.setSearchMode();
				editor.methodInsn(Opcodes.INVOKEVIRTUAL, "EntityBoat", "Entity/moveEntity", "(DDD)V");
				editor.Seek(5);
				JumpInsnNode jmpNode = editor.Get();
				editor.Seek(-5);

				editor.setInsertMode();
				editor.varInsn(Opcodes.ALOAD, 0);
				editor.fieldInsn(Opcodes.GETFIELD, "Entity", "Entity/isCollidedHorizontally", "Z");
				editor.jumpInsn(Opcodes.IFNE, jmpNode.label);
			}
		});
	}
}
