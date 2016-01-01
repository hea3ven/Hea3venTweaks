package com.hea3ven.tweaks;

import java.util.Set;

import com.google.common.collect.Sets;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.hea3ven.tools.asmtweaks.ASMClassMod;
import com.hea3ven.tools.asmtweaks.ASMMod;
import com.hea3ven.tools.asmtweaks.ASMTweak;
import com.hea3ven.tools.asmtweaks.ASMTweaksConfig.ASMTweakConfig;
import com.hea3ven.tools.asmtweaks.ASMTweaksManager;
import com.hea3ven.tools.asmtweaks.ASMUtils;
import com.hea3ven.tools.mappings.ClsMapping;
import com.hea3ven.tools.mappings.FldMapping;
import com.hea3ven.tools.mappings.MthdMapping;

public class RuntimeObfuscation implements ASMTweak {

	private static Set<ASMMod> modifications = null;

	private static Set<String> clss;

	public RuntimeObfuscation(String[] clss) {
		RuntimeObfuscation.clss = Sets.newHashSet(clss);
	}

	@Override
	public String getName() {
		return "RuntimeObfuscation";
	}

	@Override
	public void configure(ASMTweakConfig conf) {
	}

	@Override
	public Set<ASMMod> getModifications() {
		if (modifications == null) {
			modifications = Sets.newHashSet();
			for (final String cls : clss) {
				modifications.add(new ASMClassMod() {

					@Override
					public String getClassName() {
						return cls;
					}

					@Override
					public void handle(ASMTweaksManager mgr, ClassNode cls) {
						if (!mgr.isObfuscated())
							return;

						for (MethodNode mthd : cls.methods) {
							mthd.desc = ASMUtils.obfuscateDesc(mgr, mthd.desc);
							for (int i = 0; i < mthd.instructions.size(); i++) {
								if (mthd.instructions.get(i) instanceof FieldInsnNode) {
									FieldInsnNode node = (FieldInsnNode) mthd.instructions.get(i);
									ClsMapping ownerCls = mgr.getClass(node.owner);
									if (ownerCls != null) {
										node.owner = ownerCls.getSrcPath();
										FldMapping ownerFld =
												mgr.getField(ownerCls.getDstName() + "/" + node.name);
										if (ownerFld != null)
											node.name = ownerFld.getSrcName();
									}
									node.desc = ASMUtils.obfuscateDesc(mgr, node.desc);
								} else if (mthd.instructions.get(i) instanceof MethodInsnNode) {
									MethodInsnNode node = (MethodInsnNode) mthd.instructions.get(i);
									ClsMapping ownerCls = mgr.getClass(node.owner);
									if (ownerCls != null) {
										node.owner = ownerCls.getSrcPath();
										MthdMapping mthdMap =
												mgr.getMethod(ownerCls.getDstName() + "/" + node.name,
														node.desc);
										if (mthdMap != null)
											node.name = mthdMap.getDstName();
									}
									node.desc = ASMUtils.obfuscateDesc(mgr, node.desc);
								}
							}
							for (LocalVariableNode varNode : mthd.localVariables) {
								varNode.desc = ASMUtils.obfuscateDesc(mgr, varNode.desc);
							}
						}
					}
				});
			}
		}
		return modifications;
	}
}
