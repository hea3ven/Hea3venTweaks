package com.hea3ven.tweaks.asmtweaks;

import java.util.Set;

public interface ASMTweak {

	String getName();
	
	Set<ASMMod> getModifications();

}
