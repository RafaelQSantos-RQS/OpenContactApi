package br.com.personal.opencontact.api.contact;

import br.com.personal.opencontact.api.agenda.Agenda;
import br.com.personal.opencontact.api.agenda.AgendaRepository;
import br.com.personal.opencontact.api.agenda.AgendaService;
import br.com.personal.opencontact.api.common.exceptions.PhoneAlreadyExistsException;
import br.com.personal.opencontact.api.contact.dto.ContactCreateDTO;
import br.com.personal.opencontact.api.contact.dto.ContactUpdateDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final AgendaService agendaService;
    private final AgendaRepository agendaRepository;

    @Transactional
    public Contact create(UUID agendaId, ContactCreateDTO createDTO) {
        // Verify if the phone number already exists in the agenda
        boolean phoneExists = contactRepository.existsByAgendaIdAndAreaCodeAndPhoneNumber(
                agendaId,
                createDTO.areaCode(),
                createDTO.phoneNumber()
        );

        if (phoneExists) {
            throw new PhoneAlreadyExistsException("Phone number already registered in this agenda.");
        }

        // Verify if the agenda exists
        Agenda agenda = agendaRepository.findById(agendaId)
                .orElseThrow(() -> new EntityNotFoundException("Agenda not found with id: " + agendaId));

        // Create the contact
        Contact newContact = new Contact(
                createDTO.name(),
                createDTO.type(),
                createDTO.areaCode(),
                createDTO.phoneNumber(),
                agenda
        );

        return contactRepository.save(newContact);
    }

    @Transactional(readOnly = true)
    public Contact findById(UUID id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contact not found with id: " + id));
    }

    @Transactional
    public Contact update(UUID id, ContactUpdateDTO updateDTO) {
        Contact contact = findById(id);

        contactRepository.findByAgendaIdAndAreaCodeAndPhoneNumber(
                contact.getAgenda().getId(), updateDTO.areaCode(), updateDTO.phoneNumber()
        ).ifPresent(existingContact -> {
            if (!existingContact.getId().equals(id)) {
                throw new PhoneAlreadyExistsException("Phone number already registered to another contact in this agenda.");
            }
        });

        contact.updateInfo(updateDTO.name(), updateDTO.type(), updateDTO.areaCode(), updateDTO.phoneNumber());
        return contactRepository.save(contact);
    }

    @Transactional
    public void delete(UUID id) {
        if (!contactRepository.existsById(id)) {
            throw new EntityNotFoundException("Contact not found with id: " + id);
        }
        contactRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<Contact> findAllByCriteria(UUID agendaId, String nameContains, String phoneContains, Pageable pageable) {
        if (!agendaRepository.existsById(agendaId)) {
            throw new EntityNotFoundException("Agenda not found with id: " + agendaId);
        }

        Specification<Contact> spec = ContactSpecification.filterBy(agendaId, nameContains, phoneContains);
        return contactRepository.findAll(spec, pageable);
    }

    @Transactional
    public void deleteContactsByNamePrefix(UUID agendaId, String namePrefix) {
        agendaService.findById(agendaId);
        contactRepository.deleteByAgendaIdAndNameStartingWithIgnoreCase(agendaId, namePrefix);
    }
}
