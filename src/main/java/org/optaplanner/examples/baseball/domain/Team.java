package org.optaplanner.examples.baseball.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import java.math.BigDecimal;
import java.util.HashMap;

public class Team extends AbstractPersistable {
    private String name;
    private String stadium;
    private HashMap<String, BigDecimal> distanceTo;
//    private HashMap<String, BigDecimal> distanceFrom;

    public Team() {

    }
    public Team(long id, String name, String stadium) {
        super(id);
        this.name = name;
        this.stadium = stadium;
    }

    public Team(long id, String name, String stadium, HashMap<String, BigDecimal> distanceTo) {
        super(id);
        this.name = name;
        this.stadium = stadium;
        this.distanceTo = distanceTo;
//        this.distanceFrom = distanceFrom;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public HashMap<String, BigDecimal> getDistanceTo() {
        return distanceTo;
    }

    public void setDistanceTo(HashMap<String, BigDecimal> distanceTo) {
        this.distanceTo = distanceTo;
    }

//    public HashMap<String, BigDecimal> getDistanceFrom() {
//        return distanceFrom;
//    }
//
//    public void setDistanceFrom(HashMap<String, BigDecimal> distanceFrom) {
//        this.distanceFrom = distanceFrom;
//    }

    @Override
    public String toString() {
        return "Team{" +
                "name='" + name + '\'' +
                ", stadium='" + stadium + '\'' +
                '}';
    }
}
