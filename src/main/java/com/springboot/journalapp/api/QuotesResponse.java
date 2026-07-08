package com.springboot.journalapp.api;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuotesResponse {
    private String quote;
    private String author;
    private String work;
    private List<String> categories;
}
