package br.com.personal.opencontact.api.contact.dto;

import br.com.personal.opencontact.api.contact.Contact;
import br.com.personal.opencontact.api.contact.ContactType;

import java.time.LocalDateTime;
import java.util.UUID;

public record ContactResponseDTO(
        UUID id,
        String name,
        ContactType type,
        String areaCode,
        String phoneNumber,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    // Static factory method para mapeamento
    public static ContactResponseDTO fromEntity(Contact contact) {
        return new ContactResponseDTO(
                contact.getId(),
                contact.getName(),
                contact.getType(),
                contact.getAreaCode(),
                contact.getPhoneNumber(),
                contact.getCreatedAt(),
                contact.getUpdatedAt()
        );
    }
}
