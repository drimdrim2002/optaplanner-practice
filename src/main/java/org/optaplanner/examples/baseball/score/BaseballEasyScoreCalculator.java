package org.optaplanner.examples.baseball.score;

import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.examples.baseball.domain.BaseballSolution;
import org.optaplanner.examples.baseball.domain.Calendar;
import org.optaplanner.examples.baseball.domain.Match;
import org.optaplanner.examples.baseball.domain.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class BaseballEasyScoreCalculator implements EasyScoreCalculator<BaseballSolution, BendableLongScore> {
    private static final Logger logger = LoggerFactory.getLogger(BaseballEasyScoreCalculator.class);

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
        HashSet<String> prevMatch = new HashSet<>();
        HashMap<Team, Queue<Team>> visitOrderByTeam = new HashMap<>();
        for (Calendar calendar : calendarSet) {

            HashSet<String> teamDuplicationCheck = new HashSet<>();
            HashSet<String> stadiumDuplicationCheck = new HashSet<>();
            List<Match> matchList = matchListByCalendar.get(calendar);
            for (Match match : matchList) {
                String homeTeam = match.getHome().getName();
                String awayTeam = match.getAway().getName();
                String matchDuplicationKey = homeTeam + awayTeam;
                if (prevMatch.contains(matchDuplicationKey)) {
                    hard1Score -= 1;
                }

                teamDuplicationCheck.add(homeTeam);
                teamDuplicationCheck.add(awayTeam);
                stadiumDuplicationCheck.add(match.getHome().getStadium());

                // visit order
                if (!visitOrderByTeam.containsKey(match.getHome())) {
                    visitOrderByTeam.put(match.getHome(), new LinkedList<>());
                }
                visitOrderByTeam.get(match.getHome()).add(match.getHome());

                if (!visitOrderByTeam.containsKey(match.getAway())) {
                    visitOrderByTeam.put(match.getAway(), new LinkedList<>());
                }
                visitOrderByTeam.get(match.getAway()).add(match.getHome());


            }
            prevMatch.clear();
            for (Match match : matchList) {
                String homeTeam = match.getHome().getName();
                String awayTeam = match.getAway().getName();
                prevMatch.add(homeTeam + awayTeam);
            }

            if (teamDuplicationCheck.size() != 10) {
                int min = Math.min(teamDuplicationCheck.size(), 10);
                int max = Math.max(teamDuplicationCheck.size(), 10);
                hard0Score -= (max - min);
            }


            if (stadiumDuplicationCheck.size() != 5) {
                int min = Math.min(stadiumDuplicationCheck.size(), 5);
                int max = Math.max(stadiumDuplicationCheck.size(), 5);
                hard0Score -= (max - min);
            }

        }

        for (Match match : baseballSolution.getMatchList()) {
            if (match.getCalendar() == null || match.getCalendar().getId().equals(9999L)) {
                soft0Score -= 1;
            }
        }

        HashMap<String, BigDecimal> distanceByTeam = new HashMap<>();
        for (Map.Entry<Team, Queue<Team>> visitOrderEntry : visitOrderByTeam.entrySet()) {
            Team team = visitOrderEntry.getKey();
            Queue<Team> visitOrders = visitOrderEntry.getValue();
            Team prevTeam = null;
            BigDecimal totalDistance = BigDecimal.ZERO;
            for (Team visitTeam : visitOrders) {
                if (prevTeam != null) {
                    BigDecimal distance = prevTeam.getDistanceTo(visitTeam);
                    totalDistance = totalDistance.add(distance);
                }

                prevTeam = visitTeam;
            }
            distanceByTeam.put(team.getName(), totalDistance);
        }

        BigDecimal sumDistance = BigDecimal.ZERO;
        for (BigDecimal distance : distanceByTeam.values()) {
            sumDistance = sumDistance.add(distance);
        }
        BigDecimal meanDistance = sumDistance.divide(BigDecimal.valueOf(10), RoundingMode.DOWN);

        BigDecimal variance = BigDecimal.ZERO;
        for (BigDecimal distance : distanceByTeam.values()) {
            BigDecimal diff = distance.subtract(meanDistance);
            diff = diff.divide(BigDecimal.valueOf(100), RoundingMode.DOWN);
            variance  = variance.add(diff.pow(2));
        }

        double standardDeviation = Math.sqrt(variance.doubleValue());
        soft1Score -= standardDeviation;
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
