package org.optaplanner.examples.baseball.app;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import java.io.*;
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

        try {
            // 데이터 읽기
            JSONObject jsonObject = readJsonFile();


            // solution 만들기
            BaseballSolution unsolvedSolution = makeSolution(jsonObject);

            // initial 만들기
            setInitialPlan(jsonObject, unsolvedSolution);

            exportResult(unsolvedSolution, false);


            // solving
            SolverFactory<BaseballSolution> solverFactory = SolverFactory.createFromXmlResource("org/optaplanner/examples/baseball/solver/baseballSolverConfig.xml");
            Solver<BaseballSolution> solver = solverFactory.buildSolver();
            BaseballSolution solvedSolution = solver.solve(unsolvedSolution);

            // export result
            exportResult(solvedSolution, true);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }


    }

    private static void exportResult(BaseballSolution baseballSolution, boolean solved) {
        String status = solved ? "solved" : "unsolved";

        exportExcelFile(baseballSolution, status);


    }

    private static void exportExcelFile(BaseballSolution baseballSolution, String status) {

        XSSFWorkbook workbook = createWorkBook(baseballSolution);

        createExcelFile(status, workbook);
    }

    private static void createExcelFile(String status, XSSFWorkbook workbook) {
        long currentMilliseconds = System.currentTimeMillis();
        String fileName = status + "_" + String.valueOf(currentMilliseconds).substring(0, 10);
        logger.info("fileName: "+ fileName);
        File file = new File("data/baseball/" + status + "/" + fileName + ".xlsx");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            workbook.write(fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (workbook != null) workbook.close();
                if (fos != null) fos.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private static XSSFWorkbook createWorkBook(BaseballSolution baseballSolution) {

        TreeMap<LocalDateTime, List<Match>> matchResultsOrderByTime = createMatchResultsOrderByTime(baseballSolution);

        // 워크북 생성
        XSSFWorkbook workbook = new XSSFWorkbook();

        createScheduleSheet(matchResultsOrderByTime, workbook);

        createDistanceAnalysisSheet(matchResultsOrderByTime, workbook);

        HashMap<String, Integer> holidayByTeam = new HashMap<>();
        for (Match match : baseballSolution.getMatchList()) {
            Team homeTeam = match.getHome();
            Calendar calendar = match.getCalendar();
            boolean holiday = (calendar.isHoliday() || calendar.isWeekend()) ? true : false;
            if (holiday) {
                int prevQty = holidayByTeam.getOrDefault(homeTeam.getName(), 0);
                holidayByTeam.put(homeTeam.getName(), prevQty + 1);
            }
        }

        // 워크시트 생성
        XSSFSheet sheet = workbook.createSheet("holidayAnalysis");
        // 행 생성
        XSSFRow row = sheet.createRow(0);
        // 쎌 생성
        XSSFCell cell;
        // 헤더 정보 구성
        cell = row.createCell(0);
        cell.setCellValue("team");

        cell = row.createCell(1);
        cell.setCellValue("holidayCount");

        int rowIndex = 1;
        for (String team : holidayByTeam.keySet()) {
            row = sheet.createRow(rowIndex);
            // 헤더 정보 구성
            cell = row.createCell(0);
            cell.setCellValue(team);

            cell = row.createCell(1);
            cell.setCellValue(holidayByTeam.get(team));
            rowIndex++;
        }


        return workbook;
    }

    private static TreeMap<LocalDateTime, List<Match>> createMatchResultsOrderByTime(BaseballSolution baseballSolution) {
        TreeMap<LocalDateTime, List<Match>> matchResultsOrderByTime = new TreeMap<>();
        for (Match match : baseballSolution.getMatchList()) {
            LocalDateTime localDateTime = match.getCalendar().getStartTime();
            if (!matchResultsOrderByTime.containsKey(localDateTime)) {
                matchResultsOrderByTime.put(localDateTime, new ArrayList<>());
            }
            matchResultsOrderByTime.get(localDateTime).add(match);
        }
        return matchResultsOrderByTime;
    }

    private static void createDistanceAnalysisSheet(TreeMap<LocalDateTime, List<Match>> matchResultsOrderByTime, XSSFWorkbook workbook) {
        HashMap<Team, Queue<Match>> visitOrderByTeam = new HashMap<>();
        for (LocalDateTime startTIme : matchResultsOrderByTime.keySet()) {
            for (Match match : matchResultsOrderByTime.get(startTIme)) {
                if (!visitOrderByTeam.containsKey(match.getHome())) {
                    visitOrderByTeam.put(match.getHome(), new LinkedList<>());
                }
                visitOrderByTeam.get(match.getHome()).add(match);
                if (!visitOrderByTeam.containsKey(match.getAway())) {
                    visitOrderByTeam.put(match.getAway(), new LinkedList<>());
                }
                visitOrderByTeam.get(match.getAway()).add(match);
            }
        }
        // 워크시트 생성
        XSSFSheet sheet = workbook.createSheet("distanceAnalysis");
        // 행 생성
        XSSFRow row = sheet.createRow(0);
        // 쎌 생성
        XSSFCell cell;
        // 헤더 정보 구성
        cell = row.createCell(0);
        cell.setCellValue("team");

        cell = row.createCell(1);
        cell.setCellValue("home/away");

        cell = row.createCell(2);
        cell.setCellValue("opposite");

        cell = row.createCell(3);
        cell.setCellValue("distance");

        cell = row.createCell(4);
        cell.setCellValue("totalDistance");

        int rowIndex = 1;
        for (Team team : visitOrderByTeam.keySet()) {
            Queue<Match> visitOrder = visitOrderByTeam.get(team);
            BigDecimal totalDistance = BigDecimal.ZERO;
            Team prevTeam = null;
            for (Match match : visitOrder) {

                BigDecimal distanceFromPrevTeam = BigDecimal.ZERO;
                if (prevTeam != null) {
                    distanceFromPrevTeam = prevTeam.getDistanceTo(match.getHome());
                }
                totalDistance = totalDistance.add(distanceFromPrevTeam);
                prevTeam = match.getHome();

                row = sheet.createRow(rowIndex);
                cell = row.createCell(0);
                cell.setCellValue(team.getName());

                boolean homeMatch = team.equals(match.getHome());
                cell = row.createCell(1);
                cell.setCellValue(homeMatch ? "Home" : "Away");

                Team opposite;
                if (homeMatch) {
                    opposite = match.getAway();
                } else {
                    opposite = match.getHome();
                }

                cell = row.createCell(2);
                cell.setCellValue(opposite.getName());

                cell = row.createCell(3);
                cell.setCellValue(distanceFromPrevTeam.doubleValue());

                cell = row.createCell(4);
                cell.setCellValue(totalDistance.doubleValue());

                rowIndex++;
            }
        }
    }

    private static void createScheduleSheet(TreeMap<LocalDateTime, List<Match>> matchResultsOrderByTime, XSSFWorkbook workbook) {
        XSSFSheet scheduleSheet = workbook.createSheet("schedule");
        // 행 생성
        XSSFRow row = scheduleSheet.createRow(0);
        // 쎌 생성
        XSSFCell cell;

        // 헤더 정보 구성
        cell = row.createCell(0);
        cell.setCellValue("date");

        cell = row.createCell(1);
        cell.setCellValue("home");

        cell = row.createCell(2);
        cell.setCellValue("away");

        cell = row.createCell(3);
        cell.setCellValue("stadium");

        cell = row.createCell(4);
        cell.setCellValue("matches");

        cell = row.createCell(5);
        cell.setCellValue("holiday");

        int rowIndex = 1;
        for (LocalDateTime startTime : matchResultsOrderByTime.keySet()) {
            for (Match match : matchResultsOrderByTime.get(startTime)) {
                row = scheduleSheet.createRow(rowIndex);
                cell = row.createCell(0);
                cell.setCellValue(startTime.toLocalDate().toString());

//                logger.info(match.toString());
                cell = row.createCell(1);
                cell.setCellValue(match.getHome().getName());

                cell = row.createCell(2);
                cell.setCellValue(match.getAway().getName());

                cell = row.createCell(3);
                cell.setCellValue(match.getHome().getStadium());

                cell = row.createCell(4);
                cell.setCellValue(match.getConsecutive());

                cell = row.createCell(5);
                boolean holiday = match.getCalendar().isHoliday() || match.getCalendar().isWeekend() ? true : false;
                cell.setCellValue(holiday == true ? 1 : 0);

                rowIndex++;
            }
        }
    }

    private static void setInitialPlan(JSONObject jsonObject, BaseballSolution unsolvedSolution) {
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


            if (matchHashMap.get(key) == null) {
                int t = 1;
                t = 2;
            }
            Match match = matchHashMap.get(key).poll();


            Calendar calendar = calendarHashMap.get(datetime);


            match.setCalendar(calendar);

            if (initialIndex < 5) {
                match.setPinned(true);
            } else if (initialIndex >= 45 && initialIndex < 50) {
                match.setPinned(true);
            }
            initialIndex++;
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


        LocalDateTime lastDate = LocalDateTime.parse((String) "2022-12-31 00:00:00", formatter);
        Calendar dummy = new Calendar(9999, lastDate, 0, false, false);
        calendarList.add(dummy);

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

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new ValidationException("No json file");
        }
        return jsonObject;
    }
}
