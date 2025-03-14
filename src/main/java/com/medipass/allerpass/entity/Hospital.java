package com.medipass.allerpass.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hospital")
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hospitalId;

    @Column(unique = true, nullable = false)
    private String hospitalName;

    @Column(unique = true)
    private String address;

    @Column(unique = true, nullable = false)
    private String telno;

    //  HospitalAdmin 테이블과의 1:N 관계
    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL)
    private List<HospitalAdmin> hospitalAdmins;
    //  Patient 테이블과의 1:N 관계
    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL)
    private List<Patient> patients;
    //  Allergy 테이블과의 1:N 관계
    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL)
    private List<Allergy> allergies;
}
