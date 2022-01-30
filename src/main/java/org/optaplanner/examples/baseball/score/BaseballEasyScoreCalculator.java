package org.optaplanner.examples.baseball.score;

import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.examples.baseball.domain.BaseballSolution;
import org.optaplanner.examples.baseball.domain.Calendar;
import org.optaplanner.examples.baseball.domain.Match;

import java.util.*;

public class BaseballEasyScoreCalculator implements EasyScoreCalculator<BaseballSolution, BendableLongScore> {

    // hard
    // 1. 하루에 팀이 중복되면 안된다.
    // 2. 하루에 stadium이 중복되면 안된다.
    // 3. 동일한 match가 연속될 수 없다.

    // soft
    // 1. match 중 calendar가 null이거나 dummy 인 것을 최소화한다.
    // 2. 팀별 이동 거리를 평준화한다.
    // 3. 휴일 평준화
    // 4. 주말 평준화

    @Override
    public BendableLongScore calculateScore(BaseballSolution baseballSolution) {

        int hard0Score = 0;
        int hard1Score = 0;

        int soft0Score = 0;
        int soft1Score = 0;

        TreeMap<Calendar, List<Match>> matchListByCalendar = getCalendarListTreeMap(baseballSolution);

        Set<Calendar> calendarSet = matchListByCalendar.keySet();
        for (Calendar calendar : calendarSet) {

            HashSet<String> teamDuplicationCheck = new HashSet<>();
            HashSet<String> stadiumDuplicationCheck = new HashSet<>();
            List<Match> matchList = matchListByCalendar.get(calendar);
            for (Match match : matchList) {
                String homeTeam = match.getHome().getName();
                String awayTeam = match.getAway().getName();
                teamDuplicationCheck.add(homeTeam);
                teamDuplicationCheck.add(awayTeam);
                stadiumDuplicationCheck.add(match.getHome().getStadium());
            }

            if (teamDuplicationCheck.size() != 10) {
                int min = Math.min(teamDuplicationCheck.size(), 10);
                int max = Math.max(teamDuplicationCheck.size(), 10);
                hard0Score -= (max-min);
            }


            if (stadiumDuplicationCheck.size() != 5) {
                int min = Math.min(stadiumDuplicationCheck.size(), 5);
                int max = Math.max(stadiumDuplicationCheck.size(), 5);
                hard0Score -= (max-min);
            }

        }

        return BendableLongScore.of(new long[]{hard0Score, hard1Score},
                new long[]{soft0Score, soft1Score});
    }

    private TreeMap<Calendar, List<Match>> getCalendarListTreeMap(BaseballSolution baseballSolution) {
        TreeMap<Calendar, List<Match>> matchListByCalendar = new TreeMap();
        for (Match match : baseballSolution.getMatchList()) {
            Calendar calendar = match.getCalendar();
            if (calendar == null) {
                calendar = baseballSolution.getCalendarList().get(baseballSolution.getCalendarList().size() - 1);
            }
            if (!matchListByCalendar.containsKey(calendar)) {
                matchListByCalendar.put(calendar, new ArrayList<>());
            }
            matchListByCalendar.get(calendar).add(match);

        }
        return matchListByCalendar;
    }
}
