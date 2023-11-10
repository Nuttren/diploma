package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class MeDTO {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private Role role;
    private String image;

    public static MeDTO fromUser(UserDetailsDTO in) {
        MeDTO out   = new MeDTO();
        out.setId(in.getId());
        out.setEmail(in.getEmail());
        out.setFirstName(in.getFirstName());
        out.setLastName(in.getLastName());
        out.setPhone(in.getPhone());
        out.setRole(in.getRole());
        out.setImage("/" + in.getImage().getImagePath().replace("\\", "/"));
        return out;
    }

}
