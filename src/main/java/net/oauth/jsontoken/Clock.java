package net.oauth.jsontoken;

import java.time.Instant;

/**
 * Created by steve on 12/09/14.
 */
public interface Clock {
    /**
     * Returns current time.
     */
    public Instant now();

    /**
     * Determines whether the current time falls within the interval defined by the
     * start and intervalLength parameters. Implementations are free to fudge this a
     * little bit to take into account possible clock skew.
     */
    public boolean isCurrentTimeInInterval(Instant start, Instant end);
}
