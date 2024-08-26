package net.jezevcik.argon.system.identifier;

public class Identifiables {

    public static String getIdentifier(Identifiable identifiable, String uniqueName) {
        return String.join("#", identifiable.getGroup()) + "->" + uniqueName;
    }

    public static String getIdentifier(Object o) {
        return o instanceof Identifiable identifiable ? identifiable.getIdentifier(IdentifierType.UNIQUE_NORMAL) : o.toString();
    }

}
