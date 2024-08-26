package net.jezevcik.argon.module.params;

import net.jezevcik.argon.utils.objects.NullUtils;

public record ModuleParams(String name, String displayName, ModuleCategory category, Integer key,
                           boolean enabledByDefault, boolean alwaysRegistered, boolean allowToggleStateChange) {

    public static ModuleParamsBuilder builder() {
        return new ModuleParamsBuilder();
    }

    public static class ModuleParamsBuilder {

        public String name, displayName;
        public ModuleCategory moduleCategory;
        public Integer key;
        public boolean enabledByDefault = false,
                alwaysRegistered = false,
                allowToggleStateChange = false;

        public ModuleParamsBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ModuleParamsBuilder display(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public ModuleParamsBuilder category(ModuleCategory moduleCategory) {
            this.moduleCategory = moduleCategory;
            return this;
        }

        public ModuleParamsBuilder key(Integer key) {
            this.key = key;
            return this;
        }

        public ModuleParamsBuilder enabledByDefault(boolean enabledByDefault) {
            this.enabledByDefault = enabledByDefault;
            return this;
        }

        public ModuleParamsBuilder alwaysRegistered(boolean alwaysRegistered) {
            this.alwaysRegistered = alwaysRegistered;
            return this;
        }

        public ModuleParamsBuilder allowToggleStateChange(boolean allowToggleStateChange) {
            this.allowToggleStateChange = allowToggleStateChange;
            return this;
        }

        public ModuleParams build() {
            final String name = NullUtils.notNull(this.name, "Name mustn't be null!");
            final String displayName = NullUtils.nullOrElse(this.displayName, this.name);
            final ModuleCategory moduleCategory = NullUtils.notNull(this.moduleCategory, "Category mustn't be null!");

            return new ModuleParams(name, displayName, moduleCategory, key, enabledByDefault, alwaysRegistered, allowToggleStateChange);
        }

    }
}
