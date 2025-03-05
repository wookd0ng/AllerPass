package com.medipass.allerpass.entity;

import com.medipass.allerpass.constant.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hospitalAdmin")
public class HospitalAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;

    @ManyToOne
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;


    // set은 병원 인증 상태 업데이트 메서드
    @Setter
    @Column(nullable = false)
    private boolean hospitalVerified = false; // ✅ 병원 인증 여부 필드 추가

//    @Column(nullable = false)
//    private boolean emailVerfied = false; // ✅ 이메일 인증 여부 필드 추가

    @Column(nullable = true)
    private String temporaryPassword="DEFAULT_PASSWORD"; // 무작위 비밀번호 저장 필드 추가

//    @Column(nullable = false)
//    private String email;



    //  hospitalAdmin 생성 정적 메서드
    public static HospitalAdmin createAdmin(Hospital hospital){
        HospitalAdmin hospitalAdmin = new HospitalAdmin();
        hospitalAdmin.hospital = hospital;
        return hospitalAdmin;
    }

}

//    @Column(nullable = false)
//    private String adminName;

//    @Column(unique = true, nullable = false)
//    private String email;

//    @Column(unique = true,nullable = false)
//    private String address;

//    @Column(nullable = false)
//    private String password;

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private Role role;// super_admin과 hospital_admin 사용 예정
//
//    @Column
//    private String lastLogin;