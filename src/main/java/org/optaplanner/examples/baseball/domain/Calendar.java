package org.optaplanner.examples.baseball.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import java.time.LocalDateTime;

public class Calendar extends AbstractPersistable implements Comparable<Calendar> {

    private LocalDateTime startTime;
    private Calendar next;
    private Calendar prev;
    private int consecutive;
    private boolean weekend;
    private boolean holiday;

    public Calendar(long id, LocalDateTime startTime, int consecutive, boolean weekend, boolean holiday) {
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

    public Calendar getNext() {
        return next;
    }

    public void setNext(Calendar next) {
        this.next = next;
    }

    public Calendar getPrev() {
        return prev;
    }

    public void setPrev(Calendar prev) {
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
        return "Calendar{" +
                "startTime=" + startTime +
                ", consecutive=" + consecutive +
                ", weekend=" + weekend +
                ", holiday=" + holiday +
                '}';
    }


    @Override
    public int compareTo(Calendar o) {

        if (this.startTime.isEqual(o.startTime)) {
            return this.id.compareTo(o.id);
        } else {
            return this.startTime.compareTo(o.startTime);
        }

    }
}
