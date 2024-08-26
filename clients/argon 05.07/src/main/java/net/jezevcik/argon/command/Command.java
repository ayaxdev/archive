package net.jezevcik.argon.command;

import net.jezevcik.argon.system.identifier.Identifiable;
import net.jezevcik.argon.system.identifier.Identifiables;
import net.jezevcik.argon.system.identifier.IdentifierType;
import net.jezevcik.argon.utils.chat.ChatUtils;

import java.util.Arrays;
import java.util.List;

public abstract class Command implements Identifiable {

    public final String name;
    public final String[] aliases;
    public final Syntax syntax;
    public final String description;

    public Command(String name, String[] aliases, Syntax syntax, String description) {
        this.name = name;
        this.aliases = aliases;
        this.syntax = syntax;
        this.description = description;
    }

    @Override
    public String getIdentifier(IdentifierType identifierType) {
        return switch (identifierType) {
            case UNIQUE_SHORT, DISPLAY -> name;
            case UNIQUE_NORMAL -> Identifiables.getIdentifier(this, name);
        };
    }

    public abstract void execute(final String[] args);

    protected void pushSyntaxError() {
        final String message = String.format("Valid syntax - .%s %s", aliases[0], syntax.toString());
        ChatUtils.pushWithPrefix(message, ChatUtils.Prefix.ERROR);
    }

    public final String[] group = new String[] {"commands"};

    @Override
    public String[] getGroup() {
        return group;
    }

    public static class Syntax {

        public final List<Element> elements;

        public Syntax(List<Element> elements) {
            this.elements = elements;
        }

        public static Syntax of(Element... elements) {
            return new Syntax(Arrays.asList(elements));
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();

            for (final Element element : elements) {
                builder.append(element.toString()).append(" ");
            }

            builder.delete(builder.length() - 1, builder.length());

            return builder.toString();
        }

        public record Element(ElementType elementType, InputType inputType, String name) {

            @Override
            public String toString() {
                final String out = switch (elementType) {
                    case OPTIONAL -> "<%s>";
                    case REQUIRED -> "[%s]";
                };

                final String in = String.format("%s: %s", name, inputType.name);

                return String.format(out, in);
            };

        }

        public enum InputType {
            STRING("text"),
            BOOLEAN("true/false"),
            NUMBER("number"),
            MODULE("moduleName"),
            SETTING("settingName"),
            KEY("key");

            public final String name;

            InputType(final String name) {
                this.name = name;
            }
        }

        public enum ElementType {
            OPTIONAL, REQUIRED;
        }

    }
}
