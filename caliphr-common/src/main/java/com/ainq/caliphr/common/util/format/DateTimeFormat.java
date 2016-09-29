package com.ainq.caliphr.common.util.format;

import java.util.concurrent.TimeUnit;

public class DateTimeFormat {
    public static String humanElapsedTime(Long resourceStartTime) {
        Long millis = System.currentTimeMillis() - resourceStartTime;
        return String.format("%02d:%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)),
                TimeUnit.MILLISECONDS.toMillis(millis) -
                        TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis))
        );
    }
}
