package br.com.personal.opencontact.api.contact;

import br.com.personal.opencontact.api.common.dto.PageResponseDTO;
import br.com.personal.opencontact.api.contact.dto.ContactCreateDTO;
import br.com.personal.opencontact.api.contact.dto.ContactResponseDTO;
import br.com.personal.opencontact.api.contact.dto.ContactUpdateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping("/agendas/{agendaId}/contacts")
    public ResponseEntity<ContactResponseDTO> create(
            @PathVariable UUID agendaId,
            @RequestBody @Valid ContactCreateDTO createDTO
    ) {
        Contact newContact = contactService.create(agendaId, createDTO);

        URI location = ServletUriComponentsBuilder.fromPath("/contacts/{id}")
                .buildAndExpand(newContact.getId())
                .toUri();

        return ResponseEntity.created(location).body(ContactResponseDTO.fromEntity(newContact));
    }

    @GetMapping("/contacts/{id}")
    public ResponseEntity<ContactResponseDTO> findById(@PathVariable UUID id) {
        Contact contact = contactService.findById(id);
        return ResponseEntity.ok(ContactResponseDTO.fromEntity(contact));
    }

    @GetMapping("/agendas/{agendaId}/contacts")
    public ResponseEntity<PageResponseDTO<ContactResponseDTO>> findAllByCriteria(
            @PathVariable UUID agendaId,
            @RequestParam(required = false) String nameContains,
            @RequestParam(required = false) String phoneContains,
            @PageableDefault(size = 10, sort = "name") Pageable pageable
    ) {
        Page<Contact> contactsPage = contactService.findAllByCriteria(agendaId, nameContains, phoneContains, pageable);
        Page<ContactResponseDTO> dtosPage = contactsPage.map(ContactResponseDTO::fromEntity);
        PageResponseDTO<ContactResponseDTO> response = PageResponseDTO.fromPage(dtosPage);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/contacts/{id}")
    public ResponseEntity<ContactResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid ContactUpdateDTO updateDTO) {
        Contact updatedContact = contactService.update(id, updateDTO);
        return ResponseEntity.ok(ContactResponseDTO.fromEntity(updatedContact));
    }

    @DeleteMapping("/contacts/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        contactService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
