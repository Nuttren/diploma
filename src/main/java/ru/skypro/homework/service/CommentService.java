package ru.skypro.homework.service;


import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.dto.CommentInfoDTO;


import java.util.List;

public interface CommentService {

    CommentDTO addCommentToAd(Long pk, CommentDTO commentDTO, Long userId);

    List<CommentInfoDTO> getAllCommentsByPK(Long pk);

    String deleteCommentByAdAndCommentId(String pk, Long commentId, Long id);

    CommentInfoDTO updateCommentAndGetInfo(String name, Long adId, Long commentId, CommentDTO commentDTO);



}