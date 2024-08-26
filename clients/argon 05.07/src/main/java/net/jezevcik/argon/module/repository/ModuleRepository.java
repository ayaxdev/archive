package net.jezevcik.argon.module.repository;

import net.jezevcik.argon.module.Module;
import net.jezevcik.argon.module.choice.ChoiceModule;
import net.jezevcik.argon.module.params.ModuleCategory;
import net.jezevcik.argon.repository.ElementRepository;
import net.jezevcik.argon.repository.params.RepositoryParams;
import net.jezevcik.argon.system.initialize.InitializeStage;

import java.util.List;

public class ModuleRepository extends ElementRepository<Module> {

    public ModuleRepository() {
        super("Modules", RepositoryParams.<Module>builder()
                .reflectClasses(true)
                .parentType(Module.class)
                .build());
    }

    @Override
    public void init(final InitializeStage stage) {
        super.init(stage);

        if (stage == InitializeStage.POST_MINECRAFT) {
            this.forEach(Module::lazyLoad);
        }
    }

    public List<Module> getOfCategory(final ModuleCategory moduleCategory) {
        return this.stream().filter(module -> module.moduleParams.category() == moduleCategory).toList();
    }

    public final Module getByName(final String name) {
        return this.stream().filter(module -> module.moduleParams.name().equalsIgnoreCase(name)).findAny().orElse(null);
    }

}
