package com.medipass.allerpass.entity;

import com.medipass.allerpass.enums.Severity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "allergy")
public class Allergy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long allergyId;

    @ManyToOne
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private String allergyName; // 항생제 이름

    @Column(nullable = false)
    private String reactionDate; // 반응 날짜

    @Column(nullable = false)
    private String cause; // 반응 이유

    @Column(nullable = false)
    private String reactionType; // 반응 형태

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity; // 경증, 중증도, 중증

    @Column(nullable = false)
    private String treatment; // 치료 조치

    @Column(nullable = false)
    private String precautions; // 주의 사항
}
