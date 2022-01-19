package org.optaplanner.examples.baseball.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import java.time.LocalDateTime;

public class Period extends AbstractPersistable {

    private LocalDateTime startTime;
    private Period next;
    private Period prev;
    private int consecutive;
    private boolean weekend;
    private boolean holiday;

    public Period(long id, LocalDateTime startTime, int consecutive, boolean weekend, boolean holiday) {
        super(id);
        this.startTime = startTime;
        this.consecutive = consecutive;
        this.weekend = weekend;
        this.holiday = holiday;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Period getNext() {
        return next;
    }

    public void setNext(Period next) {
        this.next = next;
    }

    public Period getPrev() {
        return prev;
    }

    public void setPrev(Period prev) {
        this.prev = prev;
    }

    public int getConsecutive() {
        return consecutive;
    }

    public void setConsecutive(int consecutive) {
        this.consecutive = consecutive;
    }

    public boolean isWeekend() {
        return weekend;
    }

    public void setWeekend(boolean weekend) {
        this.weekend = weekend;
    }

    public boolean isHoliday() {
        return holiday;
    }

    public void setHoliday(boolean holiday) {
        this.holiday = holiday;
    }

    @Override
    public String toString() {
        return "Period{" +
                "startTime=" + startTime +
                ", consecutive=" + consecutive +
                ", weekend=" + weekend +
                ", holiday=" + holiday +
                '}';
    }
}
