package ch.ffhs.spring_boosters.controller;

import ch.ffhs.spring_boosters.config.JwtTokenReader;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationScheduleDto;
import ch.ffhs.spring_boosters.controller.exception.GlobalExceptionHandler;
import ch.ffhs.spring_boosters.service.Exception.UserNotFoundException;
import ch.ffhs.spring_boosters.service.ImmunizationScheduleService;
import ch.ffhs.spring_boosters.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ImmunizationScheduleControllerTest {

    @Mock
    private ImmunizationScheduleService immunizationScheduleService;

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenReader jwtTokenReader;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ImmunizationScheduleController controller = new ImmunizationScheduleController(immunizationScheduleService, userService, jwtTokenReader);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private ImmunizationScheduleDto sampleSchedule(UUID userId, String username, int currentAgeDays, int totalPending, int high, int medium, int low) {
        return new ImmunizationScheduleDto(userId, username, LocalDate.now(), currentAgeDays, List.of(), totalPending, high, medium, low);
    }

    @Test
    void getOwnPendingImmunizations_success() throws Exception {
        UUID userId = UUID.randomUUID();
        String tokenHeader = "Bearer token123";
        String token = "token123";
        String username = "alice";

        ImmunizationScheduleDto schedule = sampleSchedule(userId, username, 100, 5, 2, 2, 1);

        when(jwtTokenReader.getUsername(token)).thenReturn(username);
        when(userService.findByUsername(username)).thenReturn(new ch.ffhs.spring_boosters.controller.entity.User() {{ setId(userId); }});
        when(immunizationScheduleService.getPendingImmunizations(userId)).thenReturn(schedule);

        mockMvc.perform(get("/api/v1/immunization-schedule/pending").header("Authorization", tokenHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(username)))
                .andExpect(jsonPath("$.totalPending", is(5)));
    }

    @Test
    void getOwnPendingImmunizations_userNotFound_returnsNotFound() throws Exception {
        String tokenHeader = "Bearer token123";
        String token = "token123";
        String username = "bob";

        when(jwtTokenReader.getUsername(token)).thenReturn(username);
        when(userService.findByUsername(username)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/immunization-schedule/pending").header("Authorization", tokenHeader))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void getOwnPendingImmunizationsSummary_success() throws Exception {
        UUID userId = UUID.randomUUID();
        String tokenHeader = "Bearer tokenXYZ";
        String token = "tokenXYZ";
        String username = "carol";

        ImmunizationScheduleDto schedule = sampleSchedule(userId, username, 200, 8, 3, 3, 2);

        when(jwtTokenReader.getUsername(token)).thenReturn(username);
        when(userService.findByUsername(username)).thenReturn(new ch.ffhs.spring_boosters.controller.entity.User() {{ setId(userId); }});
        when(immunizationScheduleService.getPendingImmunizations(userId)).thenReturn(schedule);

        mockMvc.perform(get("/api/v1/immunization-schedule/pending/summary").header("Authorization", tokenHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(username)))
                .andExpect(jsonPath("$.totalPending", is(8)))
                .andExpect(jsonPath("$.currentAgeDays", is(200)));
    }

    @Test
    void getOwnPendingImmunizationsSummary_userNotFound_returnsNotFound() throws Exception {
        String tokenHeader = "Bearer tokenXYZ";
        String token = "tokenXYZ";
        String username = "dave";

        when(jwtTokenReader.getUsername(token)).thenReturn(username);
        when(userService.findByUsername(username)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/immunization-schedule/pending/summary").header("Authorization", tokenHeader))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }
}
