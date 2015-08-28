package com.hea3ven.tweaks;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.hea3ven.tools.asmtweaks.ASMClassMod;
import com.hea3ven.tools.asmtweaks.ASMMethodMod;
import com.hea3ven.tools.asmtweaks.ASMMod;
import com.hea3ven.tools.asmtweaks.ASMTweak;
import com.hea3ven.tools.asmtweaks.ASMTweaksConfig.ASMTweakConfig;
import com.hea3ven.tools.asmtweaks.ASMTweaksManager;
import com.hea3ven.tools.asmtweaks.ASMUtils;
import com.hea3ven.tools.mappings.ClsMapping;
import com.hea3ven.tools.mappings.FldMapping;
import com.hea3ven.tools.mappings.MthdMapping;

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
		modifications.add(new ASMMethodMod() {

			@Override
			public String getClassName() {
				return "net/minecraft/world/WorldServer";
			}

			@Override
			public String getMethodName() {
				return "net/minecraft/world/World/tick";
			}

			@Override
			public void handle(ASMTweaksManager mgr, MethodNode method) {
				// Replace
				// > this.worldInfo.setWorldTime(this.worldInfo.getWorldTime() + 1L);
				// To
				// > TimeTweaksManager.addTick(this.worldInfo);

				MthdMapping getWorldTimeMthd = mgr
						.getMethod("net/minecraft/world/storage/WorldInfo/getWorldTime");
				Iterator<AbstractInsnNode> iter = method.instructions.iterator();
				int index = 0;
				while (iter.hasNext()) {
					AbstractInsnNode currentNode = iter.next();

					if (currentNode.getOpcode() == Opcodes.INVOKEVIRTUAL) {
						MethodInsnNode methodInsnNode = (MethodInsnNode) currentNode;
						if (getWorldTimeMthd.matches(methodInsnNode.owner, methodInsnNode.name,
								methodInsnNode.desc)

						&& currentNode.getNext().getOpcode() == Opcodes.LCONST_1) {
							// Found the call
							index = method.instructions.indexOf(currentNode) - 2;
						}
					}
				}
				if (index == 0)
					mgr.error("Could not find call in WorldServer.tick method");

				method.instructions.remove(method.instructions.get(index));
				method.instructions.remove(method.instructions.get(index));
				method.instructions.remove(method.instructions.get(index));
				method.instructions.remove(method.instructions.get(index));
				method.instructions.remove(method.instructions.get(index));
				method.instructions.remove(method.instructions.get(index));
				method.instructions.insertBefore(method.instructions.get(index), new MethodInsnNode(
						Opcodes.INVOKESTATIC, "com/hea3ven/tweaks/DayNightCycleFunctions",
						"addTick",
						"(L" + mgr.getClass("net/minecraft/world/storage/WorldInfo").getSrcPath()
								+ ";)V"));
			}
		});

		modifications.add(new ASMMethodMod() {

			@Override
			public String getClassName() {
				return "net/minecraft/client/multiplayer/WorldClient";
			}

			@Override
			public String getMethodName() {
				return "net/minecraft/world/World/tick";
			}

			@Override
			public void handle(ASMTweaksManager mgr, MethodNode method) {
				// Replace
				// > this.setWorldTime(this.getWorldTime() + 1L);
				// To
				// > TimeTweaksManager.addTick(this.provider);

				MthdMapping getWorldTimeMthd = mgr
						.getMethod("net/minecraft/client/multiplayer/WorldClient/getWorldTime");
				int index = 0;
				Iterator<AbstractInsnNode> iter = method.instructions.iterator();
				while (iter.hasNext()) {
					AbstractInsnNode currentNode = iter.next();

					if (currentNode.getOpcode() == Opcodes.INVOKEVIRTUAL) {
						MethodInsnNode methodInsnNode = (MethodInsnNode) currentNode;
						if (getWorldTimeMthd.matches(methodInsnNode.owner, methodInsnNode.name,
								methodInsnNode.desc)
								&& currentNode.getNext().getOpcode() == Opcodes.LCONST_1) {
							index = method.instructions.indexOf(currentNode) - 1;
						}
					}
				}
				if (index == 0)
					mgr.error("Could not find call in WorldClient.tick method");

				method.instructions.remove(method.instructions.get(index));
				method.instructions.remove(method.instructions.get(index));
				method.instructions.remove(method.instructions.get(index));
				method.instructions.remove(method.instructions.get(index));
				method.instructions.remove(method.instructions.get(index));
				FldMapping worldInfoFld = mgr.getField("net.minecraft.world.World.worldInfo");
				ClsMapping worldClientCls = mgr
						.getClass("net/minecraft/client/multiplayer/WorldClient");
				method.instructions.insertBefore(method.instructions.get(index),
						new FieldInsnNode(Opcodes.GETFIELD, worldClientCls.getSrcPath(),
								worldInfoFld.getSrcName(), worldInfoFld.getDesc().getSrc()));
				method.instructions.insertBefore(method.instructions.get(index + 1),
						new MethodInsnNode(Opcodes.INVOKESTATIC,
								"com/hea3ven/tweaks/DayNightCycleFunctions", "addTick",
								"(L" + mgr
										.getClass("net/minecraft/world/storage/WorldInfo")
										.getSrcPath() + ";)V"));
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
			public void handle(ASMTweaksManager mgr, MethodNode method) {
				method.name = "setRealWorldTime";
			}
		});

		modifications.add(new ASMClassMod() {

			@Override
			public String getClassName() {
				return "net/minecraft/world/storage/WorldInfo";
			}

			@Override
			public void handle(ASMTweaksManager mgr, ClassNode cls) {
				// > long getWorldTime() {
				// >     return TimeTweaksManager.getWorldTime(this);
				// > }
				MthdMapping getWorldTimeMthdName = mgr
						.getMethod("net/minecraft/world/storage/WorldInfo/getWorldTime");
				MethodNode getWorldTimeMethod = new MethodNode(Opcodes.ASM4, Opcodes.ACC_PUBLIC,
						getWorldTimeMthdName.getSrcName(), getWorldTimeMthdName.getDesc().getSrc(),
						null, null);
				getWorldTimeMethod.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				getWorldTimeMethod.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
						"com/hea3ven/tweaks/DayNightCycleFunctions", "getWorldTime",
						"(L" + mgr.getClass("net/minecraft/world/storage/WorldInfo").getSrcPath()
								+ ";)J"));
				getWorldTimeMethod.instructions.add(new InsnNode(Opcodes.LRETURN));
				cls.methods.add(getWorldTimeMethod);

				// > void setWorldTime(long time) {
				// >     TimeTweaksManager.setWorldTime(this, time);
				// > }
				MthdMapping setWorldTimeMthdName = mgr
						.getMethod("net/minecraft/world/storage/WorldInfo/setWorldTime");
				MethodNode setWorldTimeMethod = new MethodNode(Opcodes.ASM4, Opcodes.ACC_PUBLIC,
						setWorldTimeMthdName.getSrcName(), setWorldTimeMthdName.getDesc().getSrc(),
						null, null);
				setWorldTimeMethod.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				setWorldTimeMethod.instructions.add(new VarInsnNode(Opcodes.LLOAD, 1));
				setWorldTimeMethod.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
						"com/hea3ven/tweaks/DayNightCycleFunctions", "setWorldTime",
						"(L" + mgr.getClass("net/minecraft/world/storage/WorldInfo").getSrcPath()
								+ ";J)V"));
				setWorldTimeMethod.instructions.add(new InsnNode(Opcodes.RETURN));
				cls.methods.add(setWorldTimeMethod);
			}

		});

		modifications.add(new ASMClassMod() {

			@Override
			public String getClassName() {
				return "net.minecraft.world.WorldProvider";
			}

			@Override
			public void handle(ASMTweaksManager mgr, ClassNode cls) {
				MthdMapping calcCelAngleMethodName = mgr
						.getMethod("net/minecraft/world/WorldProvider/calculateCelestialAngle");
				MethodNode calcCelAngleMethod = ASMUtils.getMethod(cls,
						calcCelAngleMethodName.getSrcName(),
						calcCelAngleMethodName.getDesc().getSrc());
				cls.methods.remove(calcCelAngleMethod);

				// > float calculateCelestialAngle(long time, float off) {
				// >     return TimeTweaksManager.calculateCelestialAngle(time, off);
				// > }
				calcCelAngleMethod = new MethodNode(Opcodes.ASM4, Opcodes.ACC_PUBLIC,
						calcCelAngleMethodName.getSrcName(),
						calcCelAngleMethodName.getDesc().getSrc(), null, null);
				calcCelAngleMethod.instructions.add(new VarInsnNode(Opcodes.LLOAD, 1));
				calcCelAngleMethod.instructions.add(new VarInsnNode(Opcodes.FLOAD, 3));
				calcCelAngleMethod.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
						"com/hea3ven/tweaks/DayNightCycleFunctions", "calculateCelestialAngle",
						"(JF)F"));
				calcCelAngleMethod.instructions.add(new InsnNode(Opcodes.FRETURN));

				cls.methods.add(calcCelAngleMethod);
			}
		});

		modifications.add(new ASMClassMod() {

			@Override
			public String getClassName() {
				return "com/hea3ven/tweaks/DayNightCycleFunctions";
			}

			@Override
			public void handle(ASMTweaksManager mgr, ClassNode cls) {
				ClsMapping worldInfoCls = mgr.getClass("net/minecraft/world/storage/WorldInfo");
				MthdMapping getWorldTimeMethod = mgr
						.getMethod("net/minecraft/world/storage/WorldInfo/getWorldTime");
				MthdMapping setWorldTimeMethod = mgr
						.getMethod("net/minecraft/world/storage/WorldInfo/setWorldTime");
				for (MethodNode mthd : cls.methods) {
					mthd.desc = ASMUtils.obfuscateDesc(mgr, mthd.desc);
					for (int i = 0; i < mthd.instructions.size(); i++) {
						if (mthd.instructions.get(i) instanceof MethodInsnNode) {
							MethodInsnNode node = (MethodInsnNode) mthd.instructions.get(i);
							if (worldInfoCls.matches(node.owner)) {
								if ((node.name.equals("getWorldTime")
										|| node.name.equals("func_76073_f")
										|| getWorldTimeMethod.getSrcName().equals(node.name))
										&& getWorldTimeMethod
												.getDesc()
												.getSrc()
												.equals(node.desc)) {
									node.name = "getRealWorldTime";
								} else if ((node.name.equals("setWorldTime")
										|| node.name.equals("func_76068_b")
										|| setWorldTimeMethod.getSrcName().equals(node.name))
										&& setWorldTimeMethod
												.getDesc()
												.getSrc()
												.equals(node.desc)) {
									node.name = "setRealWorldTime";
								}
							}
						}
					}
				}
			}
		});
	}
}
