package ch.ffhs.spring_boosters.service.implementation.enumerator;

public enum PriorityEnum {

    OVERDUE("overdue"),
    DUE_SOON("due-soon"),
    UPCOMING("upcoming");

    private final String value;

    PriorityEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PriorityEnum fromValue(String value) {
        for (PriorityEnum p : values()) {
            if (p.value.equalsIgnoreCase(value)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Unknown priority: " + value);
    }
}