package ru.skypro.homework.dto;

import lombok.Data;
import ru.skypro.homework.pojo.Ad;
import ru.skypro.homework.pojo.Image;

@Data
public class AdInfoDTO {
    private Long pk;
    private String authorFirstName;
    private String authorLastName;
    private String description;
    private String email;
    private String image;
    private String phone;
    private Long price;
    private String title;


    // Создание объекта AdsInfoDTO и заполнение его данными из объявления
    public static AdInfoDTO fromAd(Ad in) {
        AdInfoDTO out = new AdInfoDTO();
        out.setPk(in.getPk());
        out.setAuthorFirstName(in.getUser().getFirstName());
        out.setAuthorLastName(in.getUser().getLastName());
        out.setDescription(in.getDescription());
        out.setEmail(in.getUser().getEmail());
        out.setImage("/" + in.getImage().getImagePath().replace("\\", "/"));
        out.setPhone(in.getUser().getPhone());
        out.setPrice(in.getPrice());
        out.setTitle(in.getTitle());
        return out;
    }

}
