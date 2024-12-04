package com.example.terrestrial_tutor.entity;

import com.example.terrestrial_tutor.entity.enums.ERole;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
/**
 * Класс сущности администратора
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "admins", schema = "public")
public class AdminEntity implements User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 10)
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @NonNull
    @Column(name = "username")
    String username;

    @NonNull
    @Column(name = "name")
    String name;

    @NonNull
    @Column(name = "surname")
    String surname;

    @Column(name = "patronymic")
    String patronymic;

    @NonNull
    @Column(name = "email")
    String email;

    @NonNull
    @Column(name = "password")
    String password;

    @NonNull
    @Column(name = "role")
    ERole role;

    @Transient
    private GrantedAuthority authorities;



    /**
     * Конструктор администратора
     *
     * @param id          id
     * @param username    логин
     * @param name        имя
     * @param surname     фамилия
     * @param patronymic  отчество
     * @param email       почта
     * @param password    пароль
     * @param role        роль
     * @param authorities авторизация
     */
    public AdminEntity(Long id,
                       String username,
                       String name,
                       String surname,
                       String patronymic,
                       String email,
                       String password,
                       ERole role,
                       GrantedAuthority authorities) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.email = email;
        this.password = password;
        this.role = role;
        this.authorities = authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
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

    @Override
    public Boolean getVerification() {
        return true;
    }
}
