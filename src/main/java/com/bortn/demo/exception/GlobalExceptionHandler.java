package com.bortn.demo.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Обработка ошибки 404 - Страница не найдена
    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(Exception ex, Model model) {
        model.addAttribute("error", "Страница не найдена (404)");
        return "error/404";  // Путь к странице ошибки 404
    }

    // Обработка ошибки 405 - Метод не разрешен
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public String handleMethodNotAllowed(Exception ex, Model model) {
        model.addAttribute("error", "Метод не разрешен (405)");
        return "error/405";  // Путь к странице ошибки 405
    }
}
