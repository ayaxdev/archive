package net.jezevcik.argon.module.impl.flight;

import net.jezevcik.argon.module.choice.Choice;
import net.jezevcik.argon.module.choice.ChoiceModule;
import net.jezevcik.argon.module.impl.flight.antiCheat.CubeCraftFlight;
import net.jezevcik.argon.module.impl.flight.antiCheat.GenericFlight;
import net.jezevcik.argon.module.params.ModuleCategory;
import net.jezevcik.argon.module.params.ModuleParams;

public class FlightModule extends ChoiceModule {

    public FlightModule() {
        super("AntiCheat", ModuleParams.builder()
                .name("Flight")
                .category(ModuleCategory.MOVEMENT)
                .build());
    }

    private String[] names;

    private Choice[] choices;

    @Override
    public String[] getChoiceNames() {
        if (names == null)
            names = new String[] {
                    "Generic", "CubeCraft"
            };

        return names;
    }

    @Override
    public Choice[] getChoiceObjects() {
        if (choices == null)
            choices = new Choice[] {
                    new GenericFlight("Generic", this),
                    new CubeCraftFlight("CubeCraft", this)
            };

        return choices;
    }
}
