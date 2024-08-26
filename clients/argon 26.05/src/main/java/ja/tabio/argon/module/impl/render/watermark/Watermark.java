package ja.tabio.argon.module.impl.render.watermark;

import ja.tabio.argon.module.annotation.RegisterModule;
import ja.tabio.argon.module.enums.ModuleCategory;
import ja.tabio.argon.module.extension.Extension;
import ja.tabio.argon.module.impl.render.watermark.modes.ArgonWatermark;
import ja.tabio.argon.module.impl.render.watermark.modes.FatalityWatermark;
import ja.tabio.argon.module.impl.render.watermark.modes.IcarusWatermark;
import ja.tabio.argon.module.mode.ModeModule;

@RegisterModule
public class Watermark extends ModeModule {

    public final ArgonWatermark argonWatermark = new ArgonWatermark("Argon", this)
            .visibility(mode, "Argon");

    public final FatalityWatermark fatalityWatermark = new FatalityWatermark("Fatality", this)
            .visibility(mode, "Fatality");

    public final IcarusWatermark icarusWatermark = new IcarusWatermark("Icarus", this)
            .visibility(mode, "Icarus");

    public Watermark() {
        super(ModuleParams.builder()
                .name("Watermark")
                .category(ModuleCategory.RENDER)
                .enabled(true)
                .build());
    }


    @Override
    public String[] getModeNames() {
        return new String[] {"Argon", "Fatality", "Icarus"};
    }

    @Override
    public Extension[] getModes() {
        return new Extension[] {argonWatermark, fatalityWatermark, icarusWatermark};
    }
}
