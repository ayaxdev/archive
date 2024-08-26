package net.jezevcik.argon.system.identifier;

public interface Identifiable {

    String getIdentifier(IdentifierType identifierType);

    String[] getGroup();

}

