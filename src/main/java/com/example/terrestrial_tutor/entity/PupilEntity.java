package com.example.terrestrial_tutor.entity;

import com.example.terrestrial_tutor.entity.enums.ERole;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Pupil entity
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "pupils", schema = "public")
public class PupilEntity implements User, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 10)
    private Long id;

    @NonNull
    @Column(name = "balance")
    Double balance;

    @ManyToMany(mappedBy = "pupils", fetch = FetchType.LAZY)
    List<HomeworkEntity> homeworkList = new ArrayList<>();

    @Column(name = "price")
    Integer lessonPprice;

    @ManyToOne()
    @JoinColumn(name = "support")
    SupportEntity support;

    @OneToMany(mappedBy = "pupil", fetch = FetchType.EAGER)
    List<EnrollmentEntity> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "pupil", fetch = FetchType.LAZY)
    List<PaymentEntity> payments = new ArrayList<>();

    @OneToMany(mappedBy = "pupil", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<AttemptEntity> homeworkAttempts = new ArrayList<>();

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

    public Set<TutorEntity> getTutors() {
        return new HashSet<TutorEntity>(this.enrollments.stream().map(enrollment -> enrollment.getTutor()).toList());
    }

    public Set<SubjectEntity> getSubjects() {
        return new HashSet<SubjectEntity>(this.enrollments.stream().map(enrollment -> enrollment.getSubject()).toList());
    }
}
