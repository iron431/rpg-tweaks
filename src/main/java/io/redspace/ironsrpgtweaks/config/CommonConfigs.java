package io.redspace.ironsrpgtweaks.config;

import io.redspace.ironsrpgtweaks.damage_module.DamageEventHandler;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class CommonConfigs {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> DAMAGE_MODULE_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DAMAGE_MODULE_ENTITY_BLACKLIST;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DAMAGE_MODULE_DAMAGE_SOURCE_BLACKLIST;

    static {

        BUILDER.push("Damage-Module");
        BUILDER.comment("The purpose of the damage module is to remove the invulnerability ticks after an entity is damaged to better suit gameplay where entities are going to be ignoring too much damage if left unchecked.");
        DAMAGE_MODULE_ENABLED = BUILDER.define("enabled", true);
        BUILDER.comment("Some entities or damage sources rely on damage ticks to time their attacks. In these cases, we want to let them initiate i-frames.");
        BUILDER.comment("entity-blacklist default: " + getDefaultEntries(DamageEventHandler.BLACKLIST_ENTITY_TYPES));
        DAMAGE_MODULE_ENTITY_BLACKLIST = BUILDER.defineList("entity-blacklist", DamageEventHandler.BLACKLIST_ENTITY_TYPES, (x) -> true);
        BUILDER.comment("damagesource-blacklist default: " + getDefaultEntries(DamageEventHandler.BLACKLIST_DAMAGE_SOURCES));
        DAMAGE_MODULE_DAMAGE_SOURCE_BLACKLIST = BUILDER.defineList("damagesource-blacklist", DamageEventHandler.BLACKLIST_DAMAGE_SOURCES, (x) -> true);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    private static String getDefaultEntries(List<? extends String> list) {
        String str = "[";
        for (String entry : list)
            str += "\"" + entry + "\",";
        str = str.substring(0, str.length() - 1);
        return str + "]";
    }
}
