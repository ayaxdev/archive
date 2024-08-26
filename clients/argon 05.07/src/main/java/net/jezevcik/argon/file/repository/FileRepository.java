package net.jezevcik.argon.file.repository;

import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.file.DataFile;
import net.jezevcik.argon.repository.ElementRepository;
import net.jezevcik.argon.repository.params.RepositoryParams;
import net.jezevcik.argon.system.initialize.InitializeStage;

public class FileRepository extends ElementRepository<DataFile> {

    public FileRepository() {
        super("Files", RepositoryParams.<DataFile>builder()
                .parentType(DataFile.class)
                .reflectClasses(true)
                .build());
    }

    @Override
    public void init(InitializeStage initializeStage) {
        super.init(initializeStage);

        if (initializeStage == InitializeStage.POST_MINECRAFT) {
            this.forEach(DataFile::read);
        }
    }

    public void write() {
        this.forEach(dataFile -> {
            try {
                dataFile.write();
            } catch (Exception e) {
                ParekClient.LOGGER.error("Failed to write file {}", dataFile.name, e);
            }
        });
    }

}
