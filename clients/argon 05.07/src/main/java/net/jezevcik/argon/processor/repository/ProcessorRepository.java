package net.jezevcik.argon.processor.repository;

import net.jezevcik.argon.module.Module;
import net.jezevcik.argon.module.params.ModuleCategory;
import net.jezevcik.argon.processor.Processor;
import net.jezevcik.argon.repository.ElementRepository;
import net.jezevcik.argon.repository.params.RepositoryParams;
import net.jezevcik.argon.system.initialize.InitializeStage;

import java.util.List;

public class ProcessorRepository extends ElementRepository<Processor> {

    public ProcessorRepository() {
        super("Processor", RepositoryParams.<Processor>builder()
                .reflectClasses(true)
                .parentType(Processor.class)
                .build());
    }

    @Override
    public void init(InitializeStage stage) {
        super.init(stage);

        if (stage == InitializeStage.POST_MINECRAFT) {
            this.forEach(Processor::init);
        }
    }

}
