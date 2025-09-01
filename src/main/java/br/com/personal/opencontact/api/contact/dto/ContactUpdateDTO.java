package br.com.personal.opencontact.api.contact.dto;

import br.com.personal.opencontact.api.contact.ContactType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ContactUpdateDTO(
        @NotBlank String name,
        @NotNull ContactType type,
        @NotBlank @Pattern(regexp = "\\d{2}", message = "Area code must have 2 digits") String areaCode,
        @NotBlank @Pattern(regexp = "\\d{8,9}", message = "Phone number must have 8 or 9 digits") String phoneNumber
) {}
