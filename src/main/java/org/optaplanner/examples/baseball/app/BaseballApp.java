package org.optaplanner.examples.baseball.app;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.baseball.domain.BaseballSolution;
import org.optaplanner.examples.baseball.domain.Match;
import org.optaplanner.examples.baseball.domain.Period;
import org.optaplanner.examples.baseball.domain.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.ValidationException;
import java.io.FileReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class BaseballApp {

    public static final String DATA_DIR_NAME = "baseball";
    private static Logger logger = LoggerFactory.getLogger(BaseballApp.class);
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        // 데이터 읽기


        try {
            JSONObject jsonObject = readJsonFile();


            // solution 만들기
            BaseballSolution unsolvedSolution = makeSolution(jsonObject);

            // initial 만들기


            SolverFactory<BaseballSolution> solverFactory = SolverFactory.createFromXmlResource("org/optaplanner/examples/baseball/solver/baseballSolverConfig.xml");
            Solver solver = solverFactory.buildSolver();

            BaseballSolution solvedSolution = (BaseballSolution) solver.solve(unsolvedSolution);

            for (Match match : solvedSolution.getMatchList()) {
                logger.info("match : " + match.toString() + ", period : " + match.getPeriod().toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }


    }

    private static BaseballSolution makeSolution(JSONObject jsonObject) {
        HashMap<String, HashMap<String, BigDecimal>> totalDistanceMap = new HashMap<>();
        JSONArray distanceMatrixArray = (JSONArray) jsonObject.get("distanceMatrix");
        for (int i = 0; i < distanceMatrixArray.size(); i++) {
            JSONObject distanceInfo = (JSONObject) distanceMatrixArray.get(i);
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
        for (int i = 0; i < teamJsonArray.size(); i++) {
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
        }

        List<Match> matchList = new ArrayList<>();
        JSONArray matchJsonArray = (JSONArray) jsonObject.get("matches");
        for (int i = 0; i < matchJsonArray.size(); i++) {
            JSONObject matchInfo = (JSONObject) matchJsonArray.get(i);
            Team homeTeam = teamHashMap.get(matchInfo.get("home"));
            Team awayTeam = teamHashMap.get(matchInfo.get("away"));
            long consecutive = (Long) matchInfo.get("matches");

//                double consecutive = Double.parseDouble( (String) matchInfo.get("matches"));
            Match match = new Match(i, homeTeam, awayTeam, (int) consecutive);
            matchList.add(match);

        }

        List<Period> periodList = new ArrayList<>();
        JSONArray periodArray = (JSONArray) jsonObject.get("calendar");
        Period prev = null;
        for (int i = 0; i < periodArray.size(); i++) {
            JSONObject periodInfo = (JSONObject) periodArray.get(i);
            LocalDateTime startTime = LocalDateTime.parse((String) periodInfo.get("datetime"), formatter);
            double consecutive = (Double) periodInfo.get("matches");
            boolean holiday = ((Double) periodInfo.get("holiday")) == 1.0 ? true : false;
            boolean weekend = ((Double) periodInfo.get("weekend")) == 1.0 ? true : false;

            Period period = new Period(i, startTime, (int) consecutive, holiday, weekend);
            period.setPrev(prev);
            periodList.add(period);
            prev = period;
        }

        for (Period period : periodList) {
            if (period.getPrev() != null) {
                period.getPrev().setNext(period);
            }
        }

        BaseballSolution baseballSolution = new BaseballSolution();
        baseballSolution.setMatchList(matchList);
        baseballSolution.setPeriodList(periodList);
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
            logger.error(e.getMessage());
            throw new ValidationException("No json file");
        }
        return jsonObject;
    }
}
