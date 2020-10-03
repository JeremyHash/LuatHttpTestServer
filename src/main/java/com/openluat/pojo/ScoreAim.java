package com.openluat.pojo;

import lombok.Data;
import org.springframework.stereotype.Repository;

@Data
@Repository
public class ScoreAim {
    private int jigeScoreHours = 17 * 12;
    private int fullScoreHours = 20 * 12;
    private int jigeScoreMinutes = jigeScoreHours * 60;
    private int fullScoreMinutes = fullScoreHours * 60;

}
