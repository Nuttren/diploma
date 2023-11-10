package ru.skypro.homework.dto;


import lombok.Data;
import ru.skypro.homework.pojo.Comment;

import java.time.Instant;


@Data
public class CommentInfoDTO {

        private String userName;
        private String authorImage;
        private String firstName;
        private Instant createdAt;
        private Long pk;
        private String text;


        public static CommentInfoDTO fromComment(Comment in) {
                CommentInfoDTO out = new CommentInfoDTO();
                out.setUserName(in.getUser().getUserName());
                out.setAuthorImage("/"+ in.getUser().getImage().getImagePath().replace("\\", "/"));
                out.setFirstName(in.getUser().getFirstName());
                out.setCreatedAt(in.getTimeStamp());
                out.setPk(in.getCommentId());
                out.setText(in.getText());
                return out;
        }
}
