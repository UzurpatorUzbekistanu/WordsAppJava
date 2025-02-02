package com.bkleszcz.WordApp.model;

import lombok.Data;

import java.util.List;

@Data
public class WordResponse {
    private String word;
    private List<String> synonyms;
}
