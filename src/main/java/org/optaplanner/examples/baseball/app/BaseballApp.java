package org.optaplanner.examples.baseball.app;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.baseball.domain.*;
import org.optaplanner.examples.baseball.domain.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.ValidationException;
import java.io.FileReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;

public class BaseballApp {

    public static final String DATA_DIR_NAME = "baseball";
    private static final Logger logger = LoggerFactory.getLogger(BaseballApp.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        // 데이터 읽기


        try {
            JSONObject jsonObject = readJsonFile();


            // solution 만들기
            BaseballSolution unsolvedSolution = makeSolution(jsonObject);

            // initial 만들기

            JSONArray initialPlanArray = (JSONArray) jsonObject.get("initialPlan");

            HashMap<String, Queue<Match>> matchHashMap = new HashMap<>();

            for (Match match : unsolvedSolution.getMatchList()) {
                String home = match.getHome().getName();
                String away = match.getAway().getName();
                String consecutive = String.valueOf(match.getConsecutive());
                String key = home + away + consecutive;
                if (!matchHashMap.containsKey(key)) {
                    matchHashMap.put(key, new LinkedList<>());
                }
                matchHashMap.get(key).add(match);
            }

            Set<String> matchesKeyIterate = matchHashMap.keySet();
            for (String matchKey : matchesKeyIterate) {
                if (matchKey.startsWith("SSG두산")) {
                    logger.info("key: " + matchKey + ", value: " + matchHashMap.get(matchKey));
                }
            }


            HashMap<LocalDateTime, Calendar> calendarHashMap = new HashMap<>();
            for (Calendar calendar : unsolvedSolution.getCalendarList()) {
                LocalDateTime startTIme = calendar.getStartTime();
                calendarHashMap.put(startTIme, calendar);
            }


            int initialIndex = 0;
            for (Object o : initialPlanArray) {


                JSONObject initialPlanInfo = (JSONObject) o;
                LocalDateTime datetime = LocalDateTime.parse((String) initialPlanInfo.get("datetime"), formatter);
                String home = initialPlanInfo.get("home").toString();
                String away = initialPlanInfo.get("away").toString();
                String consecutive = String.valueOf(Math.round((double) initialPlanInfo.get("consecutive")));
                String key = home + away + consecutive;


                Match match = matchHashMap.get(key).poll();


                Calendar calendar = calendarHashMap.get(datetime);


                match.setCalendar(calendar);

                if (initialIndex < 5) {
                    match.setPinned(true);
                }
                initialIndex++;
            }

            for (Match match : unsolvedSolution.getMatchList()) {
                if (match.getCalendar() == null) {
                    logger.info(match.toString());
                }
            }


            SolverFactory<BaseballSolution> solverFactory = SolverFactory.createFromXmlResource("org/optaplanner/examples/baseball/solver/baseballSolverConfig.xml");
            Solver<BaseballSolution> solver = solverFactory.buildSolver();

            BaseballSolution solvedSolution = solver.solve(unsolvedSolution);

            for (Match match : solvedSolution.getMatchList()) {
                logger.info("match : " + match.toString() + ", period : " + match.getCalendar().toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }


    }

    private static BaseballSolution makeSolution(JSONObject jsonObject) {
        HashMap<String, HashMap<String, BigDecimal>> totalDistanceMap = new HashMap<>();
        JSONArray distanceMatrixArray = (JSONArray) jsonObject.get("distanceMatrix");
        for (Object o : distanceMatrixArray) {
            JSONObject distanceInfo = (JSONObject) o;
            String from = (String) distanceInfo.get("from");
            String to = (String) distanceInfo.get("to");
            BigDecimal distance = BigDecimal.valueOf((Double) distanceInfo.get("distance"));
            if (!totalDistanceMap.containsKey(from)) {
                totalDistanceMap.put(from, new HashMap<>());
            }
            totalDistanceMap.get(from).put(to, distance);
        }


        HashMap<String, Team> teamHashMap = new HashMap<>();
        JSONArray teamJsonArray = (JSONArray) jsonObject.get("teams");
        IntStream.range(0, teamJsonArray.size()).forEach(i -> {
            JSONObject teamInfo = (JSONObject) teamJsonArray.get(i);
            String name = (String) teamInfo.get("name");
            String stadium = (String) teamInfo.get("stadium");
            Set<String> toSet = totalDistanceMap.get(name).keySet();
            HashMap<String, BigDecimal> distanceTo = new HashMap<>();
            for (String to : toSet) {
                BigDecimal distance = totalDistanceMap.get(name).get(to);
                distanceTo.put(to, distance);
            }
            Team team = new Team(i, name, stadium, distanceTo);
            teamHashMap.put(name, team);
        });

        List<Match> matchList = new ArrayList<>();
        JSONArray matchJsonArray = (JSONArray) jsonObject.get("matches");
        IntStream.range(0, matchJsonArray.size()).forEach(i -> {
            JSONObject matchInfo = (JSONObject) matchJsonArray.get(i);
            Team homeTeam = teamHashMap.get(matchInfo.get("home"));
            Team awayTeam = teamHashMap.get(matchInfo.get("away"));
            long consecutive = (Long) matchInfo.get("consecutive");
            Match match = new Match(i, homeTeam, awayTeam, (int) consecutive);
            matchList.add(match);
        });

        List<Calendar> calendarList = new ArrayList<>();
        JSONArray periodArray = (JSONArray) jsonObject.get("calendar");
        Calendar prev = null;
        for (int i = 0; i < periodArray.size(); i++) {
            JSONObject calendarInfo = (JSONObject) periodArray.get(i);
            LocalDateTime startTime = LocalDateTime.parse((String) calendarInfo.get("datetime"), formatter);
            int consecutive = Integer.parseInt(calendarInfo.get("consecutive").toString());
            boolean holiday = Integer.parseInt(calendarInfo.get("holiday").toString()) == 1.0 ? true : false;
            boolean weekend = (Integer.parseInt(calendarInfo.get("weekend").toString())) == 1.0 ? true : false;

            Calendar period = new Calendar(i, startTime, consecutive, holiday, weekend);
            period.setPrev(prev);
            calendarList.add(period);
            prev = period;
        }

        for (Calendar period : calendarList) {
            if (period.getPrev() != null) {
                period.getPrev().setNext(period);
            }
        }

        BaseballSolution baseballSolution = new BaseballSolution();
        baseballSolution.setMatchList(matchList);
        baseballSolution.setCalendarList(calendarList);
        baseballSolution.setId(0L);

        return baseballSolution;
    }

    private static JSONObject readJsonFile() throws ValidationException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject;
        try {
            Reader reader = new FileReader("data/baseball/input.json");
//            Reader reader = new FileReader("../../../data/baseball/input.json");
            jsonObject = (JSONObject) parser.parse(reader);
            logger.info(jsonObject.toString());

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new ValidationException("No json file");
        }
        return jsonObject;
    }
}
