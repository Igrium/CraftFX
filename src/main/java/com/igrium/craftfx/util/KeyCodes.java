package com.igrium.craftfx.util;

import static javafx.scene.input.KeyCode.*;
import static org.lwjgl.glfw.GLFW.*;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import javafx.scene.input.KeyCode;

/**
 * JavaFX and GLFW (and by extension Minecraft) have different key code maps.
 * This class contains various functions to convert between the two.
 */
public class KeyCodes {
    private static final BiMap<KeyCode, Integer> keyMap = HashBiMap.create();

    /**
     * Convert a JavaFX key code to a GLFW key code.
     * @param javafx JavaFX code.
     * @return GLFW code, or {@code -1} if there is no GLFW equivalent.
     */
    public static int toGlfw(KeyCode javafx) {
        Integer val = keyMap.get(javafx);
        if (val == null) return GLFW_KEY_UNKNOWN;
        return val;
    }

    /**
     * Convert a GLFW key code to a JavaFX key code.
     * @param glfw GLFW code.
     * @return JavaFX code, or {@code null} if there is no JavaFX equivalent.
     */
    @Nullable
    public static KeyCode toJFX(int glfw) {
        return keyMap.inverse().get(glfw);
    }

    private static void add(KeyCode javafx, int glfw) {
        keyMap.put(javafx, glfw);
    }

    static {
        add(ENTER, GLFW_KEY_ENTER);
        add(BACK_SPACE, GLFW_KEY_BACKSPACE);
        add(SHIFT, GLFW_KEY_LEFT_SHIFT);
        add(CONTROL, GLFW_KEY_LEFT_CONTROL);
        add(ALT, GLFW_KEY_LEFT_ALT);
        add(PAUSE, GLFW_KEY_PAUSE);
        add(CAPS, GLFW_KEY_CAPS_LOCK);
        add(ESCAPE, GLFW_KEY_ESCAPE);
        add(SPACE, GLFW_KEY_SPACE);
        add(PAGE_UP, GLFW_KEY_PAGE_UP);
        add(PAGE_DOWN, GLFW_KEY_PAGE_DOWN);
        add(END, GLFW_KEY_END);
        add(HOME, GLFW_KEY_HOME);
        add(LEFT, GLFW_KEY_LEFT);
        add(UP, GLFW_KEY_UP);
        add(RIGHT, GLFW_KEY_RIGHT);
        add(DOWN, GLFW_KEY_DOWN);
        add(COMMA, GLFW_KEY_COMMA);
        add(MINUS, GLFW_KEY_MINUS);
        add(PERIOD, GLFW_KEY_PERIOD);
        add(SLASH, GLFW_KEY_SLASH);
        add(DIGIT0, GLFW_KEY_0);
        add(DIGIT1, GLFW_KEY_1);
        add(DIGIT2, GLFW_KEY_2);
        add(DIGIT3, GLFW_KEY_3);
        add(DIGIT4, GLFW_KEY_4);
        add(DIGIT5, GLFW_KEY_5);
        add(DIGIT6, GLFW_KEY_6);
        add(DIGIT7, GLFW_KEY_7);
        add(DIGIT8, GLFW_KEY_8);
        add(DIGIT9, GLFW_KEY_9);
        add(SEMICOLON, GLFW_KEY_SEMICOLON);
        add(EQUALS, GLFW_KEY_EQUAL);
        add(A, GLFW_KEY_A);
        add(B, GLFW_KEY_B);
        add(C, GLFW_KEY_C);
        add(D, GLFW_KEY_D);
        add(E, GLFW_KEY_E);
        add(F, GLFW_KEY_F);
        add(G, GLFW_KEY_G);
        add(H, GLFW_KEY_H);
        add(I, GLFW_KEY_I);
        add(J, GLFW_KEY_J);
        add(K, GLFW_KEY_K);
        add(L, GLFW_KEY_L);
        add(M, GLFW_KEY_M);
        add(N, GLFW_KEY_N);
        add(O, GLFW_KEY_O);
        add(P, GLFW_KEY_P);
        add(Q, GLFW_KEY_Q);
        add(R, GLFW_KEY_R);
        add(S, GLFW_KEY_S);
        add(T, GLFW_KEY_T);
        add(U, GLFW_KEY_U);
        add(V, GLFW_KEY_V);
        add(W, GLFW_KEY_W);
        add(X, GLFW_KEY_X);
        add(Y, GLFW_KEY_Y);
        add(Z, GLFW_KEY_Z);
        add(OPEN_BRACKET, GLFW_KEY_LEFT_BRACKET);
        add(CLOSE_BRACKET, GLFW_KEY_RIGHT_BRACKET);
        add(BACK_SLASH, GLFW_KEY_BACKSLASH);
        add(NUMPAD0, GLFW_KEY_KP_0);
        add(NUMPAD1, GLFW_KEY_KP_1);
        add(NUMPAD2, GLFW_KEY_KP_2);
        add(NUMPAD3, GLFW_KEY_KP_3);
        add(NUMPAD4, GLFW_KEY_KP_4);
        add(NUMPAD5, GLFW_KEY_KP_5);
        add(NUMPAD6, GLFW_KEY_KP_6);
        add(NUMPAD7, GLFW_KEY_KP_7);
        add(NUMPAD8, GLFW_KEY_KP_8);
        add(NUMPAD9, GLFW_KEY_KP_9);
        add(ADD, GLFW_KEY_KP_ADD);
        add(SUBTRACT, GLFW_KEY_KP_SUBTRACT);
        add(DECIMAL, GLFW_KEY_KP_DECIMAL);
        add(DIVIDE, GLFW_KEY_KP_DIVIDE);
        add(SEPARATOR, GLFW_KEY_KP_MULTIPLY); // I *think* this is right?
        add(DELETE, GLFW_KEY_DELETE);
        add(NUM_LOCK, GLFW_KEY_NUM_LOCK);
        add(SCROLL_LOCK, GLFW_KEY_SCROLL_LOCK);
        add(F1, GLFW_KEY_F1);
        add(F2, GLFW_KEY_F2);
        add(F3, GLFW_KEY_F3);
        add(F4, GLFW_KEY_F4);
        add(F5, GLFW_KEY_F5);
        add(F6, GLFW_KEY_F6);
        add(F7, GLFW_KEY_F7);
        add(F8, GLFW_KEY_F8);
        add(F9, GLFW_KEY_F9);
        add(F10, GLFW_KEY_F10);
        add(F11, GLFW_KEY_F11);
        add(F12, GLFW_KEY_F12);
        add(F13, GLFW_KEY_F13);
        add(F14, GLFW_KEY_F14);
        add(F15, GLFW_KEY_F15);
        add(F16, GLFW_KEY_F16);
        add(F17, GLFW_KEY_F17);
        add(F18, GLFW_KEY_F18);
        add(F19, GLFW_KEY_F19);
        add(F20, GLFW_KEY_F20);
        add(F21, GLFW_KEY_F21);
        add(F22, GLFW_KEY_F22);
        add(F23, GLFW_KEY_F23);
        add(F24, GLFW_KEY_F24);
        add(PRINTSCREEN, GLFW_KEY_PRINT_SCREEN);
        add(INSERT, GLFW_KEY_INSERT);
        add(WINDOWS, 343);
        add(QUOTE, GLFW_KEY_APOSTROPHE);
        
    }
}
