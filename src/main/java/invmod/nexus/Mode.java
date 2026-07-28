package invmod.nexus;

public enum Mode {
    STOPPED,
    STARTED,
    CONTINUOUS,
    WAITING,
    STABLE;

    static final Mode[] VALUES = values();

    public boolean isActive() {
        return this == STARTED || this == CONTINUOUS || this == WAITING;
    }

    public boolean isIdle() {
        return this == STOPPED || this == STABLE;
    }

    public static Mode forId(int id) {
        return VALUES[Math.abs(id) % VALUES.length];
    }
}