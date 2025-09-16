package com.bkleszcz.WordApp.model.dto;          // pakiet DTO

import lombok.*;                                  // gettery/settery/builder

@Data                                             // get/set/toString
@NoArgsConstructor                                // pusty ctor
@AllArgsConstructor                               // pełny ctor
@Builder                                          // builder
public class LevelInfoDto {                       // DTO poziomu/progresu
    private int levelNumber;                        // numer aktualnego poziomu
    private int currentExperience;                  // XP uzbierane NA BIEŻĄCY poziom
    private int nextLevelExperience;                // ile XP potrzeba do KOLEJNEGO poziomu
    private boolean levelUp;                        // czy właśnie awansował (popup)
}