package com.atani.nextgen.module.impl.combat;

import com.atani.nextgen.event.impl.EntityHitboxEvent;
import com.atani.nextgen.module.ModuleCategory;
import com.atani.nextgen.module.ModuleFeature;
import com.atani.nextgen.setting.SettingFeature;
import com.atani.nextgen.setting.builder.impl.CheckBuilder;
import com.atani.nextgen.setting.builder.impl.SliderBuilder;
import com.atani.nextgen.util.world.entity.EntityFilterUtil;
import io.github.racoondog.norbit.EventHandler;

public class HitboxExpanderModule extends ModuleFeature {

    public final SettingFeature<Float> hitboxSize = new SliderBuilder("HitboxSize", 0.0f, -1.0f, 3.0f, 1)
            .build();
    public final SettingFeature<Boolean> includeMobs = new CheckBuilder("IncludeMobs", false)
            .build();

    public HitboxExpanderModule() {
        super(new ModuleBuilder("HitboxExpander", "Alter entity hitboxes", ModuleCategory.COMBAT));
    }

    @EventHandler
    public final void onHitbox(EntityHitboxEvent entityHitboxEvent) {
        if(EntityFilterUtil.isBadEntity(entityHitboxEvent.entity, includeMobs.getValue())) {
            entityHitboxEvent.cancelled = true;
        } else {
            entityHitboxEvent.size = hitboxSize.getValue();
        }
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

}
