package com.guilinares.clinikai.presentation.controllers.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.JsonNode;

@Getter
@Setter
@NoArgsConstructor
public class PublishFlowRequest {
    @NotNull
    private JsonNode flow;
}
