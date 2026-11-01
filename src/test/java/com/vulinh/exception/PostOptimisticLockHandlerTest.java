package com.vulinh.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

class PostOptimisticLockHandlerTest {

  @Test
  void persistenceRacesReturnTheSameConflictContractAsStaleRequests() throws Exception {
    var mvc =
        MockMvcBuilders.standaloneSetup(new RaceController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    for (var path : new String[] {"/jpa-race", "/spring-race"}) {
      mvc.perform(get(path))
          .andExpect(status().isConflict())
          .andExpect(jsonPath("$.errorCode").value("M1005"));
    }
  }

  @RestController
  static class RaceController {
    @GetMapping("/jpa-race")
    public void jpaRace() {
      throw new OptimisticLockException("Concurrent post update");
    }

    @GetMapping("/spring-race")
    public void springRace() {
      throw new OptimisticLockingFailureException("Concurrent post update");
    }
  }
}
