package com.mohit.aiagent.model;

import jakarta.validation.constraints.NotBlank;

public record IncidentEvent(
        @NotBlank String incidentId,
        @NotBlank String service,
        @NotBlank String severity,
        @NotBlank String message
) {}
