package lord.daniel.alexander.settings;

import lombok.Getter;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.bool.ExpandableValue;
import lord.daniel.alexander.settings.impl.mode.MultiSelectValue;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.settings.impl.string.StringValue;
import lord.daniel.alexander.settings.interfaces.IValueChangeListener;
import lord.daniel.alexander.util.array.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@Getter
public abstract class AbstractSetting<T> {

    private final String name;
    private final AbstractModule owner;

    protected List<Supplier<Boolean>> visible = new ArrayList<>();
    private final List<IValueChangeListener<T>> valueChangeListeners = new ArrayList<>();
    private final List<ExpandableValue> expandableParents = new ArrayList<>();

    @Getter
    private T value;

    public AbstractSetting(String name, AbstractModule owner, T value) {
        if(name.contains(" ")) {
            throw new IllegalArgumentException("Setting names cannot have spaces in them! (" + name + ")");
        }

        this.name = name;
        this.value = value;

        this.owner = owner;

        owner.getSettings().add(this);
    }

    public void setValue(T value) {
        for(IValueChangeListener<T> valueChangeListener : valueChangeListeners) {
            valueChangeListener.onValueChange(this, this.value, value);
        }
        this.value = value;
    }

    public boolean isVisible() {
        for(Supplier<Boolean> supplier : visible) {
            if(!supplier.get()) {
                return false;
            }
        }
        return true;
    }

    public abstract void setValueByString(String valueString);

    public abstract String getValueAsString();

    public <O extends AbstractSetting<T>> O addVisibleCondition(MultiSelectValue multiSelectValue, String allowed) {
        return addVisibleCondition(() -> multiSelectValue.getValue().contains(allowed));
    }

    public <O extends AbstractSetting<T>> O addVisibleCondition(StringModeValue stringModeValue, boolean allowedOnly, String... allowed) {
        return addVisibleCondition(() -> allowedOnly == ArrayUtils.contains(allowed, stringModeValue.getValue()));
    }


    public <O extends AbstractSetting<T>> O addVisibleCondition(StringModeValue stringModeValue, String... allowed) {
        return addVisibleCondition(() -> ArrayUtils.contains(allowed, stringModeValue.getValue()));
    }

    public <O extends AbstractSetting<T>> O addVisibleCondition(BooleanValue booleanValue, boolean value) {
        return addVisibleCondition(() -> booleanValue.getValue() == value);
    }

    public <O extends AbstractSetting<T>> O addVisibleCondition(BooleanValue booleanValue) {
        return addVisibleCondition(booleanValue::getValue);
    }

    public <O extends AbstractSetting<T>> O addVisibleCondition(StringValue stringValue, String... allowed) {
        return addVisibleCondition(() -> ArrayUtils.contains(allowed, stringValue.getValue()));
    }

    public <O extends AbstractSetting<T>> O addVisibleCondition(Supplier<Boolean> visible) {
        this.visible.add(visible);
        return (O) this;
    }

    public <O extends AbstractSetting<T>> O addValueChangeListeners(IValueChangeListener<T>... valueChangeListeners) {
        this.valueChangeListeners.addAll(Arrays.asList(valueChangeListeners));
        return (O) this;
    }

    // this is recursive, I'm so smart fr
    public <O extends AbstractSetting<T>> O addExpandableParents(ExpandableValue... expandableValues) {
        for(ExpandableValue expandableValue : expandableValues) {
            if(!expandableValue.getValues().contains(this)) {
                expandableValue.getValues().add(this);
                for(ExpandableValue expandableValue1 : expandableValue.getExpandableParents())
                    addExpandableParents(expandableValue1);
                this.visible.addAll(expandableValue.visible);
            }
            if(!this.expandableParents.contains(expandableValue))
                this.expandableParents.add(expandableValue);
        }
        return (O) this;
    }

}
