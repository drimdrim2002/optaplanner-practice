package org.optaplanner.examples.baseball.domain;


import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.entity.PlanningPin;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@PlanningEntity
public class Match extends AbstractPersistable {
    private Team home;
    private Team away;
    private int consecutive;
    private boolean pinned;

    // planning variable
    private Period period;


    public Match() {

    }

    public Match(long id, Team home, Team away, int consecutive) {
        super(id);
        this.home = home;
        this.away = away;
        this.consecutive = consecutive;
    }

    @PlanningPin
    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }



    public Team getHome() {
        return home;
    }

    public void setHome(Team home) {
        this.home = home;
    }

    public Team getAway() {
        return away;
    }

    public void setAway(Team away) {
        this.away = away;
    }

    public int getConsecutive() {
        return consecutive;
    }

    public void setConsecutive(int consecutive) {
        this.consecutive = consecutive;
    }

    @PlanningVariable(valueRangeProviderRefs = {"periodRange"})
    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return "Match{" +
                "home=" + home +
                ", away=" + away +
                ", consecutive=" + consecutive +
                '}';
    }
}
