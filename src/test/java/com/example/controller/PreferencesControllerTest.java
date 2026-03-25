package com.example.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PreferencesController.class)
class PreferencesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getViewPreference_WhenCookieExists_ShouldReturnValue() throws Exception {
        mockMvc.perform(get("/api/preferences/view")
                        .cookie(new Cookie("viewPreference", "compact")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viewPreference").value("compact"))
                .andExpect(jsonPath("$.description").value("Compact view - minimal details"));
    }

    @Test
    void getViewPreference_WhenNoCookie_ShouldReturnDefault() throws Exception {
        mockMvc.perform(get("/api/preferences/view"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viewPreference").value("detailed"))
                .andExpect(jsonPath("$.description").value("Detailed view - full task information"));
    }

    @Test
    void setViewPreference_WithValidMode_ShouldSetCookie() throws Exception {
        mockMvc.perform(post("/api/preferences/view")
                        .param("mode", "compact"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viewPreference").value("compact"))
                .andExpect(jsonPath("$.message").value("View preference updated successfully"))
                .andExpect(cookie().exists("viewPreference"))
                .andExpect(cookie().value("viewPreference", "compact"));
    }

    @Test
    void setViewPreference_WithDetailedMode_ShouldSetCookie() throws Exception {
        mockMvc.perform(post("/api/preferences/view")
                        .param("mode", "detailed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viewPreference").value("detailed"))
                .andExpect(jsonPath("$.message").value("View preference updated successfully"))
                .andExpect(cookie().exists("viewPreference"))
                .andExpect(cookie().value("viewPreference", "detailed"));
    }

    @Test
    void setViewPreference_WithInvalidMode_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/preferences/view")
                        .param("mode", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Mode must be 'compact' or 'detailed'"));
    }

    @Test
    void setViewPreference_WithEmptyMode_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/preferences/view")
                        .param("mode", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resetViewPreference_ShouldDeleteCookie() throws Exception {
        mockMvc.perform(delete("/api/preferences/view"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viewPreference").value("detailed"))
                .andExpect(jsonPath("$.message").value("View preference reset to default: detailed"))
                .andExpect(cookie().maxAge("viewPreference", 0));
    }
}