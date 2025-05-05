package io.redspace.ironsrpgtweaks.durability_module;

public enum DeathDurabilityMode {
    ALL,
    TOOLS,
    ARMOR,
    NONE;

    public boolean shouldDamageArmor() {
        return this == ALL || this == ARMOR;
    }

    public boolean shouldDamageTools() {
        return this == ALL || this == TOOLS;
    }
}
