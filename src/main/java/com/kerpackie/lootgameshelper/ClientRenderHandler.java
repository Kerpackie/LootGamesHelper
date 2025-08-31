package com.kerpackie.lootgameshelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ru.timeconqueror.lootgames.minigame.gol.Symbol;

public class ClientRenderHandler {

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        ClientCache.clearStaleData();

        // Render Game of Light sequence on the HUD.
        if (ClientCache.golSequence != null) {
            StringBuilder sequenceText = new StringBuilder("§eGoL Sequence: §f");
            for (int index : ClientCache.golSequence) {
                if (index >= 0 && index < Symbol.values().length) {
                    sequenceText.append(Symbol.values()[index].name())
                        .append(" ");
                }
            }
            mc.fontRenderer.drawStringWithShadow(sequenceText.toString(), 5, 5, 0xFFFFFF);
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        ClientCache.clearStaleData();

        // Render Minesweeper bomb locations in the world.
        if (ClientCache.msBoard != null) {
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

            for (int x = 0; x < ClientCache.msSize; x++) {
                for (int y = 0; y < ClientCache.msSize; y++) {
                    // This is the fix: The server sends the enum's ordinal, and the Bomb's ordinal is 9, not 0.
                    // However, your debug output confirmed the value for a bomb is 0 in your case.
                    // We will trust the debug output and check for 0.
                    if (ClientCache.msBoard[x][y] == 0) { // FIXED: Check for 0 instead of Type.BOMB.getId()
                        double blockX = ClientCache.msX + 1 + x;
                        double blockY = ClientCache.msY;
                        double blockZ = ClientCache.msZ + 1 + y;

                        AxisAlignedBB aabb = AxisAlignedBB
                            .getBoundingBox(blockX, blockY, blockZ, blockX + 1, blockY + 1, blockZ + 1)
                            .expand(0.002, 0.002, 0.002);
                        drawFilledBoundingBox(aabb, 1.0F, 0.0F, 0.0F, 0.35F);
                    }
                }
            }

            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glPopMatrix();
        }
    }

    private void drawFilledBoundingBox(AxisAlignedBB box, float r, float g, float b, float a) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(r, g, b, a);

        tessellator.addVertex(box.minX, box.minY, box.minZ);
        tessellator.addVertex(box.maxX, box.minY, box.minZ);
        tessellator.addVertex(box.maxX, box.minY, box.maxZ);
        tessellator.addVertex(box.minX, box.minY, box.maxZ);

        tessellator.addVertex(box.minX, box.maxY, box.minZ);
        tessellator.addVertex(box.minX, box.maxY, box.maxZ);
        tessellator.addVertex(box.maxX, box.maxY, box.maxZ);
        tessellator.addVertex(box.maxX, box.maxY, box.minZ);

        tessellator.addVertex(box.minX, box.minY, box.minZ);
        tessellator.addVertex(box.minX, box.maxY, box.minZ);
        tessellator.addVertex(box.maxX, box.maxY, box.minZ);
        tessellator.addVertex(box.maxX, box.minY, box.minZ);

        tessellator.addVertex(box.minX, box.minY, box.maxZ);
        tessellator.addVertex(box.maxX, box.minY, box.maxZ);
        tessellator.addVertex(box.maxX, box.maxY, box.maxZ);
        tessellator.addVertex(box.minX, box.maxY, box.maxZ);

        tessellator.addVertex(box.minX, box.minY, box.minZ);
        tessellator.addVertex(box.minX, box.minY, box.maxZ);
        tessellator.addVertex(box.minX, box.maxY, box.maxZ);
        tessellator.addVertex(box.minX, box.maxY, box.minZ);

        tessellator.addVertex(box.maxX, box.minY, box.minZ);
        tessellator.addVertex(box.maxX, box.maxY, box.minZ);
        tessellator.addVertex(box.maxX, box.maxY, box.maxZ);
        tessellator.addVertex(box.maxX, box.minY, box.maxZ);

        tessellator.draw();
    }
}
