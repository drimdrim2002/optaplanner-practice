package org.optaplanner.examples.baseball.domain;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;

import java.util.List;

@PlanningSolution
public class BaseballSolution extends AbstractPersistable {

    private List<Match> matchList;
    private  List<Period> periodList;
    private HardSoftLongScore hardSoftLongScore;

    @PlanningEntityCollectionProperty
    public List<Match> getMatchList() {
        return matchList;
    }

    public void setMatchList(List<Match> matchList) {
        this.matchList = matchList;
    }

    @ValueRangeProvider(id = "periodRange")
    @ProblemFactCollectionProperty
    public List<Period> getPeriodList() {
        return periodList;
    }

    public void setPeriodList(List<Period> periodList) {
        this.periodList = periodList;
    }

    @PlanningScore
    public HardSoftLongScore getHardSoftLongScore() {
        return hardSoftLongScore;
    }

    public void setHardSoftLongScore(HardSoftLongScore hardSoftLongScore) {
        this.hardSoftLongScore = hardSoftLongScore;
    }
}
