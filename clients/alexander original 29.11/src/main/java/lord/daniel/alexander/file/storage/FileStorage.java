package lord.daniel.alexander.file.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.Modification;
import lord.daniel.alexander.file.abstracts.LocalFile;
import lord.daniel.alexander.file.impl.AltsFile;
import lord.daniel.alexander.file.impl.DraggablesFile;
import lord.daniel.alexander.file.impl.ModulesFile;
import lord.daniel.alexander.interfaces.Methods;
import lord.daniel.alexander.storage.Storage;

import java.io.File;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class FileStorage extends Storage<LocalFile> implements Methods {

    private final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    private File root;

    @Getter
    @Setter
    private static FileStorage fileStorage;

    @Override
    public void init() {
        this.root = Modification.INSTANCE.getFileDir();
        this.add(new AltsFile());
        this.add(new ModulesFile());
        this.add(new DraggablesFile());
        this.load();
    }

    public void add(LocalFile item) {
        item.setFile(root);
        super.add(item);
    }

    public void save() {
        this.getList().forEach(file -> file.save(GSON));
    }

    public void load() {
        this.getList().forEach(file -> file.load(GSON));
    }

}
