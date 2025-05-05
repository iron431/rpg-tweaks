package io.redspace.ironsrpgtweaks.durability_module;

public enum VanillaDurabilityMode {
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
