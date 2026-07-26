/*
 * Decompiled with CFR 0.152.
 */
package noppes.mpm.client.parts;

import java.util.HashMap;
import java.util.Map;
import noppes.mpm.client.parts.AnimationContainer;
import noppes.mpm.client.parts.IMpmPartDataClient;
import noppes.mpm.client.parts.ModelPartWrapper;
import noppes.mpm.client.parts.MpmPartAbstractClient;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.shared.util.NopVector3f;

public class MpmPartDataClient<T extends MpmPartAbstractClient>
implements IMpmPartDataClient<T> {
    private Map<String, AniWrapper> rotTrans = new HashMap<String, AniWrapper>();

    @Override
    public void start(T partc) {
        for (AniWrapper w : this.rotTrans.values()) {
            w.startupTicks = 0;
        }
    }

    private AniWrapper getAni(ModelPartWrapper m) {
        AniWrapper w = this.rotTrans.get(m.name);
        if (w != null) {
            return w;
        }
        w = new AniWrapper(m.oriRot, m.oriPos);
        this.rotTrans.put(m.name, w);
        return w;
    }

    @Override
    public boolean animation(T partc, EnumAnimation animation, int step, float partialTick) {
        ModelPartWrapper[] models = (ModelPartWrapper[])((MpmPartAbstractClient)partc).animations.get((Object)animation);
        if (models != null) {
            for (ModelPartWrapper m : models) {
                AnimationContainer ac = m.animations.get((Object)animation);
                float f = (float)step / 20.0f * ac.speed;
                int i = (int)f;
                float pf = (float)(step - 1) / 20.0f * ac.speed;
                int pi = (int)pf;
                AniWrapper w = this.getAni(m);
                if (pi != i) {
                    this.step(w, m, ac, i, (f - (float)i) * partialTick);
                } else {
                    this.step(w, m, ac, i, pf - (float)pi + (f - pf) * partialTick);
                }
                if (w.startupTicks >= ac.actualLength || i == pi) continue;
                ++w.startupTicks;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean animation(T partc, EnumAnimation animation, float step) {
        ModelPartWrapper[] models = (ModelPartWrapper[])((MpmPartAbstractClient)partc).animations.get((Object)animation);
        if (models != null) {
            for (ModelPartWrapper m : models) {
                AnimationContainer ac = m.animations.get((Object)animation);
                float f = step * ac.speed * (float)(ac.length - 1);
                int i = (int)f;
                this.step(this.getAni(m), m, ac, i, f - (float)i);
            }
            return true;
        }
        return false;
    }

    private void step(AniWrapper w, ModelPartWrapper part, AnimationContainer ac, int step, float progress) {
        int i = step % ac.actualLength;
        int j = (step + 1) % ac.actualLength;
        if (w.startupTicks < 60) {
            w.rot = ac.hasRotation ? w.rot.lerp(ac.rotations[i].lerp(ac.rotations[j], progress), 0.15f) : w.rot.lerp(part.oriRot, 0.15f);
            w.pos = ac.hasTranslate ? w.pos.lerp(ac.translates[i].lerp(ac.translates[j], progress), 0.15f) : w.pos.lerp(part.oriPos, 0.15f);
        } else {
            if (ac.hasRotation) {
                w.rot = ac.loop && j < i ? ac.rotations[i].subtract(NopVector3f.ROTATION).modulo(NopVector3f.ROTATION).lerp(ac.rotations[j], progress) : ac.rotations[i].lerp(ac.rotations[j], progress);
            }
            if (ac.hasTranslate) {
                w.pos = ac.translates[i].lerp(ac.translates[j], progress);
            }
        }
        part.setPos(w.pos);
        part.setRot(w.rot);
    }

    class AniWrapper {
        int startupTicks = Integer.MAX_VALUE;
        NopVector3f rot;
        NopVector3f pos;

        public AniWrapper(NopVector3f rot, NopVector3f pos) {
            this.rot = rot;
            this.pos = pos;
        }
    }
}

