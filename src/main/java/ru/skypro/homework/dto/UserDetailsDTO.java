package ru.skypro.homework.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.skypro.homework.pojo.Image;
import ru.skypro.homework.pojo.User;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDTO implements UserDetails {

    private Long id;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String phone;
    private Role role;
    private Image image;
    private String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public Role getRole() {
        return role;
    }

    public Image getImage() {
        return image;
    }

    // Преобразование сущности User в объект UserDetailsDTO
    public static UserDetailsDTO fromUser(User in) {
        UserDetailsDTO out = new UserDetailsDTO();
        out.setId(in.getUserID());
        out.setEmail(in.getEmail());
        out.setUsername(in.getUserName());
        out.setFirstName(in.getFirstName());
        out.setLastName(in.getLastName());
        out.setPhone(in.getPhone());
        out.setRole(in.getRole());
        out.setImage(in.getImage());
        out.setPassword(in.getPassword());

        return out;
    }
}
