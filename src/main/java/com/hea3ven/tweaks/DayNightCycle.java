package com.hea3ven.tweaks;

import java.util.Set;

import com.google.common.collect.Sets;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.client.Minecraft;

import com.hea3ven.tools.asmtweaks.*;
import com.hea3ven.tools.asmtweaks.ASMTweaksConfig.ASMTweakConfig;
import com.hea3ven.tools.asmtweaks.editors.ASMContext;
import com.hea3ven.tools.asmtweaks.editors.InstructionBuilder;
import com.hea3ven.tools.asmtweaks.editors.MethodEditor;
import com.hea3ven.tools.asmtweaks.editors.opcodes.FieldInsnOpcodes;
import com.hea3ven.tools.asmtweaks.editors.opcodes.InsnOpcodes;
import com.hea3ven.tools.asmtweaks.editors.opcodes.MethodInsnOpcodes;
import com.hea3ven.tools.asmtweaks.editors.opcodes.VarInsnOpcodes;
import com.hea3ven.tools.asmtweaks.tweaks.ASMClassModAddMethod;
import com.hea3ven.tools.asmtweaks.tweaks.ASMMethodModEditCode;
import com.hea3ven.tools.asmtweaks.tweaks.ASMMethodModReplaceAllCalls;

public class DayNightCycle implements ASMTweak {

	public static double dayLengthMultiplier = 1 / 1.0d;
	public static float dayToNightRatio = 2 * 0.5f;

	private static Set<ASMMod> modifications = Sets.newHashSet();

	@Override
	public String getName() {
		return "DayNightCycle";
	}

	@Override
	public Set<ASMMod> getModifications() {
		return modifications;
	}

	@Override
	public void configure(ASMTweakConfig conf) {
		dayLengthMultiplier = 1.0d / conf.getDouble("cycleLengthMultiplier", 1.0d);
		dayToNightRatio = 2.0f * conf.getFloat("dayToNightRatio", 0.5f);
	}

	static {
		modifications.add(new EditServerTick());

		modifications.add(new EditClientTick());

		modifications.add(new ASMMethodMod() {

			@Override
			public String getClassName() {
				return "net/minecraft/world/storage/WorldInfo";
			}

			@Override
			public String getMethodName() {
				return "net/minecraft/world/storage/WorldInfo.getWorldTime";
			}

			@Override
			public String getMethodDesc() {
				return "()J";
			}

			@Override
			public void handle(ASMTweaksManager mgr, MethodNode method) {
				method.name = "getRealWorldTime";
			}
		});

		modifications.add(new ASMMethodMod() {

			@Override
			public String getClassName() {
				return "net/minecraft/world/storage/WorldInfo";
			}

			@Override
			public String getMethodName() {
				return "net/minecraft/world/storage/WorldInfo.setWorldTime";
			}

			@Override
			public String getMethodDesc() {
				return "(J)V";
			}

			@Override
			public void handle(ASMTweaksManager mgr, MethodNode method) {
				method.name = "setRealWorldTime";
			}
		});
		modifications.add(new CreateNewWorldInfoGetWorldTime());

		modifications.add(new CreateNewWorldInfoSetWorldTime());

		modifications.add(new EditWorldProviderCalculateCelestialAngle());

		modifications.add(new ASMMethodModReplaceAllCalls("com/hea3ven/tweaks/DayNightCycleFunctions.addTick",
				"(Lnet/minecraft/world/storage/WorldInfo;)V",
				"net/minecraft/world/storage/WorldInfo.getWorldTime",
				"net/minecraft/world/storage/WorldInfo.getRealWorldTime", "()J", ForgeObfLevel.SRG));
		modifications.add(new ASMMethodModReplaceAllCalls("com/hea3ven/tweaks/DayNightCycleFunctions.addTick",
				"(Lnet/minecraft/world/storage/WorldInfo;)V",
				"net/minecraft/world/storage/WorldInfo.setWorldTime",
				"net/minecraft/world/storage/WorldInfo.setRealWorldTime", "(J)V", ForgeObfLevel.SRG));
		modifications.add(
				new ASMMethodModReplaceAllCalls("com/hea3ven/tweaks/DayNightCycleFunctions.getWorldTime",
						"(Lnet/minecraft/world/storage/WorldInfo;)J",
						"net/minecraft/world/storage/WorldInfo.getWorldTime",
						"net/minecraft/world/storage/WorldInfo.getRealWorldTime", "()J",
						ForgeObfLevel.SRG));
		modifications.add(
				new ASMMethodModReplaceAllCalls("com/hea3ven/tweaks/DayNightCycleFunctions.setWorldTime",
						"(Lnet/minecraft/world/storage/WorldInfo;J)V",
						"net/minecraft/world/storage/WorldInfo.setWorldTime",
						"net/minecraft/world/storage/WorldInfo.setRealWorldTime", "(J)V",
						ForgeObfLevel.SRG));
	}

