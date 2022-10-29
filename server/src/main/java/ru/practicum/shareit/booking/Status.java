package ru.practicum.shareit.booking;

public enum Status {
    ALL,
    APPROVED,
    REJECTED,
    WAITING,
    FUTURE,
    CURRENT,
    PAST;

    public static Status from(String statusParameter) {
        try {
            return Status.valueOf(statusParameter);
        } catch (Exception e) {
            return null;
        }
    }
}
