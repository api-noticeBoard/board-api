package com.portfolio.board.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * JPA Auditing 기능을 위해 현재 사용자를 제공하는 클래스.
 */
@Component // Spring이 이 클래스를 Bean으로 등록하도록 함
public class AuditorAwareImpl implements AuditorAware<String> {

    // TODO: Spring Security를 도입한 후에는, SecurityContextHolder에서
    //       실제 로그인한 사용자 ID를 가져오도록 수정해야 합니다.

    // 현재는 로그인 기능이 없으므로, 임시로 "system" 또는 "admin"을 반환합니다.
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("system");
    }
}
