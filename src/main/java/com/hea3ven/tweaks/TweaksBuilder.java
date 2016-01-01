package com.hea3ven.tweaks;

import net.minecraft.launchwrapper.IClassTransformer;

import com.hea3ven.tools.asmtweaks.ASMTweaksManagerBuilder;

public class TweaksBuilder {

	public static IClassTransformer build() {
		return new ASMTweaksManagerBuilder().loadMappings("/mappings")

				.addFldSrg("net/minecraft/world/World/worldInfo", "net/minecraft/world/World/field_72986_A")
				.addFldSrg("net/minecraft/entity/Entity/isCollidedHorizontally",
						"net/minecraft/entity/Entity/field_70123_F")
				.addMthdSrg("net/minecraft/world/storage/WorldInfo/getWorldTime",
						"net/minecraft/world/storage/WorldInfo/func_76073_f", "()J")
				.addMthdSrg("net/minecraft/world/storage/WorldInfo/setWorldTime",
						"net/minecraft/world/storage/WorldInfo/func_76068_b", "(J)V")
				.addMthdSrg("net/minecraft/world/WorldServer/tick",
						"net/minecraft/world/WorldServer/func_72835_b", "()V")
				.addMthdSrg("net/minecraft/entity/item/EntityBoat/onUpdate",
						"net/minecraft/entity/item/EntityBoat/func_70071_h_", "()V")
				.addMthdSrg("net/minecraft/world/WorldProvider/calculateCelestialAngle",
						"net/minecraft/world/WorldProvider/func_76563_a", "(JF)F")
				.addMthdSrg("net/minecraft/world/World/getWorldTime",
						"net/minecraft/world/World/func_72820_D", "()J")
				.addMthdSrg("net/minecraft/client/multiplayer/WorldClient/tick",
						"net/minecraft/client/multiplayer/WorldClient/func_72835_b", "()V")
				.addMthdSrg("net/minecraft/entity/Entity/moveEntity",
						"net/minecraft/entity/Entity/func_70091_d", "(DDD)V")
				.addMthdSrg("net/minecraft/block/Block/addCollisionBoxesToList",
						"net/minecraft/block/Block/func_180638_a",
						"(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/entity/Entity;)V")
				.addTweak(new RuntimeObfuscation(new String[] {"com/hea3ven/tweaks/DayNightCycleFunctions"}))
				.addTweak(new PreventBoatBreak())
				.addTweak(new NonSolidLeaves())
				.addTweak(new DayNightCycle())
				.build();
	}
}
