package com.openluat.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ScoreAim {
    private int fullScoreHours = 25 * 12;
    private int fullScoreMinutes = fullScoreHours * 60;

}
