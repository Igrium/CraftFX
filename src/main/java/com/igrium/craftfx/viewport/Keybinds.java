package com.igrium.craftfx.viewport;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import javafx.scene.input.KeyCode;
import net.minecraft.util.Identifier;

public final class Keybinds {
    private Keybinds() {};

    public static final Identifier FORWARD = new Identifier("craftfx", "forward");
    public static final Identifier BACK = new Identifier("craftfx", "back");
    public static final Identifier LEFT = new Identifier("craftfx", "left");
    public static final Identifier RIGHT = new Identifier("craftfx", "right");
    public static final Identifier UP = new Identifier("craftfx", "up");
    public static final Identifier DOWN = new Identifier("craftfx", "down");

    /**
     * A map of keybinds that matches Minecraft's default control scheme.
     */
    public static final Map<KeyCode, Identifier> DEFAULTS = ImmutableMap.of(
            KeyCode.W, FORWARD,
            KeyCode.A, LEFT,
            KeyCode.S, BACK,
            KeyCode.D, RIGHT,
            KeyCode.SPACE, UP,
            KeyCode.SHIFT, DOWN);
}
