package com.lcsk42.starter.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessDomainEnum implements BaseEnum<String> {

    COMMON("common", "Common Module"),
    FEIGN("feign", "Feign Module"),
    SQL("sql", "SQL Module"),
    FILE("file", "File Module"),
    ;

    private final String value;

    private final String description;
}