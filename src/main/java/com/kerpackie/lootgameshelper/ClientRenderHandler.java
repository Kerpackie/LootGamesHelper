package com.kerpackie.lootgameshelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.minigame.gol.Symbol;

public class ClientRenderHandler {

    private final Minecraft mc = Minecraft.getMinecraft();

    /**
     * Listen for mouse clicks to advance the GoL sequence tracker.
     */
    @SubscribeEvent
    public void onMouseClick(MouseEvent event) {
        // We only care about left (0) or right (1) clicks that are presses (event.buttonstate is true)
        if ((event.button == 0 || event.button == 1) && event.buttonstate) {
            // If the GoL overlay is active, any click will advance the step.
            if (ClientCache.isOverlayEnabled && ClientCache.golSequence != null) {
                ClientCache.advanceGoLStep();
            }
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        if (!ClientCache.isOverlayEnabled) return;

        ClientCache.clearStaleData();

        if (ClientCache.golSequence != null) {
            StringBuilder sequenceText = new StringBuilder("§eGoL Sequence: §f");
            for (int i = 0; i < ClientCache.golSequence.length; i++) {
                int index = ClientCache.golSequence[i];
                if (index >= 0 && index < Symbol.values().length) {
                    Symbol symbol = Symbol.values()[index];
                    // Highlight the current step in yellow
                    if (i == ClientCache.currentGoLStep) {
                        sequenceText.append("§e§l")
                            .append(symbol.name())
                            .append("§r§f ");
                    } else {
                        sequenceText.append(symbol.name())
                            .append(" ");
                    }
                }
            }
            mc.fontRenderer.drawStringWithShadow(
                sequenceText.toString()
                    .trim(),
                5,
                5,
                0xFFFFFF);
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!ClientCache.isOverlayEnabled) return;

        ClientCache.clearStaleData();

        renderGoLHighlight(event);
        renderMinesweeperHighlight(event);
    }

    private void renderGoLHighlight(RenderWorldLastEvent event) {
        if (ClientCache.golSequence != null && ClientCache.currentGoLStep < ClientCache.golSequence.length) {
            int symbolIndex = ClientCache.golSequence[ClientCache.currentGoLStep];
            if (symbolIndex < 0 || symbolIndex >= Symbol.values().length) return;

            Symbol currentSymbol = Symbol.values()[symbolIndex];
            Pos2i pos = currentSymbol.getPos();

            // The board is always 3x3, master is at corner, board starts at +1,+1
            double blockX = ClientCache.golX + 1 + pos.getX();
            double blockY = ClientCache.golY;
            double blockZ = ClientCache.golZ + 1 + pos.getY();

            drawWorldHighlight(event, blockX, blockY, blockZ, 0.0F, 1.0F, 1.0F, 0.45F); // Cyan highlight
        }
    }

    private void renderMinesweeperHighlight(RenderWorldLastEvent event) {
        if (ClientCache.msBoard != null) {
            int offset = ClientCache.msAllocatedSize - ClientCache.msSize;
            double originX = ClientCache.msX + 1 + (offset / 2.0);
            double originY = ClientCache.msY;
            double originZ = ClientCache.msZ + 1 + (offset / 2.0);

            for (int x = 0; x < ClientCache.msSize; x++) {
                for (int y = 0; y < ClientCache.msSize; y++) {
                    if (ClientCache.msBoard[x][y] == 0) { // Check for Bomb ID
                        double blockX = originX + x;
                        double blockY = originY;
                        double blockZ = originZ + y;
                        drawWorldHighlight(event, blockX, blockY, blockZ, 1.0F, 0.0F, 0.0F, 0.45F); // Red highlight
                    }
                }
            }
        }
    }

    private void drawWorldHighlight(RenderWorldLastEvent event, double blockX, double blockY, double blockZ, float r,
        float g, float b, float a) {
        double playerX = mc.thePlayer.lastTickPosX
            + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * event.partialTicks;
        double playerY = mc.thePlayer.lastTickPosY
            + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * event.partialTicks;
        double playerZ = mc.thePlayer.lastTickPosZ
            + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * event.partialTicks;

        GL11.glPushMatrix();
        GL11.glTranslated(-playerX, -playerY, -playerZ);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        AxisAlignedBB aabb = AxisAlignedBB
            .getBoundingBox(blockX, blockY + 1, blockZ, blockX + 1, blockY + 1.002, blockZ + 1);
        drawFilledTopFace(aabb, r, g, b, a);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    private void drawFilledTopFace(AxisAlignedBB box, float r, float g, float b, float a) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(r, g, b, a);
        tessellator.addVertex(box.minX, box.maxY, box.minZ);
        tessellator.addVertex(box.minX, box.maxY, box.maxZ);
        tessellator.addVertex(box.maxX, box.maxY, box.maxZ);
        tessellator.addVertex(box.maxX, box.maxY, box.minZ);
        tessellator.draw();
    }
}
