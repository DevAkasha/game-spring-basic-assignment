package com.gamebasic.runcard.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class RunCardRequest {
    @NotBlank @Size(min =1)
    private String cardType;
    @NotNull @Min(0) @Max(10)
    private Integer acquiredFloor;
}
