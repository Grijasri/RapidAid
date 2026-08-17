package com.rapidaid.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        model.addAttribute("errorTitle", "System Encountered an Issue");
        model.addAttribute("errorMessage", ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred while processing your request.");
        return "error";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(NoHandlerFoundException ex, Model model) {
        model.addAttribute("errorTitle", "404 - Page Not Found");
        model.addAttribute("errorMessage", "The page or resource you requested could not be located.");
        return "error";
    }
}
