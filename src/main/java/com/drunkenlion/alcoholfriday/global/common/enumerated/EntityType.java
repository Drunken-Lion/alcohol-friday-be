package com.drunkenlion.alcoholfriday.global.common.enumerated;

import lombok.Getter;

@Getter
public enum EntityType {
    ITEM("item");

    private final String entityName;
    EntityType(String entityName) {
        this.entityName = entityName;
    }
}
