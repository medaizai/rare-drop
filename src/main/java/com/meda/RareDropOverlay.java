package com.meda;

import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import java.awt.*;
import net.runelite.api.Client;
import javax.inject.Inject;

public class RareDropOverlay extends Overlay
{
    @Inject private Client client;
    @Inject private RareDropPlugin plugin;
    @Inject private RareDropConfig config;

    @Override
    public Dimension render(Graphics2D g)
    {
        if (!plugin.isActive())
        {
            return null;
        }

        long now = System.currentTimeMillis();
        float progress = plugin.getProgress();

        // ========================
        // SCREEN FLASH
        // ========================

        float flashHue = (now % 1000) / 1000f;

        Color flashBase = Color.getHSBColor(flashHue, 1f, 1f);

        float fade = 1f - progress;

        int flashAlpha = (int)(config.flashIntensity() * fade * fade);

        Color flashColor = new Color(
                flashBase.getRed(),
                flashBase.getGreen(),
                flashBase.getBlue(),
                flashAlpha
        );

        if (config.enableFlash())
        {
            g.setColor(flashColor);
            g.fillRect(0, 0, client.getCanvasWidth(), client.getCanvasHeight());
        }

        // ========================
        // TEXT SETUP
        // ========================

        String rareText = "RARE DROP!";
        String itemText = plugin.getDrop();

        Font font = FontManager.getRunescapeBoldFont().deriveFont(32f);
        g.setFont(font);

        FontMetrics metrics = g.getFontMetrics(font);

        int centerX = client.getCanvasWidth() / 2;
        int baseY = client.getCanvasHeight() / 2;

        // ========================
        // BOUNCE ANIMATION
        // ========================

        float anim;

        if (progress < 0.20f)
        {
            // Shoot up super fast
            float t = progress / 0.18f;
            anim = 80 + (t * 100);
        }
        else if (progress < 0.28f)
        {
            // Drop fast
            float t = progress / 0.18f;
            anim = -80 + (t * 100);
        }
        else if (progress < 0.38f)
        {
            // Bounce slightly upward
            float t = (progress - 0.18f) / 0.10f;
            anim = 20 - (t * 30);
        }
        else if (progress < 0.48f)
        {
            // Settle back into place
            float t = (progress - 0.28f) / 0.10f;
            anim = -10 + (t * 10);
        }
        else
        {
            anim = 0;
        }

        // ========================
        // TEXT COLORS
        // ========================

        float textHue = ((now / 8f) % 360) / 360f;

        Color textBase = Color.getHSBColor(textHue, 1f, 1f);

        int textAlpha = (int)(255 * fade);

        Color textColor = new Color(
                textBase.getRed(),
                textBase.getGreen(),
                textBase.getBlue(),
                textAlpha
        );

        Color shadowColor = new Color(0, 0, 0, textAlpha);

        // ========================
// RARE DROP TEXT (PER-LETTER BOUNCE)
// ========================

        int rareWidth = metrics.stringWidth(rareText);
        int startX = centerX - (rareWidth / 2);

        int currentX = startX;

        for (int i = 0; i < rareText.length(); i++)
        {
            char c = rareText.charAt(i);

            String s = String.valueOf(c);

            int charWidth = metrics.stringWidth(s);

            // Delay each letter slightly
            float letterProgress = progress - (i * 0.035f);

            float letterAnim;

            if (letterProgress < 0f)
            {
                // Not started yet
                letterAnim = -80;
            }
            else if (letterProgress < 0.18f)
            {
                // Drop downward
                float t = letterProgress / 0.18f;
                letterAnim = -80 + (t * 100);
            }
            else if (letterProgress < 0.28f)
            {
                // Bounce upward
                float t = (letterProgress - 0.18f) / 0.10f;
                letterAnim = 20 - (t * 30);
            }
            else if (letterProgress < 0.38f)
            {
                // Settle
                float t = (letterProgress - 0.28f) / 0.10f;
                letterAnim = -10 + (t * 10);
            }
            else
            {
                letterAnim = 0;
            }

            int charY = baseY + (int)letterAnim;

            // Per-letter rainbow
            float charHue = (((now / 8f) + (i * 20)) % 360) / 360f;

            Color charBase = Color.getHSBColor(charHue, 1f, 1f);

            Color charColor = new Color(
                    charBase.getRed(),
                    charBase.getGreen(),
                    charBase.getBlue(),
                    textAlpha
            );

            // Shadow
            g.setColor(shadowColor);
            g.drawString(s, currentX + 2, charY + 2);

            // Main text
            g.setColor(charColor);
            g.drawString(s, currentX, charY);

            currentX += charWidth;
        }

        // ========================
        // ITEM TEXT (STATIC)
        // ========================

        int itemWidth = metrics.stringWidth(itemText);
        int itemX = centerX - (itemWidth / 2);
        int itemY = baseY + 50;

        // Shadow
        g.setColor(shadowColor);
        g.drawString(itemText, itemX + 2, itemY + 2);

        // Main text
        g.setColor(textColor);
        g.drawString(itemText, itemX, itemY);

        return null;
    }
}