package lord.daniel.alexander.storage.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.Modification;
import lord.daniel.alexander.file.abstracts.LocalFile;
import lord.daniel.alexander.file.impl.ModulesFile;
import lord.daniel.alexander.interfaces.IMinecraft;
import lord.daniel.alexander.storage.Storage;

import java.io.File;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class FileStorage extends Storage<LocalFile> implements IMinecraft {

    private final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    private File root;

    @Getter
    @Setter
    private static FileStorage fileStorage;

    @Override
    public void init() {
        this.root = Modification.getModification().getFileDir();
        this.add(new ModulesFile());
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
