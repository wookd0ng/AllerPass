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



    public HospitalAdmin(Hospital hospital, String adminName, String email, String password, Role role) {
        this.hospital = hospital;
        this.adminName = adminName;
        this.email = email;
//        this.password = new BCryptPasswordEncoder().encode(password);
        this.password = password; // ✅ 이미 암호화된 값이 들어오므로 그대로 저장

        this.role = role;
    }
}
