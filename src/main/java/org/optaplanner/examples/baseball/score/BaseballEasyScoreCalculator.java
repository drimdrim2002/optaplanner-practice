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

public class BaseballEasyScoreCalculator implements EasyScoreCalculator<BaseballSolution, BendableLongScore> {
    private static final Logger logger = LoggerFactory.getLogger(BaseballEasyScoreCalculator.class);

    // hard
    // 1. 하루에 팀이 중복되면 안된다.
    // 2. 하루에 stadium이 중복되면 안된다.
    // 3. 동일한 match가 연속될 수 없다.

    // soft
    // 1. match 중 calendar가 null이거나 dummy 인 것을 최소화한다.
    // 2. 휴일 및 주말 평준화
    // 3. home/away 가 연속 3일 허용
    // 4. 거리 평준화
    @Override
    public BendableLongScore calculateScore(BaseballSolution baseballSolution) {

        int duplicationHardScore = 0;
        int successiveHardScore = 0;

        int minimizeShortScore = 0;
        int stabilizeDistanceScore = 0;
        int stabilizeHolidayScore = 0;

        TreeMap<Calendar, List<Match>> matchListByCalendar = getCalendarListTreeMap(baseballSolution);

        Set<Calendar> calendarSet = matchListByCalendar.keySet();
        HashSet<String> prevMatch = new HashSet<>();
        HashMap<Team, Queue<Team>> visitOrderByTeam = new HashMap<>();

        for (Calendar calendar : calendarSet) {

            HashSet<String> teamDuplicationCheck = new HashSet<>();
            HashSet<String> stadiumDuplicationCheck = new HashSet<>();
            List<Match> matchList = matchListByCalendar.get(calendar);

            int calendarConsecutive = calendar.getConsecutive();
            for (Match match : matchList) {
                String homeTeam = match.getHome().getName();
                String awayTeam = match.getAway().getName();
                String matchDuplicationKey = homeTeam + awayTeam;
                if (prevMatch.contains(matchDuplicationKey)) {
                    successiveHardScore -= 1;
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
                prevMatch.add(awayTeam + homeTeam);

                if (match.getConsecutive() != calendarConsecutive) {
                    duplicationHardScore -= 1;
                }
            }

            if (teamDuplicationCheck.size() != 10) {
                int min = Math.min(teamDuplicationCheck.size(), 10);
                int max = Math.max(teamDuplicationCheck.size(), 10);
                duplicationHardScore -= (max - min);
            }

            if (stadiumDuplicationCheck.size() != 5) {
                int min = Math.min(stadiumDuplicationCheck.size(), 5);
                int max = Math.max(stadiumDuplicationCheck.size(), 5);
                duplicationHardScore -= (max - min);
            }

            if (matchList.size() != 5) {
                int min = Math.min(matchList.size(), 5);
                int max = Math.max(matchList.size(), 5);
                duplicationHardScore -= (max - min);
            }

        }
        HashMap<Team, Integer> holidayByTeam = new HashMap<>();
        for (Match match : baseballSolution.getMatchList()) {
            if (match.getCalendar() == null || match.getCalendar().getId().equals(9999L)) {
                minimizeShortScore -= 1;
                continue;
            }

            if (match.getCalendar().isHoliday() || match.getCalendar().isWeekend()) {
                int prevQty = holidayByTeam.getOrDefault(match.getHome(), 0);
                holidayByTeam.put(match.getHome(), prevQty + 1);
            }
        }

        HashMap<String, BigDecimal> distanceByTeam = new HashMap<>();
        for (Map.Entry<Team, Queue<Team>> visitOrderEntry : visitOrderByTeam.entrySet()) {
            Team team = visitOrderEntry.getKey();
            Queue<Team> visitOrders = visitOrderEntry.getValue();
            Team prevTeam = null;
            BigDecimal totalDistance = BigDecimal.ZERO;

            int consecutive = 1;
            boolean prevIsHome = true; //초반에 무엇이 되어도 상관 없다.
            for (Team visitTeam : visitOrders) {
                boolean isHome = team.equals(visitTeam);
                if (prevTeam != null) {
                    BigDecimal distance = prevTeam.getDistanceTo(visitTeam);
                    totalDistance = totalDistance.add(distance);

                    if (isHome == prevIsHome) {
                        consecutive++;
                    } else {
                        consecutive = 1;
                    }

                    if (consecutive > 3) {
                        duplicationHardScore -= 1;
                    }
                }
                prevIsHome = isHome;
                prevTeam = visitTeam;


            }
            distanceByTeam.put(team.getName(), totalDistance);
        }

        BigDecimal sumDistance = BigDecimal.ZERO;
        for (BigDecimal distance : distanceByTeam.values()) {
            sumDistance = sumDistance.add(distance);
        }
        BigDecimal meanDistance = sumDistance.divide(BigDecimal.valueOf(10), RoundingMode.DOWN);

        BigDecimal distanceVariance = BigDecimal.ZERO;
        for (BigDecimal distance : distanceByTeam.values()) {
            BigDecimal diff = distance.subtract(meanDistance);
            diff = diff.divide(BigDecimal.valueOf(100), RoundingMode.DOWN);
            distanceVariance = distanceVariance.add(diff.pow(2));
        }
        stabilizeDistanceScore -= distanceVariance.intValue();


        double meanHolidayCount = 0.0;
        for (Team team : holidayByTeam.keySet()) {
            int holidayCnt = holidayByTeam.get(team);
            meanHolidayCount += holidayCnt;
        }
        meanHolidayCount /= 10;

        double holidayVariance = 0;
        for (Team team : holidayByTeam.keySet()) {
            int holidayCnt = holidayByTeam.get(team);
            double diff = Math.pow(Math.abs(holidayCnt - meanHolidayCount), 2);
            holidayVariance += diff;
        }

        stabilizeHolidayScore -= holidayVariance;
        return BendableLongScore.of(new long[]{duplicationHardScore, successiveHardScore},
                new long[]{minimizeShortScore, stabilizeHolidayScore, stabilizeDistanceScore, -sumDistance.longValue()});


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
