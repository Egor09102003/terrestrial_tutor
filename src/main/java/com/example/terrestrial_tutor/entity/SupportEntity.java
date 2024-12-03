package com.example.terrestrial_tutor.entity;

import javax.persistence.*;

import com.example.terrestrial_tutor.entity.enums.ERole;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.sql.Timestamp;import java.util.List;

/**
 * Класс сущности тех поддержки
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "supports", schema = "public")
public class SupportEntity implements User, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 10)
    private Long id;

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

    @OneToMany(mappedBy = "support", fetch = FetchType.LAZY)
    List<PupilEntity> pupils;

    @OneToMany(mappedBy = "support", fetch = FetchType.LAZY)
    List<TaskEntity> tasks;

    @NonNull
    @Column(name = "telegram_tag")
    String telegramTag;

    @Column(name = "payment_data")
    String paymentData;

    @NonNull
    @Column(name = "username")
    String username;

    

    /**
     * Конструктор сущности тех поддержки
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
    public SupportEntity(Long id,
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
