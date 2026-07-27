package invmod.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import invmod.menu.NexusMenu;
import invmod.net.NexusActionPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

/** Buttons-only Nexus control panel: shows live state from {@link NexusMenu}
 *  data slots and dispatches {@link NexusActionPayload} on click. */
public class NexusScreen extends AbstractContainerScreen<NexusMenu> {

    public NexusScreen(NexusMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 140;
        this.inventoryLabelY = this.imageHeight - 100;
    }

    @Override
    protected void init() {
        super.init();
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        this.addRenderableWidget(Button.builder(Component.literal("Begin Wave"),
                        b -> sendAction(NexusActionPayload.ACTION_BEGIN))
                .pos(x + 20, y + 50).size(60, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("End"),
                        b -> sendAction(NexusActionPayload.ACTION_END))
                .pos(x + 90, y + 50).size(60, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Cycle Radius"),
                        b -> sendAction(NexusActionPayload.ACTION_RADIUS))
                .pos(x + 35, y + 80).size(100, 20).build());
    }

    private void sendAction(int action) {
        if (this.menu.getNexusPos() == null) return;
        PacketDistributor.sendToServer(new NexusActionPayload(this.menu.getNexusPos(), action));
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        g.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xCC101418);
        g.fill(x, y, x + this.imageWidth, y + 1, 0xFF6B2BFF);
        g.fill(x, y + this.imageHeight - 1, x + this.imageWidth, y + this.imageHeight, 0xFF6B2BFF);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g, mouseX, mouseY, partialTick);
        super.render(g, mouseX, mouseY, partialTick);
        this.renderTooltip(g, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        int color = 0xFFC0C8FF;
        g.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, color, false);
        int y = this.titleLabelY + 14;
        g.drawString(this.font, "Mode: " + modeName(menu.getMode()), 8, y, color, false);
        g.drawString(this.font, "Wave: " + menu.getWaveNumber(), 8, y + 12, color, false);
        g.drawString(this.font, "Mobs: " + menu.getSpawned() + " / " + menu.getTarget(),
                8, y + 24, color, false);
        g.drawString(this.font, "Radius: " + menu.getRadius(), 8, y + 36, color, false);
    }

    private static String modeName(int m) {
        return switch (m) {
            case 0 -> "Idle";
            case 1 -> "Spawning";
            case 2 -> "Await-Clear";
            case 3 -> "Cooldown";
            default -> "?";
        };
    }
}
