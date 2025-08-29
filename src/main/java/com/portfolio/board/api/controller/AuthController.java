package com.portfolio.board.api.controller;

import com.portfolio.board.api.service.AuthService;
import com.portfolio.board.api.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "로그인 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;           // 회원가입 비즈니스 로직을 위임하기 위해 사용
    private final MemberService memberService;           // 회원가입 비즈니스 로직을 위임하기 위해 사용

    // DTO들은 컨트롤러의 내부 클래스로 두어 요청/응답의 형태를 명확히 할 수 있습니다.
    /**
     * 회원가입 요청 시 사용될 DTO(Data Transfer Object)입니다.
     * Java Record를 사용하여 불변 객체를 간결하게 정의합니다.
     * @param username 사용자 ID
     * @param password 비밀번호
     * @param name     실제 이름
     */
    public record SignUpRequest(
            @NotBlank(message = "사용자 이름은 필수입니다.") String username
            , @NotBlank(message = "비밀번호는 필수입니다.") String password
            , @NotBlank(message = "이름은 필수입니다.")String name) {}

    /**
     * 로그인 요청 시 사용될 DTO입니다.
     * @param username 사용자 ID
     * @param password 비밀번호
     */
    public record SignInRequest(
            @NotBlank(message = "사용자 이름은 필수입니다.") String username
            , @NotBlank(message = "비밀번호는 필수입니다.") String password) {}

    /**
     * 회원가입 API 엔드포인트입니다. (POST /api/auth/signUp)
     * @RequestBody 어노테이션을 통해, 클라이언트가 보낸 JSON 형식의 요청 본문이 SignUpRequest 객체로 자동 변환됩니다.
     *
     * @param signUpRequest 회원가입에 필요한 정보를 담은 DTO
     * @return HTTP 상태 코드 200(OK)와 함께 성공 메시지를 담은 ResponseEntity 객체
     */
    @PostMapping("/signUp")
    @Operation(summary = "회원가입", description = "회원가입 API")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest signUpRequest) {
        // 실제 회원가입 처리 로직(DB 저장, 유효성 검사 등)은 UserService에 위임합니다.
        // 이를 통해 컨트롤러는 HTTP 요청/응답 처리라는 자신의 역할에만 집중할 수 있습니다 (관심사의 분리).
        memberService.signUp(signUpRequest);
        return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다.");
    }

    /**
     * 로그인 API 엔드포인트입니다. (POST /api/auth/signIn)
     *
     * @param signInRequest 로그인에 필요한 정보를 담은 DTO
     * @return HTTP 상태 코드 200(OK)와 함께 JWT를 담은 ResponseEntity 객체
     */
    @PostMapping("/signIn")
    @Operation(summary = "로그인", description = "로그인 API")
    public ResponseEntity<String> signIn(@RequestBody SignInRequest signInRequest) {
        String token = authService.signIn(signInRequest);
        return ResponseEntity.ok(token);
    }
}