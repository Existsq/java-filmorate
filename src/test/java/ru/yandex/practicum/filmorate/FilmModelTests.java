package ru.yandex.practicum.filmorate;

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
import ru.yandex.practicum.filmorate.model.Film;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmModelTests {

  @Autowired private MockMvc mvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  public void testBlankName() throws Exception {
    Film film =
        Film.builder()
            .name(" ")
            .description("Film description")
            .duration(234)
            .releaseDate(LocalDate.now().minusYears(4))
            .build();

    mvc.perform(
            post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testEmptyName() throws Exception {
    Film film = Film.builder().name("").build();

    mvc.perform(
            post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testDescriptionTooLong() throws Exception {
    Film film =
        Film.builder()
            .name("Valid Name")
            .description("A".repeat(201))
            .duration(100)
            .releaseDate(LocalDate.of(2000, 1, 1))
            .build();

    mvc.perform(
            post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testNegativeDuration() throws Exception {
    Film film =
        Film.builder()
            .name("Valid Name")
            .description("Some description")
            .duration(-100)
            .releaseDate(LocalDate.of(2000, 1, 1))
            .build();

    mvc.perform(
            post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testZeroDuration() throws Exception {
    Film film =
        Film.builder()
            .name("Valid Name")
            .description("Some description")
            .duration(0)
            .releaseDate(LocalDate.of(2000, 1, 1))
            .build();

    mvc.perform(
            post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testReleaseDateTooEarly() throws Exception {
    Film film =
        Film.builder()
            .name("Valid Name")
            .description("Some description")
            .duration(100)
            .releaseDate(LocalDate.of(1800, 1, 1))
            .build();

    mvc.perform(
            post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testValidFilm() throws Exception {
    Film film =
        Film.builder()
            .name("Inception")
            .description("Some description")
            .duration(148)
            .releaseDate(LocalDate.of(2010, 7, 16))
            .build();

    mvc.perform(
            post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)))
        .andExpect(status().isOk());
  }
}