	private static class EditServerTick extends ASMMethodModEditCode {
		public EditServerTick() {
			super("net/minecraft/world/WorldServer.tick", "()V");
		}

		@Override
		protected void handle(MethodEditor editor) {
			// Replace
			// > this.worldInfo.setWorldTime(this.worldInfo.getWorldTime() + 1L);
			// To
			// > TimeTweaksManager.addTick(this.worldInfo);

			ASMContext ctx = new ASMContext();
			ctx.addImport("net/minecraft/world/World");
			ctx.addImport("net/minecraft/world/storage/WorldInfo");
			ctx.addImport("net/minecraft/world/WorldServer");
			ctx.addImport("com/hea3ven/tweaks/DayNightCycleFunctions");

			InstructionBuilder replaceCode = editor.newInstructionBuilder(ctx)
					.varInsn(VarInsnOpcodes.ALOAD, 0)
					.fieldInsn(FieldInsnOpcodes.GETFIELD, "WorldServer", "World.worldInfo", "LWorldInfo;")
					.methodInsn(MethodInsnOpcodes.INVOKEVIRTUAL, "WorldInfo", "WorldInfo.getWorldTime", "()J")
					.insn(InsnOpcodes.LCONST_1)
					.insn(InsnOpcodes.LADD)
					.methodInsn(MethodInsnOpcodes.INVOKEVIRTUAL, "WorldInfo", "WorldInfo.setWorldTime",
							"(J)V");

			editor.setSearchExactMode();
			editor.apply(replaceCode);

			editor.setRemoveExactMode();
			editor.apply(replaceCode);

			editor.setInsertMode();
			editor.apply(editor.newInstructionBuilder(ctx)
					.methodInsn(MethodInsnOpcodes.INVOKESTATIC, "DayNightCycleFunctions",
							"DayNightCycleFunctions.addTick", "(LWorldInfo;)V"));
		}
	}

	private static class EditClientTick extends ASMMethodModEditCode {
		public EditClientTick() {
			super("net/minecraft/client/multiplayer/WorldClient.tick", "()V");
		}

		@Override
		protected void handle(MethodEditor editor) {
			// Replace
			// > this.setWorldTime(this.getWorldTime() + 1L);
			// To
			// > TimeTweaksManager.addTick(this.provider);

			ASMContext ctx = new ASMContext();
			ctx.addImport("net/minecraft/world/storage/WorldInfo");
			ctx.addImport("net/minecraft/world/World");
			ctx.addImport("net/minecraft/client/multiplayer/WorldClient");
			ctx.addImport("com/hea3ven/tweaks/DayNightCycleFunctions");

			editor.setSearchExactMode();
			editor.apply(editor.newInstructionBuilder(ctx)
					.methodInsn(MethodInsnOpcodes.INVOKEVIRTUAL, "WorldClient", "World.getWorldTime", "()J"));

			editor.setRemoveExactMode();
			editor.Seek(-1);
			editor.apply(editor.newInstructionBuilder(ctx)
					.varInsn(VarInsnOpcodes.ALOAD, 0)
					.methodInsn(MethodInsnOpcodes.INVOKEVIRTUAL, "WorldClient", "World.getWorldTime", "()J")
					.insn(InsnOpcodes.LCONST_1)
					.insn(InsnOpcodes.LADD)
					.methodInsn(MethodInsnOpcodes.INVOKEVIRTUAL, "WorldClient", "WorldClient.setWorldTime",
							"(J)V"));

			editor.setInsertMode();
//			editor.Seek(-1);
			editor.apply(editor.newInstructionBuilder(ctx)
					.fieldInsn(FieldInsnOpcodes.GETFIELD, "WorldClient", "World.worldInfo", "LWorldInfo;")
					.methodInsn(MethodInsnOpcodes.INVOKESTATIC, "DayNightCycleFunctions",
							"DayNightCycleFunctions.addTick", "(LWorldInfo;)V"));
		}
	}

