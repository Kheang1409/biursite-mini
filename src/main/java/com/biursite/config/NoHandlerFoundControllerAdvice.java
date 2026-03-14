package com.biursite.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class NoHandlerFoundControllerAdvice {

    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest request) {
        String accept = request.getHeader(HttpHeaders.ACCEPT);
        if (accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE)) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Not Found");
            body.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(body);
        }
        ModelAndView mav = new ModelAndView("error/404");
        mav.setStatus(HttpStatus.NOT_FOUND);
        return mav;
    }
}
