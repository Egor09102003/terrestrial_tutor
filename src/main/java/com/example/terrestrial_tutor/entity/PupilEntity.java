package com.example.terrestrial_tutor.entity;

import com.example.terrestrial_tutor.entity.enums.ERole;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Класс сущности ученика
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "pupils", schema = "public")
public class PupilEntity implements User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 10)
    private Long id;

    @NonNull
    @Column(name = "balance")
    Double balance;

    @ManyToMany(mappedBy = "pupils", fetch = FetchType.LAZY)
    List<HomeworkEntity> homeworkList;

    @Column(name = "price")
    Integer price;

    @ManyToMany(mappedBy = "pupils", fetch = FetchType.EAGER)
    List<SubjectEntity> subjects = new ArrayList<>();

    @ManyToOne()
    @JoinColumn(name = "support")
    SupportEntity support;

    @ManyToMany(mappedBy = "pupils", fetch = FetchType.EAGER)
    List<TutorEntity> tutors = new ArrayList<>();

    @OneToMany(mappedBy = "pupil", fetch = FetchType.LAZY)
    List<PaymentEntity> payments = new ArrayList<>();

    @OneToMany(mappedBy = "pupil", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    List<AttemptEntity> answers = new ArrayList<>();

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

    @NonNull
    @Column(name = "verification")
    Boolean verification = false;

    @Transient
    private GrantedAuthority authorities;

    /**
     * Конструктор сущности ученика
     *
     * @param id           id
     * @param username     логин
     * @param name         имя
     * @param surname      фамилия
     * @param patronymic   отчество
     * @param email        почта
     * @param password     пароль
     * @param role         роль
     * @param verification верификация
     * @param authorities  авторизация
     */
    public PupilEntity(Long id,
                       String username,
                       String name,
                       String surname,
                       String patronymic,
                       String email,
                       String password,
                       ERole role,
                       Boolean verification,
                       GrantedAuthority authorities) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.email = email;
        this.password = password;
        this.role = role;
        this.verification = verification;
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
}
