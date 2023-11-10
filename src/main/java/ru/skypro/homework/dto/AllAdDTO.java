package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.skypro.homework.pojo.Ad;
import ru.skypro.homework.pojo.Image;

@Data
public class AllAdDTO {

    private Long author;
    private String image;
    private Long pk;
    private Long price;
    private String title;

    public static AllAdDTO fromAd(Ad in) {
        AllAdDTO out = new AllAdDTO();
        out.setAuthor(in.getUser().getUserID()); // Устанавливаем айдишник объявления
        out.setImage("/" + in.getImage().getImagePath().replace("\\", "/")); // Устанавливаем путь к изображению
        out.setPk(in.getPk());
        out.setPrice(in.getPrice());
        out.setTitle(in.getTitle());
        return out;
    }
}
