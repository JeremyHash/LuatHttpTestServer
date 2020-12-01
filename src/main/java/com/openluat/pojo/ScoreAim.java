package com.openluat.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Data
@Component
public class ScoreAim {
    private int jigeScoreHours = 23 * 12;
    private int fullScoreHours = 29 * 12;
    private int jigeScoreMinutes = jigeScoreHours * 60;
    private int fullScoreMinutes = fullScoreHours * 60;

}
