package br.com.personal.opencontact.api.contact;

import br.com.personal.opencontact.api.agenda.Agenda;
import br.com.personal.opencontact.api.agenda.AgendaRepository;
import br.com.personal.opencontact.api.common.exceptions.PhoneAlreadyExistsException;
import br.com.personal.opencontact.api.contact.dto.ContactCreateDTO;
import br.com.personal.opencontact.api.contact.dto.ContactUpdateDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private AgendaRepository agendaRepository;

    @InjectMocks
    private ContactService contactService;

    @Test
    @DisplayName("create should save contact when phone is unique and agenda exists")
    void create_shouldSaveContact_whenSuccessful() {
        // Arrange
        var agendaId = UUID.randomUUID();
        var createDTO = new ContactCreateDTO("John Doe", ContactType.MOBILE, "11", "987654321");
        var agenda = new Agenda("Familia");
        agenda.setId(agendaId);
        var generatedId = UUID.randomUUID();

        when(contactRepository.existsByAgendaIdAndAreaCodeAndPhoneNumber(agendaId, "11", "987654321")).thenReturn(false);
        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(contactRepository.save(any(Contact.class))).thenAnswer(invocation -> {
            Contact contact = invocation.getArgument(0);
            contact.setId(generatedId);
            return contact;
        });

        // Act
        Contact savedContact = contactService.create(agendaId, createDTO);

        // Assert
        assertThat(savedContact).isNotNull();
        assertThat(savedContact.getId()).isEqualTo(generatedId);
        assertThat(savedContact.getName()).isEqualTo("John Doe");
        assertThat(savedContact.getAreaCode()).isEqualTo("11");
        assertThat(savedContact.getPhoneNumber()).isEqualTo("987654321");
        assertThat(savedContact.getAgenda().getId()).isEqualTo(agendaId);
        verify(contactRepository).existsByAgendaIdAndAreaCodeAndPhoneNumber(agendaId, "11", "987654321");
        verify(agendaRepository).findById(agendaId);
        verify(contactRepository).save(any(Contact.class));
    }

    @Test
    @DisplayName("create should throw exception when phone already exists in agenda")
    void create_shouldThrowException_whenPhoneAlreadyExists() {
        // Arrange
        var agendaId = UUID.randomUUID();
        var createDTO = new ContactCreateDTO("John Doe", ContactType.MOBILE, "11", "987654321");
        when(contactRepository.existsByAgendaIdAndAreaCodeAndPhoneNumber(agendaId, "11", "987654321")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> contactService.create(agendaId, createDTO))
                .isInstanceOf(PhoneAlreadyExistsException.class)
                .hasMessage("Phone number already registered in this agenda.");

        verify(agendaRepository, never()).findById(any());
        verify(contactRepository, never()).save(any());
    }

    @Test
    @DisplayName("create should throw exception when agenda does not exist")
    void create_shouldThrowException_whenAgendaNotFound() {
        // Arrange
        var agendaId = UUID.randomUUID();
        var createDTO = new ContactCreateDTO("John Doe", ContactType.MOBILE, "11", "987654321");
        when(contactRepository.existsByAgendaIdAndAreaCodeAndPhoneNumber(agendaId, "11", "987654321")).thenReturn(false);
        when(agendaRepository.findById(agendaId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> contactService.create(agendaId, createDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Agenda not found with id: " + agendaId);

        verify(contactRepository, never()).save(any());
    }

    @Test
    @DisplayName("update should change contact details when successful")
    void update_shouldChangeContactDetails_whenSuccessful() {
        // Arrange
        var agendaId = UUID.randomUUID();
        var contactId = UUID.randomUUID();
        var updateDTO = new ContactUpdateDTO("Jane Doe Updated", ContactType.FAX, "21", "23456789");

        var agenda = new Agenda("Trabalho");
        agenda.setId(agendaId);
        var originalContact = new Contact("Jane Doe", ContactType.MOBILE, "11", "12345678", agenda);
        originalContact.setId(contactId);

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(originalContact));
        when(contactRepository.findByAgendaIdAndAreaCodeAndPhoneNumber(agendaId, "21", "23456789")).thenReturn(Optional.empty());
        when(contactRepository.save(any(Contact.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Contact updatedContact = contactService.update(contactId, updateDTO);

        // Assert
        assertThat(updatedContact).isNotNull();
        assertThat(updatedContact.getId()).isEqualTo(contactId);
        assertThat(updatedContact.getName()).isEqualTo("Jane Doe Updated");
        assertThat(updatedContact.getAreaCode()).isEqualTo("21");
        assertThat(updatedContact.getPhoneNumber()).isEqualTo("23456789");
        verify(contactRepository).findById(contactId);
        verify(contactRepository).findByAgendaIdAndAreaCodeAndPhoneNumber(agendaId, "21", "23456789");
        verify(contactRepository).save(originalContact);
    }

    @Test
    @DisplayName("update should throw exception when phone belongs to another contact")
    void update_shouldThrowException_whenPhoneBelongsToAnotherContact() {
        // Arrange
        var agendaId = UUID.randomUUID();
        var contactToUpdateId = UUID.randomUUID();
        var otherContactId = UUID.randomUUID();
        var updateDTO = new ContactUpdateDTO("Jane Doe Updated", ContactType.FAX, "11", "99998888");

        var agenda = new Agenda("Trabalho");
        agenda.setId(agendaId);
        var contactToUpdate = new Contact("Jane Doe", ContactType.MOBILE, "11", "12345678", agenda);
        contactToUpdate.setId(contactToUpdateId);

        var otherContactWithPhone = new Contact("Other Guy", ContactType.MOBILE, "11", "99998888", agenda);
        otherContactWithPhone.setId(otherContactId);

        when(contactRepository.findById(contactToUpdateId)).thenReturn(Optional.of(contactToUpdate));
        when(contactRepository.findByAgendaIdAndAreaCodeAndPhoneNumber(agendaId, "11", "99998888")).thenReturn(Optional.of(otherContactWithPhone));

        // Act & Assert
        assertThatThrownBy(() -> contactService.update(contactToUpdateId, updateDTO))
                .isInstanceOf(PhoneAlreadyExistsException.class)
                .hasMessage("Phone number already registered to another contact in this agenda.");

        verify(contactRepository, never()).save(any());
    }

    @Test
    @DisplayName("findById should return contact when id exists")
    void findById_shouldReturnContact_whenIdExists() {
        // Arrange
        var contactId = UUID.randomUUID();
        var contact = new Contact();
        contact.setId(contactId);
        contact.setName("Jane Doe");

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

        // Act
        Contact foundContact = contactService.findById(contactId);

        // Assert
        assertThat(foundContact).isNotNull();
        assertThat(foundContact.getId()).isEqualTo(contactId);
        verify(contactRepository).findById(contactId);
    }

    @Test
    @DisplayName("delete should remove contact when id exists")
    void delete_shouldRemoveContact_whenIdExists() {
        // Arrange
        var contactId = UUID.randomUUID();
        when(contactRepository.existsById(contactId)).thenReturn(true);
        doNothing().when(contactRepository).deleteById(contactId);

        // Act
        contactService.delete(contactId);

        // Assert
        verify(contactRepository).existsById(contactId);
        verify(contactRepository).deleteById(contactId);
    }

    @Test
    @DisplayName("delete should throw exception when id does not exist")
    void delete_shouldThrowException_whenIdDoesNotExist() {
        // Arrange
        var contactId = UUID.randomUUID();
        when(contactRepository.existsById(contactId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> contactService.delete(contactId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Contact not found with id: " + contactId);

        verify(contactRepository, never()).deleteById(any(UUID.class));
    }
}