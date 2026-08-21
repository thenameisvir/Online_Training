package com.shopsphere.authservice;

import com.shopsphere.authservice.exception.InvalidCredentialsException;
import com.shopsphere.authservice.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleUserAlreadyExists_ReturnsConflict() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("Already exists");
        ResponseEntity<Map<String, Object>> response = handler.handleUserAlreadyExists(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Already exists", response.getBody().get("message"));
    }

    @Test
    void handleInvalidCredentials_ReturnsUnauthorized() {
        InvalidCredentialsException ex = new InvalidCredentialsException("Wrong pass");
        ResponseEntity<Map<String, Object>> response = handler.handleInvalidCredentials(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Wrong pass", response.getBody().get("message"));
    }

    @Test
    void handleGeneralException_ReturnsInternalError() {
        Exception ex = new Exception("Random error");
        ResponseEntity<Map<String, Object>> response = handler.handleException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Something went wrong!", response.getBody().get("message"));
    }
    @Test
    void handleValidationException_ReturnsBadRequest() {
        // Mocking validation error
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult br = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(br);
        when(br.getFieldErrors()).thenReturn(List.of());

        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation Failed", response.getBody().get("error"));
    }
}