package com.example.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/preferences")
public class PreferencesController {

    private static final String VIEW_PREFERENCE_COOKIE_NAME = "viewPreference";
    private static final String DEFAULT_VIEW_MODE = "detailed";


    @GetMapping("/view")
    public ResponseEntity<Map<String, String>> getViewPreference(
            @CookieValue(name = VIEW_PREFERENCE_COOKIE_NAME, required = false) String viewPreference) {

        String mode = viewPreference != null ? viewPreference : DEFAULT_VIEW_MODE;

        Map<String, String> response = new HashMap<>();
        response.put("viewPreference", mode);
        response.put("description", mode.equals("compact") ?
                "Compact view - minimal details" :
                "Detailed view - full task information");

        log.info("View preference retrieved: {}", mode);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/view")
    public ResponseEntity<Map<String, String>> setViewPreference(
            @RequestParam String mode,
            HttpServletResponse response) {

        // Валидация
        if (!mode.equals("compact") && !mode.equals("detailed")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Mode must be 'compact' or 'detailed'"));
        }

        Cookie cookie = new Cookie(VIEW_PREFERENCE_COOKIE_NAME, mode);
        cookie.setPath("/");
        cookie.setMaxAge(30 * 24 * 60 * 60); // 30 дней
        cookie.setHttpOnly(true);

        response.addCookie(cookie);

        Map<String, String> result = new HashMap<>();
        result.put("viewPreference", mode);
        result.put("message", "View preference updated successfully");

        log.info("View preference set to: {}", mode);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/view")
    public ResponseEntity<Map<String, String>> resetViewPreference(HttpServletResponse response) {
        // Удаляем cookie, устанавливая maxAge = 0
        Cookie cookie = new Cookie(VIEW_PREFERENCE_COOKIE_NAME, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);

        response.addCookie(cookie);

        Map<String, String> result = new HashMap<>();
        result.put("viewPreference", DEFAULT_VIEW_MODE);
        result.put("message", "View preference reset to default: " + DEFAULT_VIEW_MODE);

        log.info("View preference reset to default");

        return ResponseEntity.ok(result);
    }
}