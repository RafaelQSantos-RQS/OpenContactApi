package br.com.personal.opencontact.api.agenda;

import br.com.personal.opencontact.api.agenda.dto.AgendaCreateDTO;
import br.com.personal.opencontact.api.agenda.dto.AgendaResponseDTO;
import br.com.personal.opencontact.api.agenda.dto.AgendaUpdateDTO;
import br.com.personal.opencontact.api.common.dto.PageResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/agendas")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;

    @PostMapping
    public ResponseEntity<AgendaResponseDTO> create(@RequestBody @Valid AgendaCreateDTO createDTO) {
        Agenda newAgenda = agendaService.create(createDTO);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newAgenda.getId())
                .toUri();

        return ResponseEntity.created(location).body(AgendaResponseDTO.fromEntity(newAgenda));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendaResponseDTO> findById(@PathVariable UUID id) {
        Agenda agenda = agendaService.findById(id);
        return ResponseEntity.ok(AgendaResponseDTO.fromEntity(agenda));
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<AgendaResponseDTO>> findAll(@PageableDefault(size = 10, sort = "name") Pageable pageable) {
        Page<Agenda> agendasPage = agendaService.findAll(pageable);

        Page<AgendaResponseDTO> dtosPage = agendasPage.map(AgendaResponseDTO::fromEntity);

        PageResponseDTO<AgendaResponseDTO> response = PageResponseDTO.fromPage(dtosPage);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgendaResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid AgendaUpdateDTO updateDTO) {
        Agenda updatedAgenda = agendaService.update(id, updateDTO);
        return ResponseEntity.ok(AgendaResponseDTO.fromEntity(updatedAgenda));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        agendaService.delete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
