package com.hea3ven.tweaks;

import java.util.Set;

import com.google.common.collect.Sets;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

import com.hea3ven.tools.asmtweaks.ASMMethodMod;
import com.hea3ven.tools.asmtweaks.ASMMod;
import com.hea3ven.tools.asmtweaks.ASMTweak;
import com.hea3ven.tools.asmtweaks.ASMTweaksConfig.ASMTweakConfig;
import com.hea3ven.tools.asmtweaks.ASMTweaksManager;
import com.hea3ven.tools.asmtweaks.editors.MethodEditor;
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
		modifications.add(new ASMMethodModEditCode("net/minecraft/world/WorldServer/tick", "()V") {
			@Override
			protected void handle(MethodEditor editor) {
				// Replace
				// > this.worldInfo.setWorldTime(this.worldInfo.getWorldTime() + 1L);
				// To
				// > TimeTweaksManager.addTick(this.worldInfo);

				editor.addImport("net/minecraft/world/World");
				editor.addImport("net/minecraft/world/storage/WorldInfo");
				editor.addImport("net/minecraft/world/WorldServer");
				editor.addImport("com/hea3ven/tweaks/DayNightCycleFunctions");

				editor.setSearchMode();
				editor.methodInsn(Opcodes.INVOKEVIRTUAL, "WorldInfo", "WorldInfo/getWorldTime", "()J");
				editor.Seek(1);
				editor.methodInsn(Opcodes.INVOKEVIRTUAL, "WorldInfo", "WorldInfo/getWorldTime", "()J");

				editor.setRemoveMode();
				editor.Seek(-2);
				editor.varInsn(Opcodes.ALOAD, 0);
				editor.fieldInsn(Opcodes.GETFIELD, "WorldServer", "World/worldInfo", "LWorldInfo;");
				editor.methodInsn(Opcodes.INVOKEVIRTUAL, "WorldInfo", "WorldInfo/getWorldTime", "()J");
				editor.insn(Opcodes.LCONST_1);
				editor.insn(Opcodes.LADD);
				editor.methodInsn(Opcodes.INVOKEVIRTUAL, "WorldInfo", "WorldInfo/setWorldTime", "(J)V");

				editor.setInsertMode();
				editor.Seek(-1);
				editor.methodInsn(Opcodes.INVOKESTATIC, "DayNightCycleFunctions",
						"DayNightCycleFunctions/addTick", "(LWorldInfo;)V");
			}
		});

		modifications.add(
				new ASMMethodModEditCode("net/minecraft/client/multiplayer/WorldClient/tick", "()V") {
					@Override
					protected void handle(MethodEditor editor) {
						// Replace
						// > this.setWorldTime(this.getWorldTime() + 1L);
						// To
						// > TimeTweaksManager.addTick(this.provider);

						editor.addImport("net/minecraft/world/storage/WorldInfo");
						editor.addImport("net/minecraft/world/World");
						editor.addImport("net/minecraft/client/multiplayer/WorldClient");
						editor.addImport("com/hea3ven/tweaks/DayNightCycleFunctions");

						editor.setSearchMode();
						editor.methodInsn(Opcodes.INVOKEVIRTUAL, "WorldClient", "World/getWorldTime", "()J");

						editor.setRemoveMode();
						editor.Seek(-1);
						editor.varInsn(Opcodes.ALOAD, 0);
						editor.methodInsn(Opcodes.INVOKEVIRTUAL, "WorldClient", "World/getWorldTime", "()J");
						editor.insn(Opcodes.LCONST_1);
						editor.insn(Opcodes.LADD);
						editor.methodInsn(Opcodes.INVOKEVIRTUAL, "WorldClient", "WorldClient/setWorldTime",
								"(J)V");

						editor.setInsertMode();
						editor.Seek(-1);
						editor.fieldInsn(Opcodes.GETFIELD, "WorldClient", "World/worldInfo", "LWorldInfo;");
						editor.methodInsn(Opcodes.INVOKESTATIC, "DayNightCycleFunctions",
								"DayNightCycleFunctions/addTick", "(LWorldInfo;)V");
					}
				});

		modifications.add(new ASMMethodMod() {

			@Override
			public String getClassName() {
				return "net/minecraft/world/storage/WorldInfo";
			}

			@Override
			public String getMethodName() {
				return "net/minecraft/world/storage/WorldInfo/getWorldTime";
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
				return "net/minecraft/world/storage/WorldInfo/setWorldTime";
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
		modifications.add(new ASMClassModAddMethod("net/minecraft/world/storage/WorldInfo",
				"net/minecraft/world/storage/WorldInfo/getWorldTime", "()J") {

			@Override
			protected void handle(MethodEditor editor) {
				// > long getWorldTime() {
				// >     return TimeTweaksManager.getWorldTime(this);
				// > }
				editor.setInsertMode();
				editor.varInsn(Opcodes.ALOAD, 0);
				editor.methodInsn(Opcodes.INVOKESTATIC, "com/hea3ven/tweaks/DayNightCycleFunctions",
						"com/hea3ven/tweaks/DayNightCycleFunctions/getWorldTime",
						"(Lnet/minecraft/world/storage/WorldInfo;)J");
				editor.insn(Opcodes.LRETURN);
			}
		});

		modifications.add(new ASMClassModAddMethod("net/minecraft/world/storage/WorldInfo",
				"net/minecraft/world/storage/WorldInfo/setWorldTime", "(J)V") {

			@Override
			protected void handle(MethodEditor editor) {
				// > void setWorldTime(long time) {
				// >     TimeTweaksManager.setWorldTime(this, time);
				// > }
				editor.setInsertMode();
				editor.varInsn(Opcodes.ALOAD, 0);
				editor.varInsn(Opcodes.LLOAD, 1);
				editor.methodInsn(Opcodes.INVOKESTATIC, "com/hea3ven/tweaks/DayNightCycleFunctions",
						"com/hea3ven/tweaks/DayNightCycleFunctions/setWorldTime",
						"(Lnet/minecraft/world/storage/WorldInfo;J)V");
				editor.insn(Opcodes.RETURN);
			}
		});

		modifications.add(
				new ASMMethodModEditCode("net/minecraft/world/WorldProvider/calculateCelestialAngle",
						"(JF)F") {

					@Override
					protected void handle(MethodEditor editor) {
						editor.setRemoveMode();
						editor.setInsertMode();
						editor.varInsn(Opcodes.LLOAD, 1);
						editor.varInsn(Opcodes.FLOAD, 3);
						editor.methodInsn(Opcodes.INVOKESTATIC, "com/hea3ven/tweaks/DayNightCycleFunctions",
								"com/hea3ven/tweaks/DayNightCycleFunctions/calculateCelestialAngle", "(JF)F");
						editor.insn(Opcodes.FRETURN);
					}
				});

		modifications.add(new ASMMethodModReplaceAllCalls("com/hea3ven/tweaks/DayNightCycleFunctions/addTick",
				"(Lnet/minecraft/world/storage/WorldInfo;)V",
				"net/minecraft/world/storage/WorldInfo/getWorldTime",
				"net/minecraft/world/storage/WorldInfo/getRealWorldTime", "()J", false));
		modifications.add(new ASMMethodModReplaceAllCalls("com/hea3ven/tweaks/DayNightCycleFunctions/addTick",
				"(Lnet/minecraft/world/storage/WorldInfo;)V",
				"net/minecraft/world/storage/WorldInfo/setWorldTime",
				"net/minecraft/world/storage/WorldInfo/setRealWorldTime", "(J)V", false));
		modifications.add(
				new ASMMethodModReplaceAllCalls("com/hea3ven/tweaks/DayNightCycleFunctions/getWorldTime",
						"(Lnet/minecraft/world/storage/WorldInfo;)J",
						"net/minecraft/world/storage/WorldInfo/getWorldTime",
						"net/minecraft/world/storage/WorldInfo/getRealWorldTime", "()J", false));
		modifications.add(
				new ASMMethodModReplaceAllCalls("com/hea3ven/tweaks/DayNightCycleFunctions/setWorldTime",
						"(Lnet/minecraft/world/storage/WorldInfo;J)V",
						"net/minecraft/world/storage/WorldInfo/setWorldTime",
						"net/minecraft/world/storage/WorldInfo/setRealWorldTime", "(J)V", false));
	}
}
