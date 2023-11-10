package ru.skypro.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {

    //UserException
    @org.springframework.web.bind.annotation.ExceptionHandler(value = {ForbittenException.class})
    public ResponseEntity<?> handleWrongPassword(ForbittenException exception) {
        return new ResponseEntity<>("Запрещено", HttpStatus.FORBIDDEN);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = {UserNotFoundException.class})
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException exception) {
        return new ResponseEntity<>("Пользователь не найден", HttpStatus.NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = {UserUnauthorizedException.class})
    public ResponseEntity<?> handleUserNotFound(UserUnauthorizedException exception) {
        return new ResponseEntity<>("Пользователь не авторизован", HttpStatus.UNAUTHORIZED);
    }


    //CommentException
    @org.springframework.web.bind.annotation.ExceptionHandler(value = {CommentNotFoundException.class})
    public ResponseEntity<?> handleCommentNotFound(CommentNotFoundException exception) {
        return new ResponseEntity<>("Комментарий не найден", HttpStatus.NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = {AccessErrorException.class})
    public ResponseEntity<?> handleAccessError(AccessErrorException exception) {
        return new ResponseEntity<>("Запрещено", HttpStatus.FORBIDDEN);
    }

    //AdException
    @org.springframework.web.bind.annotation.ExceptionHandler(value = {AdNotFoundException.class})
    public ResponseEntity<?> handleAdNotFound(AdNotFoundException exception) {
        return new ResponseEntity<>("Объявление не найдено", HttpStatus.NOT_FOUND);
    }
}