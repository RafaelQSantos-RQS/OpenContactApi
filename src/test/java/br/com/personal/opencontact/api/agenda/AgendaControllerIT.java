package br.com.personal.opencontact.api.agenda;

import br.com.personal.opencontact.api.AbstractIntegrationTest;
import br.com.personal.opencontact.api.agenda.dto.AgendaCreateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Transactional
class AgendaControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AgendaRepository agendaRepository;

    @Test
    @DisplayName("POST /agendas should create an agenda and return 201 Created")
    void postAgenda_whenValidInput_shouldReturn201AndCreateAgenda() throws Exception {
        var createDTO = new AgendaCreateDTO("Familia");
        String jsonPayload = objectMapper.writeValueAsString(createDTO);

        mockMvc.perform(post("/agendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", matchesPattern("^http://localhost/agendas/[a-f0-9\\-]+$")))
                .andExpect(jsonPath("$.name").value("Familia"));

        List<Agenda> agendas = agendaRepository.findAll();
        assertThat(agendas).hasSize(1);
        assertThat(agendas.getFirst().getName()).isEqualTo("Familia");
    }
}