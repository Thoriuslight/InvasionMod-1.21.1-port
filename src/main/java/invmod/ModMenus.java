package invmod;

import invmod.menu.NexusMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModMenus {
    private ModMenus() {}

    public static final DeferredHolder<MenuType<?>, MenuType<NexusMenu>> NEXUS =
            InvasionMod.MENU_TYPES.register("nexus",
                    () -> IMenuTypeExtension.create(NexusMenu::new));

    static void touch() {}
}
