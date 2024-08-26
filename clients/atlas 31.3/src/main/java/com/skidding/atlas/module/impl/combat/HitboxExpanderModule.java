package com.skidding.atlas.module.impl.combat;

import com.skidding.atlas.event.impl.player.misc.CollisionBoxEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.util.minecraft.world.entity.EntityFilterUtil;
import io.github.racoondog.norbit.EventHandler;

public class HitboxExpanderModule extends ModuleFeature {

    public final SettingFeature<Float> hitboxSize = slider("Hit-box size", 0.0f, -1.0f, 3.0f, 1).build();
    public final SettingFeature<Boolean> includeMobs = check("Include mobs", false).build();

    public HitboxExpanderModule() {
        super(new ModuleBuilder("HitboxExpander", "Expands the hitboxes of entities", ModuleCategory.COMBAT));
    }

    @EventHandler
    public final void onHitbox(CollisionBoxEvent collisionBoxEvent) {
        if (EntityFilterUtil.isBadEntity(collisionBoxEvent.entity, includeMobs.getValue())) {
            collisionBoxEvent.cancelled = true;
        } else {
            collisionBoxEvent.size = hitboxSize.getValue();
        }
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

}
