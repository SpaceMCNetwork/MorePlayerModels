/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.world.entity.player.Player
 */
package noppes.mpm.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;

public class TextBlockClient {
    public List<Component> lines = new ArrayList<Component>();
    private Style style;
    public int color = 0xE0E0E0;
    public String name;

    public TextBlockClient(String name, String text, int lineWidth) {
        this(text, lineWidth, (Player)Minecraft.getInstance().player);
        this.name = name;
    }

    public TextBlockClient(String name, String text, int lineWidth, int color) {
        this(name, text, lineWidth);
        this.color = color;
    }

    public TextBlockClient(String text, int lineWidth) {
        this(text, lineWidth, (Player)Minecraft.getInstance().player);
    }

    public TextBlockClient(Component text, int lineWidth) {
        this.lines.add(text);
    }

    public TextBlockClient(String text, int lineWidth, Player player) {
        this.style = Style.EMPTY;
        Object line = "";
        text = text.replaceAll("\n", " \n ");
        text = text.replaceAll("\r", " \r ");
        String[] words = text.split(" ");
        Font font = Minecraft.getInstance().font;
        for (String word : words) {
            char c;
            if (word.isEmpty()) continue;
            if (word.length() == 1 && ((c = word.charAt(0)) == '\r' || c == '\n')) {
                this.addLine((String)line);
                line = "";
                continue;
            }
            Object newLine = ((String)line).isEmpty() ? word : (String)line + " " + word;
            if (font.width((String)newLine) > lineWidth) {
                this.addLine((String)line);
                line = word.trim();
                continue;
            }
            line = newLine;
        }
        if (!((String)line).isEmpty()) {
            this.addLine((String)line);
        }
    }

    private void addLine(String text) {
        MutableComponent line = Component.translatable((String)text);
        line.setStyle(this.style);
        this.lines.add((Component)line);
    }
}

