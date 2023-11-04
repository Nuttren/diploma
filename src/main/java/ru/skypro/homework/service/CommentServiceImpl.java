package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.dto.CommentInfoDTO;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.pojo.Ad;
import ru.skypro.homework.pojo.Comment;
import ru.skypro.homework.pojo.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final AdRepository adRepository;
    private final UserRepository userRepository;

    @Override
    public CommentDTO addCommentToAd(Long pk, CommentDTO commentDTO, Long userId) {
        Ad ad = adRepository.findById(pk).orElse(null);

        if (ad != null) {
            Comment comment = new Comment();
            comment.setText(commentDTO.getText());
            comment.setUserId(userId);
            comment.setPk(pk);
            comment.setTimeStamp(comment.getTimeStamp());
            Comment savedComment = commentRepository.save(comment);

            // Преобразование savedComment обратно в CommentDTO и его возврат
            CommentDTO createdCommentDTO = new CommentDTO();
            createdCommentDTO.setText(savedComment.getText());

            return createdCommentDTO;
        }
        return null;
    }


    @Override
    public List<CommentInfoDTO> getAllCommentsByPK(Long pk) {
        if(adRepository.findByPk(pk) == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        List<Comment> comments = commentRepository.findByPk(pk); // Получаем комментарии из базы данных
        List<CommentInfoDTO> commentsFullInfo = new ArrayList<>();
        for (Comment comment : comments) {
            commentsFullInfo.add(CommentInfoDTO.fromComment(comment));
        }

        return commentsFullInfo;
    }

    @Override
    public String deleteCommentByAdAndCommentId(String userName, Long pk, Long commentId) {
        Comment comment = commentRepository.findByPkAndCommentId(pk, commentId);
        User user = userRepository.findUserByUserName(userName);
        if(user == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }else if(user.getUserID().equals(comment.getUserId()) || user.getRole().equals(Role.ADMIN)) {
            if (comment != null) {
                commentRepository.delete(comment); // Удаляем комментарий из базы данных
                return "Комментарий успешно удален";
            } else {
                return "Комментарий не найден";
            }
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }


    @Override
    public CommentInfoDTO updateCommentAndGetInfo(String userName, Long pk, Long commentId, CommentDTO commentDTO) {
        Comment comment = commentRepository.findByPkAndCommentId(pk, commentId);
        User user = userRepository.findUserByUserName(userName);
        if(user == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }else if(user.getUserID().equals(comment.getUserId()) || user.getRole().equals(Role.ADMIN)) {
            if (comment != null) {
                // Обновляем текст комментария
                comment.setText(commentDTO.getText());
                // Сохраняем обновленный комментарий в бд
                comment = commentRepository.save(comment);
                return CommentInfoDTO.fromComment(comment);
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

}


