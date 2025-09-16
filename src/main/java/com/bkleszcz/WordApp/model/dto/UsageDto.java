package com.bkleszcz.WordApp.model.dto;                       // pakiet DTO

import lombok.AllArgsConstructor;                             // generuje konstruktor
import lombok.Data;                                           // generuje get/set/toString

@Data                                                         // adnotacja Lombok
@AllArgsConstructor                                           // konstruktor z wszystkimi polami
public class UsageDto {                                       // DTO odpowiedzi
    private String day;                                         // dzień (yyyy-MM-dd)
    private int used;                                           // ile wykorzystano
    private int limit;                                          // limit dzienny
    private int remaining;                                      // ile zostało
}
