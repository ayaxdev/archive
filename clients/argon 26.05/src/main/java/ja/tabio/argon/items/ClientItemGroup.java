package ja.tabio.argon.items;

import ja.tabio.argon.Argon;
import ja.tabio.argon.interfaces.Identifiable;
import ja.tabio.argon.interfaces.Nameable;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class ClientItemGroup implements Nameable, Identifiable {

    public final String name;

    public ClientItemGroup(final String name) {
        this.name = name;
    }

    public void init() {
        ItemGroup itemGroup = FabricItemGroup.builder()
                .displayName(Text.literal(name))
                .entries((displayContext, entries) -> {
                    try {
                        addEntries(entries);
                    } catch (Exception e) {
                        Argon.getInstance().logger.error("Failed to add entries", e);
                    }
                })
                .icon(this::getIcon).build();

        // Add tab to creative inventory
        Registry.register(Registries.ITEM_GROUP, new Identifier(Argon.MOD_ID, name.toLowerCase()), itemGroup);
    }

    protected abstract ItemStack getIcon();

    protected abstract void addEntries(ItemGroup.Entries entries);

    @Override
    public String getUniqueIdentifier() {
        return String.format("ItemGroup-%s", getName());
    }

    @Override
    public String getName() {
        return name;
    }
}
