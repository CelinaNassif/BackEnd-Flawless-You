package com.flawlessyou.backend.entity.routine;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.sql.Timestamp;
import java.util.Map;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Routine {
    private String routineId;

    private String userId;
    private Map<String, Timestamp> productIds;
    private Timestamp timeAnalysis;
    private String description;
    private String analysisId;

}