package com.cxcron.quartz;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

public final class RandomDailySchedule {

    private RandomDailySchedule() {
    }

    /**
     * 校验每日随机执行的时间范围是否有效。
     *
     * @param start 随机执行开始时间
     * @param end   随机执行结束时间
     * @return 时间范围有效时返回 {@code true}
     */
    public static boolean isValidWindow(LocalTime start, LocalTime end) {
        return start != null && end != null && start.getSecond() == 0 && start.getNano() == 0
                && end.getSecond() == 0 && end.getNano() == 0 && start.isBefore(end);
    }

    /**
     * 计算不早于当前时间的下一次随机执行时间；当天时间窗口已过时安排在次日。
     *
     * @param start 每日随机执行开始时间
     * @param end   每日随机执行结束时间
     * @param now   当前时间
     * @return 下一次随机执行时间
     */
    public static LocalDateTime nextExecutionTime(LocalTime start, LocalTime end, LocalDateTime now) {
        LocalDateTime earliest = now.toLocalDate().atTime(start);
        LocalDateTime latest = now.toLocalDate().atTime(end);
        LocalDateTime firstAvailable = now.withSecond(0).withNano(0).plusMinutes(1);
        if (firstAvailable.isBefore(earliest)) firstAvailable = earliest;
        if (!firstAvailable.isAfter(latest)) return randomTime(firstAvailable, latest);
        return nextDayExecutionTime(start, end, now);
    }

    /**
     * 计算次日时间窗口内的随机执行时间。
     *
     * @param start 每日随机执行开始时间
     * @param end   每日随机执行结束时间
     * @param now   当前时间
     * @return 次日的随机执行时间
     */
    public static LocalDateTime nextDayExecutionTime(LocalTime start, LocalTime end, LocalDateTime now) {
        var nextDay = now.toLocalDate().plusDays(1);
        return randomTime(nextDay.atTime(start), nextDay.atTime(end));
    }

    private static LocalDateTime randomTime(LocalDateTime start, LocalDateTime end) {
        int startMinute = (start.getHour() * 60) + start.getMinute();
        int endMinute = (end.getHour() * 60) + end.getMinute();
        int minute = ThreadLocalRandom.current().nextInt(startMinute, endMinute + 1);
        return start.toLocalDate().atTime(minute / 60, minute % 60);
    }
}
