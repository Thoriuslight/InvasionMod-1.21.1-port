package invmod.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import invmod.InvasionMod;
import invmod.menu.NexusMenu;
import invmod.net.NexusActionPayload;
import invmod.nexus.Mode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

/** Buttons-only Nexus control panel: shows live state from {@link NexusMenu}
 *  data slots and dispatches {@link NexusActionPayload} on click. */
public class NexusScreen extends AbstractContainerScreen<NexusMenu> {
    private static final ResourceLocation BACKGROUND = InvasionMod.id("textures/gui/nexus.png");

    public NexusScreen(NexusMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, playerInventoryTitle, titleLabelX, titleLabelY, 0x404040, false);
        guiGraphics.drawString(this.font, "Nexus - Level " + menu.getLevel(), 46, 6, 0x404040, false);
        guiGraphics.drawString(this.font, menu.getKills() + " mobs killed", 96, 60, 0x404040, false);
        guiGraphics.drawString(this.font, "R: " + menu.getSpawnRadius(), 142, 72, 0x404040, false);

        if (menu.getMode() == Mode.STARTED || menu.getMode() == Mode.WAITING) {
            guiGraphics.drawString(this.font, "Activated!", 13, 62, 4210752, false);
            guiGraphics.drawString(this.font, "Wave " + menu.getCurrentWave(), 55, 37, 0x404040, false);
        } else if (menu.getMode() == Mode.CONTINUOUS) {
            guiGraphics.drawString(this.font, "Power:", 56, 31, 4210752, false);
            guiGraphics.drawString(this.font, "" + menu.getPowerLevel(), 61, 44, 0x404040, false);
        }

        if (menu.isActivating() && menu.getMode() == Mode.STOPPED) {
            guiGraphics.drawString(this.font, "Activating...", 13, 62, 0x404040, false);
            if (menu.getMode() != Mode.STABLE) {
                guiGraphics.drawString(this.font, "Are you sure?", 8, 72, 0x404040, false);
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int j = (width - imageWidth) / 2;
        int k = (height - imageHeight) / 2;
        guiGraphics.blit(BACKGROUND, j, k, 0, 0, imageWidth, imageHeight);

        int l = menu.getGenerationProgressScaled(26);
        guiGraphics.blit(BACKGROUND, j + 126, k + 28 + 26 - l, 185, 26 - l, 9, l);
        guiGraphics.blit(BACKGROUND, j + 31, k + 51, 204, 0, menu.getCookProgressScaled(18), 2);

        if (menu.getMode() == Mode.STARTED || menu.getMode() == Mode.WAITING) {
            guiGraphics.blit(BACKGROUND, j + 19, k + 29, 176, 0, 9, 31);
            guiGraphics.blit(BACKGROUND, j + 19, k + 19, 194, 0, 9, 9);
        } else if (menu.getMode() == Mode.CONTINUOUS) {
            guiGraphics.blit(BACKGROUND, j + 19, k + 29, 176, 31, 9, 31);
        }

        if ((menu.getMode() == Mode.STOPPED || menu.getMode() == Mode.CONTINUOUS) && menu.isActivating()) {
            l = menu.getActivationProgressScaled(31);
            guiGraphics.blit(BACKGROUND, j + 19, k + 29 + 31 - l, 176, 31 - l, 9, l);
        } else if (menu.getMode() == Mode.STABLE && menu.isActivating()) {
            l = menu.getActivationProgressScaled(31);
            guiGraphics.blit(BACKGROUND, j + 19, k + 29 + 31 - l, 176, 62 - l, 9, l);
        }
    }
}