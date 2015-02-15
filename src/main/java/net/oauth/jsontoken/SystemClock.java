/**
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package net.oauth.jsontoken;

import java.time.Instant;

/**
 * Created by steve on 12/09/14.
 */
public class SystemClock implements Clock {
    public static final int DEFAULT_ACCEPTABLE_CLOCK_SKEW_IN_MIN = 2;

    private final int acceptableClockSkewInMin;

    /**
     * Public constructor.
     */
    public SystemClock() {
        this(DEFAULT_ACCEPTABLE_CLOCK_SKEW_IN_MIN);
    }

  /**
   * Public constructor.
     * @param acceptableClockSkewInMin the current time will be considered inside the
     *   interval at {@link #isCurrentTimeInInterval(java.time.Instant, java.time.Instant)} even if the current time
     *   is up to acceptableClockSkewInMin off the ends of the interval.
   */
    public SystemClock(int acceptableClockSkewInMin) {
        this.acceptableClockSkewInMin = acceptableClockSkewInMin;
  }

    /*
     * (non-Javadoc)
     * @see net.oauth.jsontoken.Clock#now()
     */
    @Override
    public Instant now() {
        return Instant.now();
    }

  /**
     * Determines whether the current time (plus minus the acceptableClockSkewInMin) falls within the
   * interval defined by the start and intervalLength parameters.
   */
  @Override
  public boolean isCurrentTimeInInterval(Instant start, Instant end) {
        start = start.minusSeconds(acceptableClockSkewInMin*60);
        end = end.plusSeconds(acceptableClockSkewInMin*60);
        return now().isAfter(start) && now().isBefore(end);
  }

}
