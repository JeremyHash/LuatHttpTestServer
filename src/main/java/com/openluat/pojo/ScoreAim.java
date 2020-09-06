package com.openluat.pojo;

import lombok.Data;
import org.springframework.stereotype.Repository;

@Data
@Repository
public class ScoreAim {
    private int jigeScoreHours = 276;
    private int fullScoreHours = 324;
    private int jigeScoreMinutes = 16560;
    private int fullScoreMinutes = 19440;

}
