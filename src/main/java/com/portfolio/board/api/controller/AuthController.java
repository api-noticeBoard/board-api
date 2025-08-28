package com.portfolio.board.api.controller;

import com.portfolio.board.config.jwt.JwtProvider;
import com.portfolio.board.api.domain.Member;
import com.portfolio.board.api.repository.MemberRepository;
import com.portfolio.board.api.service.UserService;
import com.portfolio.common.business.user.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    // MemberRepository는 이제 로그인(signIn)에서만 사용됩니다.
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserService userService; // ✨ UserService 주입

    // DTO들은 컨트롤러의 내부 클래스로 두어 요청/응답의 형태를 명확히 할 수 있습니다.
    public record SignUpRequest(String username, String password, String name) {}
    public record SignInRequest(String username, String password) {}

    /**
     * 회원가입 API
     * 이제 실제 로직은 UserService에 위임합니다.
     */
    @PostMapping("/signUp")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest signUpRequest) {
        // ✨ 복잡한 비즈니스 로직을 서비스 계층에 위임
        userService.signUp(signUpRequest);
        return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다.");
    }

    /**
     * 로그인 API
     * 로그인 로직은 인증(Authentication)에 가깝고 간단하므로 컨트롤러에 둘 수도 있지만,
     * 별도의 AuthService를 만들어 분리하는 것이 더 좋습니다. 여기서는 설명을 위해 그대로 둡니다.
     */
    @PostMapping("/signIn")
    public ResponseEntity<String> signIn(@RequestBody SignInRequest signInRequest) {
        Member member = memberRepository.findByUsername(signInRequest.username())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(signInRequest.password(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        AuthUser authUser = new AuthUser(
                member.getId(),
                member.getUsername(),
                member.getRoles().stream().collect(Collectors.toSet())
        );

        String token = jwtProvider.createToken(authUser);

        return ResponseEntity.ok(token);
    }
}