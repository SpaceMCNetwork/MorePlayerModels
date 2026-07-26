/*
 * Decompiled with CFR 0.152.
 */
package noppes.mpm.client.parts;

import noppes.mpm.client.parts.MpmPart;
import noppes.mpm.constants.EnumAnimation;

public interface IMpmPartDataClient<T extends MpmPart> {
    public void start(T var1);

    public boolean animation(T var1, EnumAnimation var2, int var3, float var4);

    public boolean animation(T var1, EnumAnimation var2, float var3);
}

