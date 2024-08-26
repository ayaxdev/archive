package ja.tabio.argon.module.impl.movement.fly;

import ja.tabio.argon.module.annotation.RegisterModule;
import ja.tabio.argon.module.enums.ModuleCategory;
import ja.tabio.argon.module.extension.Extension;
import ja.tabio.argon.module.mode.ModeModule;
import ja.tabio.argon.module.impl.movement.fly.modes.AbilitiesFly;
import ja.tabio.argon.module.impl.movement.fly.modes.VelocityFly;
import ja.tabio.argon.module.impl.movement.fly.modes.VulcanFly;

@RegisterModule
public class Fly extends ModeModule {

    public final AbilitiesFly abilitiesFly = new AbilitiesFly("Abilities", this)
            .visibility(mode, "Abilities");
    public final VulcanFly vulcanFly = new VulcanFly("Vulcan", this)
            .visibility(mode, "Vulcan");
    public final VelocityFly velocityFly = new VelocityFly("Velocity", this)
            .visibility(mode, "Velocity");

    public Fly() {
        super(ModuleParams.builder()
                .name("Fly")
                .category(ModuleCategory.MOVEMENT)
                .build());
    }

    @Override
    public String[] getModeNames() {
        return new String[] {"Abilities", "Vulcan", "Velocity"};
    }

    @Override
    public Extension[] getModes() {
        return new Extension[] {abilitiesFly, vulcanFly, velocityFly};
    }

}
