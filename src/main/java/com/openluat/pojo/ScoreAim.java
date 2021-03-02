package com.openluat.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Data
@Component
public class ScoreAim {
    private int fullScoreHours = 27 * 12;
    private int fullScoreMinutes = fullScoreHours * 60;

}
