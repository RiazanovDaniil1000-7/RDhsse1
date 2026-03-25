package com.example.controller;

import com.example.dto.AttachmentResponseDto;
import com.example.model.TaskAttachment;
import com.example.service.AttachmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AttachmentController.class)
class AttachmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AttachmentService attachmentService;

    private AttachmentResponseDto testAttachmentDto;
    private MockMultipartFile testFile;
    private TaskAttachment testTaskAttachment;

    @BeforeEach
    void setUp() {
        testAttachmentDto = new AttachmentResponseDto(1L, "test.txt", 1024L, LocalDateTime.now());

        testFile = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello World".getBytes()
        );

        testTaskAttachment = new TaskAttachment();
        testTaskAttachment.setId(1L);
        testTaskAttachment.setFileName("test.txt");
        testTaskAttachment.setStoredFileName("uuid-test.txt");
        testTaskAttachment.setContentType("text/plain");
        testTaskAttachment.setSize(1024L);
        testTaskAttachment.setUploadedAt(LocalDateTime.now());
    }

    @Test
    void uploadAttachment_WithValidFile_ShouldReturnCreated() throws Exception {
        when(attachmentService.storeAttachment(eq(1L), any())).thenReturn(testAttachmentDto);

        mockMvc.perform(multipart("/api/tasks/1/attachments")
                        .file(testFile))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.fileName", is("test.txt")))
                .andExpect(jsonPath("$.size", is(1024)));

        verify(attachmentService).storeAttachment(eq(1L), any());
    }

    @Test
    void uploadAttachment_WithEmptyFile_ShouldReturnBadRequest() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.txt",
                MediaType.TEXT_PLAIN_VALUE,
                new byte[0]
        );

        mockMvc.perform(multipart("/api/tasks/1/attachments")
                        .file(emptyFile))
                .andExpect(status().isBadRequest());

        verify(attachmentService, never()).storeAttachment(anyLong(), any());
    }

    @Test
    void uploadAttachment_WithNoFile_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(multipart("/api/tasks/1/attachments"))
                .andExpect(status().isBadRequest());

        verify(attachmentService, never()).storeAttachment(anyLong(), any());
    }
    @Test
    void downloadAttachment_WhenExists_ShouldReturnFile() throws Exception {
        when(attachmentService.getAttachment(1L)).thenReturn(testTaskAttachment);

        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream("Hello".getBytes()));
        when(attachmentService.loadAsResource(1L)).thenReturn(resource);

        mockMvc.perform(get("/api/attachments/1"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().string("Content-Disposition", containsString("test.txt")));

        verify(attachmentService).getAttachment(1L);
        verify(attachmentService).loadAsResource(1L);
    }

    @Test
    void downloadAttachment_WhenNotFound_ShouldReturnBadRequest() throws Exception {
        when(attachmentService.getAttachment(999L)).thenThrow(new IllegalArgumentException("Attachment not found"));

        mockMvc.perform(get("/api/attachments/999"))
                .andExpect(status().isBadRequest());  // ← 400, не 500

        verify(attachmentService).getAttachment(999L);
        verify(attachmentService, never()).loadAsResource(anyLong());
    }
    @Test
    void deleteAttachment_WhenExists_ShouldReturnNoContent() throws Exception {
        doNothing().when(attachmentService).deleteAttachment(1L);

        mockMvc.perform(delete("/api/attachments/1"))
                .andExpect(status().isNoContent());

        verify(attachmentService).deleteAttachment(1L);
    }

    @Test
    void deleteAttachment_WhenNotFound_ShouldReturnBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("Attachment not found")).when(attachmentService).deleteAttachment(999L);

        mockMvc.perform(delete("/api/attachments/999"))
                .andExpect(status().isBadRequest());

        verify(attachmentService).deleteAttachment(999L);
    }

    @Test
    void getTaskAttachments_ShouldReturnList() throws Exception {
        List<AttachmentResponseDto> attachments = Arrays.asList(testAttachmentDto);
        when(attachmentService.getAttachmentsByTaskId(1L)).thenReturn(attachments);

        mockMvc.perform(get("/api/tasks/1/attachments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].fileName", is("test.txt")));

        verify(attachmentService).getAttachmentsByTaskId(1L);
    }

    @Test
    void getTaskAttachments_WhenNoAttachments_ShouldReturnEmptyList() throws Exception {
        when(attachmentService.getAttachmentsByTaskId(1L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/tasks/1/attachments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(attachmentService).getAttachmentsByTaskId(1L);
    }
}