package com.biursite.config;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class CustomErrorController implements ErrorController {
    private final ErrorAttributes errorAttributes;

    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping(value = "/error", produces = MediaType.TEXT_HTML_VALUE)
    public String errorHtml(HttpServletRequest request, Model model) {
        ServletWebRequest webRequest = new ServletWebRequest(request);
        Map<String, Object> attrs = this.errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
        Throwable throwable = this.errorAttributes.getError(webRequest);
        int status = (int) attrs.getOrDefault("status", 500);
        if (throwable instanceof NoHandlerFoundException) {
            status = HttpStatus.NOT_FOUND.value();
        }
        model.addAttribute("status", status);
        model.addAttribute("error", attrs.getOrDefault("error", "Error"));
        model.addAttribute("message", attrs.getOrDefault("message", ""));
        if (status == HttpStatus.NOT_FOUND.value()) {
            return "error/404";
        } else if (status == HttpStatus.FORBIDDEN.value()) {
            return "error/403";
        } else {
            return "error/500";
        }
    }

    @RequestMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> errorJson(HttpServletRequest request) {
        ServletWebRequest webRequest = new ServletWebRequest(request);
        Map<String, Object> attrs = this.errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
        Throwable throwable = this.errorAttributes.getError(webRequest);
        int status = (int) attrs.getOrDefault("status", 500);
        if (throwable instanceof NoHandlerFoundException) {
            status = HttpStatus.NOT_FOUND.value();
            attrs.put("status", status);
            attrs.put("error", "Not Found");
        }
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(attrs);
    }
}
