package dev.dhev.realworld.shared;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class InstantPrecision {

    private InstantPrecision() {
    }

    public static Instant toMicros(Instant instant) {
        return instant == null ? null : instant.truncatedTo(ChronoUnit.MICROS);
    }
}
