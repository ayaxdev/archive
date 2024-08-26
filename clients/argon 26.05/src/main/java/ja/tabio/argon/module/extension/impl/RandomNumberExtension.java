package ja.tabio.argon.module.extension.impl;

import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.extension.Extension;
import ja.tabio.argon.setting.Setting;
import ja.tabio.argon.setting.impl.ModeSetting;
import ja.tabio.argon.setting.impl.NumberSetting;
import ja.tabio.argon.utils.jvm.ObjectUtils;
import ja.tabio.argon.utils.math.random.Randomization;

import java.util.List;

public class RandomNumberExtension extends Extension {

    public NumberSetting minSetting, maxSetting;
    public ModeSetting randomizationAlgorithm;

    public RandomNumberExtension(String name, Module parent, float min, float max, float minValue, float maxValue, int decimals) {
        super(name, parent, false);

        minSetting = new NumberSetting(String.format("Min%s", name), min, minValue, maxValue, decimals);
        maxSetting = new NumberSetting(String.format("Max%s", name), max, minValue, maxValue, decimals);
        randomizationAlgorithm = new ModeSetting(String.format("%sRandomizationAlgorithm", name), "JavaSecure", (Object[]) Randomization.values());
    }

    @Override
    public List<Setting<?>> add() {
        return List.of(minSetting, maxSetting);
    }

    public float getValue() {
        final Randomization randomizationAlgorithm = ObjectUtils.getEnum(Randomization.class, this.randomizationAlgorithm.getValue());
        return randomizationAlgorithm.algorithm.getRandomFloat(minSetting.getValue(), maxSetting.getValue());
    }

}
