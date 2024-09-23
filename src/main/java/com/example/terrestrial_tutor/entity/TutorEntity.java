package com.example.terrestrial_tutor.entity;

import javax.persistence.*;

import com.example.terrestrial_tutor.entity.enums.ERole;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import java.util.*;

/**
 * Класс сущности репетитора
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "tutors", schema = "public")
public class TutorEntity implements User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 10)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "pupils")
    Set<PupilEntity> pupils;

    @Column(name = "payment_data")
    String paymentData;

    @ManyToMany(mappedBy = "tutors", fetch = FetchType.LAZY)
    Set<HomeworkEntity> homeworkList;

    @OneToMany(mappedBy = "tutor")
    List<EnrollEntity> enrolls;

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
     * Конструктор сущности репетитора
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
    public TutorEntity(Long id,
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

    public Set<PupilEntity> getPupils() {
        return new HashSet<PupilEntity>(this.enrolls
                .stream()
                .map(EnrollEntity::getPupil)
                .toList());
    }

    public Set<PupilEntity> getPupilsBySubject(Long subjectId) {
        return new HashSet<PupilEntity>(
                this.enrolls
                        .stream()
                        .filter(enroll -> enroll.getSubject().getId().equals(subjectId))
                        .map(EnrollEntity::getPupil)
                        .toList()
        );
    }

    public List<SubjectEntity> getSubjects() {
        return this.enrolls
                .stream()
                .map(EnrollEntity::getSubject)
                .toList();
    }
}
