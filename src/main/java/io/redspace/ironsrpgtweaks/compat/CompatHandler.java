package io.redspace.ironsrpgtweaks.compat;

import io.redspace.ironsrpgtweaks.compat.farmers_delight.FarmersDelightProxy;
import io.redspace.ironsrpgtweaks.compat.farmers_delight.FarmersDelightProxyImpl;
import net.neoforged.fml.ModList;

import java.util.Map;

public class CompatHandler {
    public static FarmersDelightProxy FARMERS_DELIGHT_PROXY = new FarmersDelightProxy() {
    };

    private static final Map<String, Runnable> MOD_MAP = Map.of(
            "farmersdelight", () -> FARMERS_DELIGHT_PROXY = new FarmersDelightProxyImpl()
    );

    public static void init() {
        MOD_MAP.forEach((modid, supplier) -> {
            if (ModList.get().isLoaded(modid)) {
                supplier.run();
            }
        });
    }
}
