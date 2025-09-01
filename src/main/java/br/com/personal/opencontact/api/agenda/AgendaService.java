package br.com.personal.opencontact.api.agenda;

import br.com.personal.opencontact.api.agenda.dto.AgendaCreateDTO;
import br.com.personal.opencontact.api.agenda.dto.AgendaUpdateDTO;
import br.com.personal.opencontact.api.contact.ContactRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final ContactRepository contactRepository;

    @Transactional
    public Agenda create(AgendaCreateDTO createDTO) {
        // TODO: Validate if the agenda name already exists

        Agenda newAgenda = new Agenda(createDTO.name());

        return agendaRepository.save(newAgenda);
    }

    @Transactional(readOnly = true)
    public Agenda findById(UUID id) {
        return agendaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agenda not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Agenda> findAll(Pageable pageable) {
        return agendaRepository.findAll(pageable);
    }

    @Transactional
    public Agenda update(UUID id, AgendaUpdateDTO updateDTO) {
        Agenda agenda = findById(id);
        agenda.setName(updateDTO.name());
        return agendaRepository.save(agenda);
    }

    @Transactional
    public void delete(UUID id) {
        if (!agendaRepository.existsById(id)) {
            throw new EntityNotFoundException("Agenda not found with id: " + id);
        }
        // Business rule: Cannot delete agenda with associated contacts
        if (contactRepository.existsByAgendaId(id)) {
            throw new IllegalStateException("Cannot delete agenda with associated contacts.");
        }
        agendaRepository.deleteById(id);
    }
}
