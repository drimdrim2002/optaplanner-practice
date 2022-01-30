package org.optaplanner.examples.baseball.score;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.examples.baseball.domain.BaseballSolution;
import org.optaplanner.examples.baseball.domain.Match;

public class BaseballEasyScoreCalculator implements EasyScoreCalculator<BaseballSolution, HardSoftScore> {

    @Override
    public HardSoftScore calculateScore(BaseballSolution baseballSolution) {
        int hardScore = 0;
        int shortPenalty = 0;

        for (Match match : baseballSolution.getMatchList()) {

            if (match.getCalendar() == null) {
                shortPenalty -= match.getConsecutive();
                continue;
            }

            // 경기수와 period는 일치해야 한다
            if (match.getConsecutive() != match.getCalendar().getConsecutive()) {
                hardScore -= 1;
            }


        }
        return HardSoftScore.of(hardScore, shortPenalty);
    }
}
