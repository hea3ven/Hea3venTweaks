package com.hea3ven.tweaks;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import com.hea3ven.tweaks.asmtweaks.ASMTweaksManager;
import com.hea3ven.tweaks.asmtweaks.ASMTweaksManagerBuilder;
import com.hea3ven.tweaks.asmtweaks.ASMUtils;

public class Hea3venTweaks implements ITweaker, IClassTransformer {
	private static ASMTweaksManager asmTweaksManager = new ASMTweaksManagerBuilder(
			discoverVersion())
					.startClass("net.minecraft.entity.Entity")
					.withMapping("1.7.10", "sa")
					.withMapping("1.8", "wv")
					.withMapping("15w31[abc]", "pr")
					.startMethod("onUpdate")
					.withMapping("1.7.10", "h")
					.withMapping("1.8", "s_")
					.withMapping("15w31[abc]", "t_")
					.withDesc(".*", "()V")
					.endMethod()
					.startMethod("moveEntity")
					.withMapping("1.7.10", "d")
					.withMapping("1.8", "d")
					.withMapping("15w31[abc]", "d")
					.withDesc(".*", "(DDD)V")
					.endMethod()
					.startField("isCollidedHorizontally", "Z")
					.withMapping("1.7.10", "E")
					.withMapping("1.8", "D")
					.withMapping("15w31[abc]", "D")
					.endField()
					.endClass()
					.startClass("net.minecraft.entity.item.EntityBoat")
					.withMapping("1.7.10", "xi")
					.withMapping("1.8", "adu")
					.withMapping("15w31[abc]", "vk")
					.endClass()
					.startClass("net.minecraft.world.World")
					.withMapping("1.7.10", "ahb")
					.withMapping("1.8", "aqu")
					.withMapping("15w31[ab]", "aen")
					.withMapping("15w31c", "aeo")
					.endClass()
					.startClass("net.minecraft.util.BlockPos")
					.withMapping("1.8", "dt")
					.withMapping("15w31[abc]", "cj")
					.endClass()
					.startClass("net.minecraft.util.AxisAlignedBB")
					.withMapping("1.7.10", "azt")
					.withMapping("1.8", "brt")
					.withMapping("15w31[ab]", "awf")
					.withMapping("15w31c", "awg")
					.endClass()
					.startClass("net.minecraft.block.state.IBlockState")
					.withMapping("1.8", "bec")
					.withMapping("15w31[ab]", "anl")
					.withMapping("15w31c", "anm")
					.endClass()
					.startClass("net.minecraft.entity.EntityLivingBase")
					.withMapping("1.7.10", "sv")
					.withMapping("1.8", "xm")
					.withMapping("15w31[abc]", "qa")
					.endClass()
					.startClass("net.minecraft.block.Block")
					.withMapping("1.7.10", "aji")
					.withMapping("1.8", "atr")
					.withMapping("15w31[ab]", "agj")
					.withMapping("15w31c", "agk")
					.startMethod("addCollisionBoxesToList")
					.withMapping("1.7.10", "a")
					.withMapping("1.8", "a")
					.withMapping("15w31[abc]", "a")
					.withDesc("1.7.10",
							"(Lnet.minecraft.world.World;IIILnet.minecraft.util.AxisAlignedBB;"
									+ "Ljava/util/List;Lnet.minecraft.entity.Entity;)V")
					.withDesc("(1.8|15w31[abc])",
							"(Lnet.minecraft.world.World;Lnet.minecraft.util.BlockPos;"
									+ "Lnet.minecraft.block.state.IBlockState;"
									+ "Lnet.minecraft.util.AxisAlignedBB;"
									+ "Ljava/util/List;Lnet.minecraft.entity.Entity;)V")
					.endMethod()
					.endClass()
					.startClass("net.minecraft.block.BlockLeavesBase")
					.withMapping("1.7.10", "aod")
					.withMapping("1.8", "bbo")
					.withMapping("15w31[ab]", "alq")
					.withMapping("15w31c", "alr")
					.endClass()
					.addTweak(new PreventBoatBreak())
					.addTweak(new NonSolidLeaves())
					.build();

	private static String discoverVersion() {
		InputStream stream = Launch.classLoader
				.getResourceAsStream("net/minecraft/server/MinecraftServer.class");
		ClassNode serverClass = ASMUtils.readClass(stream);
		VersionScannerVisitor versionScanner = new VersionScannerVisitor();
		Iterator<MethodNode> methodIter = serverClass.methods.iterator();
		while (methodIter.hasNext()) {
			MethodNode method = methodIter.next();
			method.accept(versionScanner);
		}
		if (versionScanner.version == null)
			throw new RuntimeException("could not detect the running version");
		return versionScanner.version;
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (name.startsWith("com.hea3ven.tweaks"))
			return basicClass;

		return asmTweaksManager.handle(name, transformedName, basicClass);
	}

	@Override
	public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
	}

	@Override
	public void injectIntoClassLoader(LaunchClassLoader classLoader) {
		classLoader.registerTransformer(Hea3venTweaks.class.getName());
	}

	@Override
	public String getLaunchTarget() {
		return null;
	}

	@Override
	public String[] getLaunchArguments() {
		return new String[0];
	}

	public static class VersionScannerVisitor extends MethodVisitor {

		public VersionScannerVisitor() {
			super(Opcodes.ASM4);
		}

		public String version = null;

		Pattern normalVer = Pattern.compile("^\\d+\\.\\d+(\\.\\d+)?$");
		Pattern snapVer = Pattern.compile("^\\d\\dw\\d+[a-z]$");

		@Override
		public void visitLdcInsn(Object cst) {
			if (cst instanceof String) {
				String potentialVersion = (String) cst;
				if (normalVer.matcher(potentialVersion).matches())
					setVersion(potentialVersion);
				else if (snapVer.matcher(potentialVersion).matches())
					setVersion(potentialVersion);
			}
			super.visitLdcInsn(cst);
		}

		private void setVersion(String potentialVersion) {
			if (version != null && !version.equals(potentialVersion))
				throw new RuntimeException("could not detect running version, multiple matches");
			version = potentialVersion;
		}
	}
}
