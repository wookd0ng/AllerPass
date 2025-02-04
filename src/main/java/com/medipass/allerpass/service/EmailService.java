package com.medipass.allerpass.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    //이메일 코드 저장
    private final ConcurrentHashMap<String, String> verificationCodes = new ConcurrentHashMap<>();

    //인증이 완료된 이메일 저장
    private final ConcurrentHashMap<String, Boolean> verifiedEmails = new ConcurrentHashMap<>();

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender javaMailSender){
        this.mailSender = javaMailSender;
    }

    public String sendVerificationCode(String email){
        // 6자리 랜덤 인증코드 생성
        String verificationCode = String.format("%06d", new Random().nextInt(999999));

        //이메일 전송
        try{
            // 이메일 메시지 생성
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject("AllerPass 이메일 인증 코드");
            helper.setText("인증 코드: " + verificationCode);
            helper.setFrom(fromEmail);

            // 이메일 전송
            mailSender.send(message);

            //인증 코드 저장(5분간 유효)
            verificationCodes.put(email,verificationCode);

            //일정 시간 후 인증 코드 삭제
            new Thread(()->{
                try{
                    TimeUnit.MINUTES.sleep(5); // 5분 후 삭제
                    verificationCodes.remove(email);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }).start();

            return verificationCode;
        } catch (MessagingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 이메일 인증 코드 검증
     */
    public boolean verifyCode(String email, String inputCode){
        String storedCode = verificationCodes.get(email);
        boolean isValid = storedCode != null && storedCode.equals(inputCode);

        if (isValid) {
            verifiedEmails.put(email, true); // 인증 완료된 이메일 저장
        }

        return isValid;
    }

    /**
     * 이메일 인증 여부 확인 (회원가입 시 체크)
     */
    public boolean isEmailVerified(String email) {
        return verifiedEmails.getOrDefault(email, false);
    }
}
