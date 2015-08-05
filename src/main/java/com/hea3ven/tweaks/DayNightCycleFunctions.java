package com.hea3ven.tweaks;

import net.minecraft.launchwrapper.LogWrapper;
import net.minecraft.world.storage.WorldInfo;

public class DayNightCycleFunctions {

	static {
		LogWrapper.info("DayNightCycleFunctions");
	}

	public static void addTick(WorldInfo worldInfo) {
		worldInfo.setWorldTime(worldInfo.getWorldTime() + 1);
	}

	public static long getWorldTime(WorldInfo worldInfo) {
		return (long) Math.floor(worldInfo.getWorldTime() * DayNightCycle.dayLengthMultiplier);
	}

	public static void setWorldTime(WorldInfo worldInfo, long time) {
		worldInfo.setWorldTime((long) Math.floor(time / DayNightCycle.dayLengthMultiplier));
	}

	public static float calculateCelestialAngle(long time, float off) {
		int actualTime = (int) (time % 24000L);
		float timeRatio = ((actualTime + off) / 24000.0F);
		if (timeRatio <= (DayNightCycle.dayToNightRatio / 2.0f)) {
			timeRatio /= DayNightCycle.dayToNightRatio;
		} else {
			timeRatio = (timeRatio - (DayNightCycle.dayToNightRatio / 2.0f))
					/ (2.0f - DayNightCycle.dayToNightRatio) + 0.5f;
		}
		timeRatio -= 0.25F;
		if (timeRatio < 0.0F) {
			++timeRatio;
		}
		if (timeRatio > 1.0F) {
			--timeRatio;
		}

		float f2 = timeRatio;
		timeRatio = 1.0F - (float) ((Math.cos(timeRatio * Math.PI) + 1.0D) / 2.0D);
		timeRatio = f2 + (timeRatio - f2) / 3.0F;
		return timeRatio;
	}
}
