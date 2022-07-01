package Model;

public enum AccessLevel {
    Default,
    Developer,
    ProjectManager,
    Admin;

    public boolean isSufficientFor(AccessLevel minimumAccessLevel) {
        return this.ordinal() >= minimumAccessLevel.ordinal();
    }
}
