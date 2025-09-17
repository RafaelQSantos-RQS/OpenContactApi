package br.com.personal.opencontact.api.contact;

import br.com.personal.opencontact.api.AbstractIntegrationTest;
import br.com.personal.opencontact.api.agenda.Agenda;
import br.com.personal.opencontact.api.agenda.AgendaRepository;
import br.com.personal.opencontact.api.contact.dto.ContactCreateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
class ContactControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AgendaRepository agendaRepository;

    @Autowired
    private ContactRepository contactRepository;

    private Agenda savedAgenda;

    @BeforeEach
    void setUp() {
        // CORREÇÃO 1: Garante um estado 100% limpo antes de cada teste.
        // A ordem é importante para respeitar as chaves estrangeiras.
        contactRepository.deleteAll();
        agendaRepository.deleteAll();

        // Agora, cria a agenda de teste em um banco de dados limpo.
        Agenda agenda = new Agenda("Trabalho");
        savedAgenda = agendaRepository.save(agenda);
    }

    @Test
    @DisplayName("POST /agendas/{agendaId}/contacts should create a contact and return 201 Created")
    void postContact_whenValidInput_shouldReturn201AndCreateContact() throws Exception {
        // Arrange
        var createDTO = new ContactCreateDTO("Marco", ContactType.MOBILE, "11", "987654321");
        String jsonPayload = objectMapper.writeValueAsString(createDTO);

        // Act & Assert - HTTP Level
        mockMvc.perform(post("/agendas/{agendaId}/contacts", savedAgenda.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Marco"));

        // Assert - Database Level (Agora a asserção funcionará)
        var contacts = contactRepository.findAll();
        assertThat(contacts).hasSize(1);
        Contact persistedContact = contacts.get(0);
        assertThat(persistedContact.getName()).isEqualTo("Marco");
        assertThat(persistedContact.getAgenda().getId()).isEqualTo(savedAgenda.getId());
    }

    @Test
    @DisplayName("POST /agendas/{agendaId}/contacts should return 409 when phone already exists in agenda")
    void postContact_whenPhoneExists_shouldReturn409Conflict() throws Exception {
        // Arrange
        Contact existingContact = new Contact("Contato Antigo", ContactType.FIXED_LINE, "11", "987654321", savedAgenda);
        contactRepository.save(existingContact);

        var createDTO = new ContactCreateDTO("Contato Novo", ContactType.MOBILE, "11", "987654321");
        String jsonPayload = objectMapper.writeValueAsString(createDTO);

        // Act & Assert
        mockMvc.perform(post("/agendas/{agendaId}/contacts", savedAgenda.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                // CORREÇÃO 2: Espera o status 409 Conflict.
                .andExpect(status().isConflict());

        // Garante que nenhum contato novo foi criado
        assertThat(contactRepository.findAll()).hasSize(1);
    }
}