	private static class CreateNewWorldInfoGetWorldTime extends ASMClassModAddMethod {
		public CreateNewWorldInfoGetWorldTime() {
			super("net/minecraft/world/storage/WorldInfo",
					"net/minecraft/world/storage/WorldInfo.getWorldTime", "()J");
		}

		@Override
		protected void handle(MethodEditor editor) {
			// > long getWorldTime() {
			// >     return TimeTweaksManager.getWorldTime(this);
			// > }

			ASMContext ctx = new ASMContext();
			ctx.addImport("net/minecraft/world/storage/WorldInfo");
			ctx.addImport("com/hea3ven/tweaks/DayNightCycleFunctions");

			editor.setInsertMode();
			editor.apply(editor.newInstructionBuilder(ctx)
					.varInsn(VarInsnOpcodes.ALOAD, 0)
					.methodInsn(MethodInsnOpcodes.INVOKESTATIC, "DayNightCycleFunctions",
							"DayNightCycleFunctions.getWorldTime", "(LWorldInfo;)J")
					.insn(InsnOpcodes.LRETURN));
		}
	}

	private static class CreateNewWorldInfoSetWorldTime extends ASMClassModAddMethod {
		public CreateNewWorldInfoSetWorldTime() {
			super("net/minecraft/world/storage/WorldInfo",
					"net/minecraft/world/storage/WorldInfo.setWorldTime", "(J)V");
		}

		@Override
		protected void handle(MethodEditor editor) {
			// > void setWorldTime(long time) {
			// >     TimeTweaksManager.setWorldTime(this, time);
			// > }

			ASMContext ctx = new ASMContext();
			ctx.addImport("net/minecraft/world/storage/WorldInfo");
			ctx.addImport("com/hea3ven/tweaks/DayNightCycleFunctions");

			editor.setInsertMode();
			editor.apply(editor.newInstructionBuilder(ctx)
					.varInsn(VarInsnOpcodes.ALOAD, 0)
					.varInsn(VarInsnOpcodes.LLOAD, 1)
					.methodInsn(MethodInsnOpcodes.INVOKESTATIC, "DayNightCycleFunctions",
							"DayNightCycleFunctions.setWorldTime", "(LWorldInfo;J)V")
					.insn(InsnOpcodes.RETURN));
		}
	}

	private static class EditWorldProviderCalculateCelestialAngle extends ASMMethodModEditCode {
		public EditWorldProviderCalculateCelestialAngle() {
			super("net/minecraft/world/WorldProvider.calculateCelestialAngle", "(JF)F");
		}

		@Override
		protected void handle(MethodEditor editor) {
			ASMContext ctx = new ASMContext();
			ctx.addImport("com/hea3ven/tweaks/DayNightCycleFunctions");
			editor.setInsertMode();
			editor.apply(editor.newInstructionBuilder(ctx)
					.varInsn(VarInsnOpcodes.LLOAD, 1)
					.varInsn(VarInsnOpcodes.FLOAD, 3)
					.methodInsn(MethodInsnOpcodes.INVOKESTATIC, "DayNightCycleFunctions",
							"DayNightCycleFunctions.calculateCelestialAngle", "(JF)F")
					.insn(InsnOpcodes.FRETURN));
		}
	}
}
