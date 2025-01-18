package io.redspace.durability_module;

public enum DurabilityMode {
    ALL,
    TOOLS,
    ARMOR;

    public boolean shouldDamageArmor() {
        return this == ALL || this == ARMOR;
    }

    public boolean shouldDamageTools() {
        return this == ALL || this == TOOLS;
    }
}
