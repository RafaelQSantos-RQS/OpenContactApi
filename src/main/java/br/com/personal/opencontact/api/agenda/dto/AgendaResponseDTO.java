package br.com.personal.opencontact.api.agenda.dto;

import br.com.personal.opencontact.api.agenda.Agenda;

import java.time.LocalDateTime;
import java.util.UUID;

public record AgendaResponseDTO(
        UUID id,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AgendaResponseDTO fromEntity(Agenda agenda) {
        return new AgendaResponseDTO(
                agenda.getId(),
                agenda.getName(),
                agenda.getCreatedAt(),
                agenda.getUpdatedAt()
        );
    }
}
