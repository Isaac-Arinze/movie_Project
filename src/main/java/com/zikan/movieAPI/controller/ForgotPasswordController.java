package com.zikan.movieAPI.controller;

import com.zikan.movieAPI.Auth.entities.ForgotPassword;
import com.zikan.movieAPI.Auth.entities.User;
import com.zikan.movieAPI.Auth.repository.ForgotPasswordRepository;
import com.zikan.movieAPI.Auth.repository.UserRepository;
import com.zikan.movieAPI.dto.MailBody;
import com.zikan.movieAPI.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Date;
import java.util.Random;

@RestController
@RequestMapping("/api/v1")
public class ForgotPasswordController {

    private final UserRepository userRepository;

    private EmailService emailService;

    private ForgotPasswordRepository forgotPasswordRepository;

    public ForgotPasswordController(UserRepository userRepository, EmailService emailService, ForgotPasswordRepository forgotPasswordRepository) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.forgotPasswordRepository = forgotPasswordRepository;
    }

    @PostMapping ("/verifyMail/{email}")
    public ResponseEntity<String> verifyEmail (@PathVariable String email){
        User user  = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid email address"));

   // formu;te the mailbody to send a mail

        int otp = otpGenerator();

        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is the OTP for your for forgot passwor request : " + otp)
                .subject("OTP for forgot Password request")
                .build();

        ForgotPassword forgotPassword = new ForgotPassword().builder()

                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() * 70 * 1000))
                .user(user)
                .build();

        emailService.sendSimpleMessage(mailBody);

        forgotPasswordRepository.save(forgotPassword);

        return ResponseEntity.ok("Email sent to for verification");

    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp (@PathVariable Integer otp, @PathVariable String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Username Not found Exception"));


       ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp, user)
                .orElseThrow(()-> new RuntimeException("Invalid Otp for email: " + email));

       if (fp.getExpirationTime().before(Date.from(Instant.now()))){
           forgotPasswordRepository.deleteById(fp.getFpid());
           return new ResponseEntity<>("OTP has expired", HttpStatus.EXPECTATION_FAILED);
       }
       return ResponseEntity.ok("OTP verified");

    }



    private Integer otpGenerator (){
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }
}
