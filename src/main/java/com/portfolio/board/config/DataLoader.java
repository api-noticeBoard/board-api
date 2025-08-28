package com.portfolio.board.config;

import com.portfolio.board.api.domain.Member;
import com.portfolio.board.api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 애플리케이션 시작 시점에 초기 데이터를 생성하는 클래스입니다.
 * CommandLineRunner 인터페이스를 구현하면, 애플리케이션이 완전히 시작된 후 run() 메서드가 자동으로 실행됩니다.
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final MemberRepository memberRepo;
    private final PasswordEncoder passwordEncoder;

    /**
     * 애플리케이션 시작 후 이 메서드가 호출됩니다.
     * @param args a
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        // 시스템 관리자 계정의 ID를 1L로 정의합니다. (UserInfoHolder의 SYSTEM_USER_ID와 일치)
        final Long SYSTEM_ADMIN_ID = 1L;

        // 1. DB에서 ID가 1L인 사용자가 있는지 확인합니다.
        if (!memberRepo.existsById(SYSTEM_ADMIN_ID)) {
            // 2. ID가 1L인 사용자가 없으면, 시스템 관리자 계정을 생성합니다.
            //    주의: 이 방법은 H2, MySQL 등 대부분의 DB에서 ID를 1로 시작할 때 잘 동작하지만,
            //    DB를 초기화하지 않고 여러 번 재시작하면 ID 충돌이 날 수 있습니다.
            //    가장 안전한 방법은 ID를 지정하지 않고, username으로 찾는 것입니다.
            //    여기서는 설명을 위해 ID 기반으로 진행합니다.

            Member systemAdmin = Member.builder()
                    // .id(SYSTEM_ADMIN_ID) // IDENTITY 전략에서는 ID를 직접 설정하지 않습니다. DB가 자동으로 1로 할당해 줄 것을 기대합니다.
                    .username("system")
                    .password(passwordEncoder.encode("system_password_placeholder")) // 실제로는 사용하지 않을 비밀번호
                    .name("시스템 관리자")
                    .roles(Collections.singletonList("ROLE_ADMIN")) // 관리자 권한 부여
                    .build();

            // 3. 생성된 관리자 계정을 DB에 저장합니다.
            //    만약 member 테이블이 비어있다면, 이 계정은 ID 1번을 할당받게 됩니다.
            memberRepo.save(systemAdmin);

            System.out.println("====== System Admin User (ID: 1) has been created. ======");
        } else {
            System.out.println("====== System Admin User (ID: 1) already exists. ======");
        }

        // 추가적으로 테스트용 일반 사용자를 생성할 수도 있습니다.
        if (memberRepo.findByUsername("testuser").isEmpty()) {
            Member testUser = Member.builder()
                    .username("testuser")
                    .password(passwordEncoder.encode("password123"))
                    .name("테스트유저")
                    .roles(Collections.singletonList("ROLE_USER"))
                    .build();
            memberRepo.save(testUser);
            System.out.println("====== Test User 'testuser' has been created. ======");
        }
    }
}
