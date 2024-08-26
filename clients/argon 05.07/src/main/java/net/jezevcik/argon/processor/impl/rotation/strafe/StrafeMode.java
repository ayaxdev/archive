package net.jezevcik.argon.processor.impl.rotation.strafe;

import net.jezevcik.argon.processor.impl.rotation.strafe.impl.*;

public enum StrafeMode {
    SIMPLE("Simple", SimpleStrafe.class),
    BREEZILY("Breezily", BreezilyStrafe.class),
    COMBAT("Combat", CombatStrafe.class),
    HYPER_CORRECTED_FIX("HyperCorrected", HyperCorrectedStrafe.class),
    SILENT("Silent", SilentStrafe.class);

    public final String name;
    public final Class<? extends StrafeCorrector> corrector;

    StrafeMode(String name, Class<? extends StrafeCorrector> corrector) {
        this.name = name;
        this.corrector = corrector;
    }

    @Override
    public String toString() {
        return name;
    }
}
