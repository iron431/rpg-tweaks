package io.redspace.ironsrpgtweaks.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Boolean> combatDebugMode;

    static {
        BUILDER.comment("Combat Debug Mode will show the id of damage sources and entities. Useful for getting the ids of other mods for the combat module's blacklist");
        combatDebugMode = BUILDER.define("combatDebugMode", false);

        SPEC = BUILDER.build();
    }
}
