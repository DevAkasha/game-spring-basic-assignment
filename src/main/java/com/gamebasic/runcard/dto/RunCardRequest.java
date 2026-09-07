package com.gamebasic.runcard.dto;

import lombok.Getter;

@Getter
public class RunCardRequest {
    // TODO (Lv 5): API 명세의 카드 필드 제약을 Bean Validation 어노테이션으로 붙이세요.
    private String cardType;
    private Integer acquiredFloor;
}
