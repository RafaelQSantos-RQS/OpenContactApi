package br.com.personal.opencontact.api.agenda;

import br.com.personal.opencontact.api.agenda.dto.AgendaCreateDTO;
import br.com.personal.opencontact.api.agenda.dto.AgendaUpdateDTO;
import br.com.personal.opencontact.api.common.exceptions.AgendaNameAlreadyExistsException;
import br.com.personal.opencontact.api.contact.ContactRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AgendaServiceTest {

    @Mock
    private AgendaRepository agendaRepository;
    @Mock
    private ContactRepository contactRepository;
    @InjectMocks
    private AgendaService agendaService;

    @Test
    @DisplayName("Create should save agenda when name is unique")
    void create_shouldSaveAgenda_whenNameIsUnique() {
        var createDTO = new AgendaCreateDTO("Familia");
        var generatedId = UUID.randomUUID();

        when(agendaRepository.existsByNameIgnoreCase(createDTO.name())).thenReturn(false);
        when(agendaRepository.save(any(Agenda.class))).thenAnswer(invocation -> {
            Agenda agendaToSave = invocation.getArgument(0);
            agendaToSave.setId(generatedId);
            return agendaToSave;
        });

        Agenda savedAgenda = agendaService.create(createDTO);

        assertThat(savedAgenda).isNotNull();
        assertThat(savedAgenda.getId()).isEqualTo(generatedId);
        assertThat(savedAgenda.getName()).isEqualTo("Familia");

        verify(agendaRepository).existsByNameIgnoreCase("Familia");
        verify(agendaRepository).save(any(Agenda.class));
    }

    @Test
    @DisplayName("create should throw exception when name already exists")
    void create_shouldThrowException_whenNameAlreadyExists() {
        var createDTO = new AgendaCreateDTO("Familia");
        when(agendaRepository.existsByNameIgnoreCase(createDTO.name())).thenReturn(true);

        assertThatThrownBy(() -> agendaService.create(createDTO))
                .isInstanceOf(AgendaNameAlreadyExistsException.class)
                .hasMessage("Agenda name 'Familia' already exists.");

        verify(agendaRepository).existsByNameIgnoreCase("Familia");
        verify(agendaRepository, never()).save(any(Agenda.class));
    }

    @Test
    @DisplayName("findById should return agenda when id exists")
    void findById_shouldReturnAgenda_whenIdExists() {
        var agendaId = UUID.randomUUID();
        var existingAgenda = new Agenda();
        existingAgenda.setId(agendaId);
        existingAgenda.setName("Trabalho");

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(existingAgenda));

        Agenda foundAgenda = agendaService.findById(agendaId);

        assertThat(foundAgenda).isNotNull();
        assertThat(foundAgenda.getId()).isEqualTo(agendaId);
        assertThat(foundAgenda.getName()).isEqualTo("Trabalho");
        verify(agendaRepository).findById(agendaId);
    }

    @Test
    @DisplayName("findById should throw exception when id does not exist")
    void findById_shouldThrowException_whenIdDoesNotExist() {
        var nonExistentId = UUID.randomUUID();
        when(agendaRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> agendaService.findById(nonExistentId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Agenda not found with id: " + nonExistentId);

        verify(agendaRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("update should change agenda name when successful")
    void update_shouldChangeAgendaName_whenSuccessful() {
        var agendaId = UUID.randomUUID();
        var updateDTO = new AgendaUpdateDTO("Trabalho Novo");

        var originalAgenda = new Agenda("Trabalho");
        originalAgenda.setId(agendaId);

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(originalAgenda));
        when(agendaRepository.findByNameIgnoreCase("Trabalho Novo")).thenReturn(Optional.empty());
        when(agendaRepository.save(any(Agenda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Agenda updatedAgenda = agendaService.update(agendaId, updateDTO);

        assertThat(updatedAgenda).isNotNull();
        assertThat(updatedAgenda.getId()).isEqualTo(agendaId);
        assertThat(updatedAgenda.getName()).isEqualTo("Trabalho Novo");
        verify(agendaRepository).findById(agendaId);
        verify(agendaRepository).findByNameIgnoreCase("Trabalho Novo");
        verify(agendaRepository).save(originalAgenda);
    }

    @Test
    @DisplayName("update should throw exception when new name already exists in another agenda")
    void update_shouldThrowException_whenNewNameExistsInAnotherAgenda() {
        var agendaToUpdateId = UUID.randomUUID();
        var anotherAgendaId = UUID.randomUUID();
        var updateDTO = new AgendaUpdateDTO("Amigos");

        var agendaToUpdate = new Agenda("Trabalho");
        agendaToUpdate.setId(agendaToUpdateId);

        var existingAgendaWithSameName = new Agenda("Amigos");
        existingAgendaWithSameName.setId(anotherAgendaId);

        when(agendaRepository.findById(agendaToUpdateId)).thenReturn(Optional.of(agendaToUpdate));
        when(agendaRepository.findByNameIgnoreCase("Amigos")).thenReturn(Optional.of(existingAgendaWithSameName));

        assertThatThrownBy(() -> agendaService.update(agendaToUpdateId, updateDTO))
                .isInstanceOf(AgendaNameAlreadyExistsException.class)
                .hasMessage("Agenda name 'Amigos' already exists.");

        verify(agendaRepository).findById(agendaToUpdateId);
        verify(agendaRepository).findByNameIgnoreCase("Amigos");
        verify(agendaRepository, never()).save(any(Agenda.class));
    }

    @Test
    @DisplayName("delete should remove agenda when it has no associated contacts")
    void delete_shouldRemoveAgenda_whenItHasNoAssociatedContacts() {
        var agendaId = UUID.randomUUID();

        when(agendaRepository.existsById(agendaId)).thenReturn(true);
        when(contactRepository.existsByAgendaId(agendaId)).thenReturn(false);
        doNothing().when(agendaRepository).deleteById(agendaId);

        agendaService.delete(agendaId);

        verify(agendaRepository).existsById(agendaId);
        verify(contactRepository).existsByAgendaId(agendaId);
        verify(agendaRepository).deleteById(agendaId);
    }

    @Test
    @DisplayName("delete should throw exception when agenda has associated contacts")
    void delete_shouldThrowException_whenAgendaHasAssociatedContacts() {
        var agendaId = UUID.randomUUID();
        when(agendaRepository.existsById(agendaId)).thenReturn(true);
        when(contactRepository.existsByAgendaId(agendaId)).thenReturn(true);

        assertThatThrownBy(() -> agendaService.delete(agendaId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot delete agenda with associated contacts.");

        verify(agendaRepository).existsById(agendaId);
        verify(contactRepository).existsByAgendaId(agendaId);
        verify(agendaRepository, never()).deleteById(any(UUID.class));
    }

}
