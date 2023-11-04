package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.dto.CommentInfoDTO;
import ru.skypro.homework.exception.UserUnauthorizedException;
import ru.skypro.homework.pojo.User;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.CommentService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс-контроллер для работы с комментариями
 */
@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping("/ads")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserRepository userRepository;

    /**
     * Добавление комментария к объявлению
     * @param authentication - проверка авторизации
     * @param pk - id Объявления
     * @param commentDTO (text)
     * @return
     * HTTPStatus 200, 401, 404
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDTO> addCommentToAd(
            Authentication authentication,
            @PathVariable("id") Long pk,
            @RequestBody CommentDTO commentDTO
    ) {
        User user = userRepository.findUserByUserName(authentication.getName());
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        CommentDTO createdComment = commentService.addCommentToAd(pk, commentDTO, user.getUserID());

        if (createdComment != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Получение комментариев объявления
     * @param authentication - проверка авторизации
     * @param pk - id Объявления
     * @return
     * HTTPStatus 200, 401, 404
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity<Map<String, Object>> getCommentsByAdId(
            Authentication authentication,
            @PathVariable("id") Long pk) {
        if(authentication.getName() == null){
            throw new UserUnauthorizedException();
        }

        List<CommentInfoDTO> comments = commentService.getAllCommentsByPK(pk);

        Map<String, Object> response = new HashMap<>();
        response.put("count", comments.size());
        response.put("results", comments);

        return ResponseEntity.ok(response);
    }

    /**
     * Удаление комментария
     * @param authentication - проверка авторизации
     * @param pk - id объявления
     * @param commentId - id объявления
     * @return статусы 200, 401, 403, 404
     */
    @DeleteMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<String> deleteCommentByAdAndCommentId(
            Authentication authentication,
            @PathVariable("adId") Long pk,
            @PathVariable("commentId") Long commentId) {
        String result = commentService.deleteCommentByAdAndCommentId(authentication.getName(), pk, commentId);

        if ("Комментарий успешно удален".equals(result)) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Обновление комментария
     * @param authentication - проверка авторизации
     * @param pk - id объявления
     * @param commentId
     * @param commentDTO (text)
     * @return CommentInfoDTO(author, authorImage, authorFirstName, createdAt, pk, text)
     * HTTPStatus 200, 401, 403, 404
     */
    @PatchMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<CommentInfoDTO> updateCommentAndGetInfo(
            Authentication authentication,
            @PathVariable("adId") Long pk,
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentDTO commentDTO
    ) {
        CommentInfoDTO updatedCommentInfo = commentService.updateCommentAndGetInfo(
                authentication.getName(),
                pk, commentId, commentDTO);

        if (updatedCommentInfo != null) {
            return ResponseEntity.ok(updatedCommentInfo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
