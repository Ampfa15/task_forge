package com.taskforge;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthAndTasksTests {
    @Autowired
    MockMvc mvc;

    @Test
    void register_then_list_empty_tasks() throws Exception {
        String body = "{\"email\":\"test@taskforge.dev\",\"password\":\"secret123\",\"displayName\":\"Test\"}";
        String token = mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String jwt = token.replaceAll(".*\"token\":\"(.*?)\".*", "$1");

        mvc.perform(get("/api/tasks").header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());
    }
}


