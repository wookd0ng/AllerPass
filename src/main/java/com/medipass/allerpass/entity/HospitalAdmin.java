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

    @Column(nullable = false)
    private String adminName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;// super_admin과 hospital_admin 사용 예정

    @Column
    private String lastLogin;

    // set은 병원 인증 상태 업데이트 메서드
    @Setter
    @Column(nullable = false)
    private boolean hospitalVerified = false; // ✅ 병원 인증 여부 필드 추가

    @Column(nullable = false)
    private boolean emailVerfied = false; // ✅ 이메일 인증 여부 필드 추가



    //  hospitalAdmin 생성 정적 메서드
    public static HospitalAdmin createAdmin(Hospital hospital, String adminName, String email, String password, Role role){
        HospitalAdmin hospitalAdmin = new HospitalAdmin();
        hospitalAdmin.hospital=hospital; // 여기서 병원과 연결됨
        hospitalAdmin.adminName=adminName;
        hospitalAdmin.email=email;
        hospitalAdmin.password=password; //암호화는 서비스 레이어에서 처리
        hospitalAdmin.role=role;
        hospitalAdmin.hospitalVerified=false;
        hospitalAdmin.emailVerfied=false;
        return hospitalAdmin;

    }

}
