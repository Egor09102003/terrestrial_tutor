package com.example.terrestrial_tutor.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

/**
 * Не актуально
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "payments", schema = "public")
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 10)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "pupil")
    PupilEntity pupil;

    /*
    @ManyToOne()
    @JoinColumn(name = "tutor")
    PupilEntity tutor;

     */

    @Column(name = "lesson_time")
    Time lessonTime;

    //int hw_id;

    @Column(name = "lesson_date")
    Date lessonDate;

    @Column(name = "status")
    String status;

    
}
