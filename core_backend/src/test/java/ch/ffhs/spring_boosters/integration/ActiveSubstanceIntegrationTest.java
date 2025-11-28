package ch.ffhs.spring_boosters.integration;

import ch.ffhs.spring_boosters.controller.ActiveSubstanceController;
import ch.ffhs.spring_boosters.controller.mapper.ActiveSubstanceMapper;
import ch.ffhs.spring_boosters.repository.ActiveSubstanceRepository;
import ch.ffhs.spring_boosters.service.ActiveSubstanceService;
import ch.ffhs.spring_boosters.test.TestFlywayInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(initializers = TestFlywayInitializer.class)
@ActiveProfiles("integrationtest")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ActiveSubstanceIntegrationTest {


    @Autowired
    private ActiveSubstanceController activeSubstanceController;

    @Autowired
    private ActiveSubstanceRepository activeSubstanceRepository;

    @Autowired
    private ActiveSubstanceService activeSubstanceService;

    @Autowired
    private ActiveSubstanceMapper activeSubstanceMapper;

    @Test
    void testKaan() {
        System.out.println("hi");

        assert activeSubstanceController != null;
        assert activeSubstanceRepository != null;
        assert activeSubstanceService != null;
        assert activeSubstanceMapper != null;
    }
}