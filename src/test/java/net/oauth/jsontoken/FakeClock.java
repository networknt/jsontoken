package net.oauth.jsontoken;


import java.time.Instant;

/**
 * Created by steve on 12/09/14.
 */
public class FakeClock extends SystemClock {
    private Instant now = Instant.now();

    public FakeClock() {
        super(0);
    }

    public FakeClock(int acceptableClockSkewInMin) {
        super(acceptableClockSkewInMin);
    }

    public void setNow(Instant i) {
        now = i;
    }

    @Override
    public Instant now() {
        return now;
    }

}

