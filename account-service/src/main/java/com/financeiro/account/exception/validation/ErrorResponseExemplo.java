package com.financeiro.account.exception.validation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ErrorResponseExemplo {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Integer statusCode;
    private String timestamp;
    private List<String> violations;


}
