package io.redspace.ironsrpgtweaks.setup;


import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModSetup {

//    public static void setup() {
//        IEventBus bus = MinecraftForge.EVENT_BUS;
//
//    }

    public static void init(FMLCommonSetupEvent event) {
        Messages.register();

//        if (ModList.get().isLoaded("tetra")) {
//            TetraProxy.PROXY = new TetraActualImpl();
//        }

    }


}
