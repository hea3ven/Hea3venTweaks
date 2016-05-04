package com.hea3ven.tweaks;

import net.minecraft.launchwrapper.IClassTransformer;

import com.hea3ven.tools.asmtweaks.ASMTweaksBuilder;

public class TweaksBuilder {

	public static IClassTransformer build() {
		return ASMTweaksBuilder.create()
				.loadMappings("/mappings/%s.mappings")
				.addFldSrg("net/minecraft/world/World/worldInfo", "net/minecraft/world/World/field_72986_A")
				.addMthdSrg("net/minecraft/world/storage/WorldInfo/getWorldTime",
						"net/minecraft/world/storage/WorldInfo/func_76073_f", "()J")
				.addMthdSrg("net/minecraft/world/storage/WorldInfo/setWorldTime",
						"net/minecraft/world/storage/WorldInfo/func_76068_b", "(J)V")
				.addMthdSrg("net/minecraft/world/WorldServer/tick",
						"net/minecraft/world/WorldServer/func_72835_b", "()V")
				.addMthdSrg("net/minecraft/world/WorldProvider/calculateCelestialAngle",
						"net/minecraft/world/WorldProvider/func_76563_a", "(JF)F")
				.addMthdSrg("net/minecraft/world/World/getWorldTime",
						"net/minecraft/world/World/func_72820_D", "()J")
				.addMthdSrg("net/minecraft/client/multiplayer/WorldClient/tick",
						"net/minecraft/client/multiplayer/WorldClient/func_72835_b", "()V")
				.addMthdSrg("net/minecraft/client/multiplayer/WorldClient/setWorldTime",
						"net/minecraft/client/multiplayer/WorldClient/func_72877_b", "(J)V")
				.addMthdSrg("net/minecraft/world/World/getWorldTime",
						"net/minecraft/world/World/func_72820_D", "()J")
				.addMthdSrg("net/minecraft/block/Block/addCollisionBoxToList",
						"net/minecraft/block/Block/func_185477_a",
						"(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/entity/Entity;)V")
				.addTweak(new NonSolidLeaves())
				.addTweak(new DayNightCycle())
				.build();
	}
}
