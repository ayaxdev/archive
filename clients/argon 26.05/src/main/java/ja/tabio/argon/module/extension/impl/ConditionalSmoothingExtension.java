package ja.tabio.argon.module.extension.impl;

import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.extension.Extension;
import ja.tabio.argon.setting.Setting;
import ja.tabio.argon.setting.impl.ModeSetting;
import ja.tabio.argon.setting.impl.NumberSetting;
import ja.tabio.argon.utils.jvm.ObjectUtils;
import ja.tabio.argon.utils.math.random.Randomization;

import java.util.List;

public class ConditionalSmoothingExtension extends Extension {

    public final NumberSetting coefDistance, coefDiffH, coefDiffV, coefCrosshairH, coefCrosshairV, interceptH, interceptV, minimumTurnSpeedH, minimumTurnSpeedV;

    public ConditionalSmoothingExtension(String name, Module parent) {
        super(name, parent, false);

        coefDistance = new NumberSetting("CoefDistance", -0.666f, -2f, 2f, 3);
        coefDiffH = new NumberSetting("CoefDiffH", 0.4f, -1f, 1f, 2);
        coefDiffV = new NumberSetting("CoefDiffV", 0.16f, -1f, 1f, 2);
        coefCrosshairH = new NumberSetting("CoefCrosshairH", -1.76f, -30f, 30f, 2);
        coefCrosshairV = new NumberSetting("CoefCrosshairV", -5.45f, -30f, 30f, 2);
        interceptH = new NumberSetting("InterceptH", 6.66f, 0f, 20f, 3);
        interceptV = new NumberSetting("InterceptV", 7.33f, 0f, 10f, 3);
        minimumTurnSpeedH = new NumberSetting("MinimumTurnSpeedH", 0.01f, 0f, 1f, 3);
        minimumTurnSpeedV = new NumberSetting("MinimumTurnSpeedV", 0.01f, 0f, 1f, 3);
    }

    @Override
    public List<Setting<?>> add() {
        return List.of(coefDistance, coefDiffH, coefDiffV, coefCrosshairH, coefCrosshairV, interceptH, interceptV, minimumTurnSpeedH, minimumTurnSpeedV);
    }

    public float getCoefDistance() {
        return coefDistance.getValue();
    }

    public float[] getCoefDiff() {
        return new float[] {coefDiffH.getValue(), coefDiffV.getValue()};
    }

    public float[] getCoefCrosshair() {
        return new float[] {coefCrosshairH.getValue(), coefCrosshairV.getValue()};
    }

    public float[] getIntercept() {
        return new float[] {interceptH.getValue(), interceptV.getValue()};
    }

    public float[] getMinimumTurnSpeed() {
        return new float[] {minimumTurnSpeedH.getValue(), minimumTurnSpeedV.getValue()};
    }

}
