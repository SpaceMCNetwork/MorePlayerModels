/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package noppes.mpm.util;

import net.minecraft.resources.ResourceLocation;

public class NoppesStringUtils {
    public static boolean areEqual(String s1, String s2) {
        if (s1 == s2) {
            return true;
        }
        if (s1 == null || s2 == null) {
            return false;
        }
        return s1.equalsIgnoreCase(s2);
    }

    public static boolean areEqual(ResourceLocation s1, ResourceLocation s2) {
        if (s1 == s2) {
            return true;
        }
        if (s1 == null || s2 == null) {
            return false;
        }
        return s1.toString().equalsIgnoreCase(s2.toString());
    }

    public static boolean empty(String s) {
        return s == null || s.trim().isEmpty();
    }
}

