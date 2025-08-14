package ru.yandex.practicum.filmorate.model;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class UserModelTests {

  @Autowired private MockMvc mvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  public void testBlankEmail() throws Exception {
    User user =
        User.builder().email(" ").login("Test").birthday(LocalDate.now().minusMonths(4)).build();

    mvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testEmailWithoutAtSymbol() throws Exception {
    User user =
        User.builder()
            .email("testgmail.com")
            .login("Test")
            .birthday(LocalDate.now().minusMonths(4))
            .build();

    mvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testBlankLogin() throws Exception {
    User user =
        User.builder()
            .email("test@gmail.com")
            .login("")
            .birthday(LocalDate.now().minusMonths(4))
            .build();

    mvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testLoginWithSpaces() throws Exception {
    User user =
        User.builder()
            .email("test@gmail.com")
            .login("te st")
            .birthday(LocalDate.now().minusMonths(4))
            .build();

    mvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testNullLogin() throws Exception {
    User user =
        User.builder().email("test@gmail.com").birthday(LocalDate.now().minusMonths(4)).build();

    mvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testBirthdayInFuture() throws Exception {
    User user =
        User.builder()
            .email("test@gmail.com")
            .login("te st")
            .birthday(LocalDate.now().plusDays(4))
            .build();

    mvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isBadRequest());
  }
}
