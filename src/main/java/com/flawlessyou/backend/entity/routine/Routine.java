package com.flawlessyou.backend.entity.routine;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Routine {
    private String routineId;
    private String name;
    private String userId;
    private Set<String> productIds;
    private List<LocalDate> applicationTimes;
    private String description;

}