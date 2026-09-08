package com.sayyoung.seed.domain.user.service;

import com.sayyoung.seed.domain.auth.jwt.JwtProvider;
import com.sayyoung.seed.domain.auth.service.RefreshTokenService;
import com.sayyoung.seed.domain.user.entity.User;
import com.sayyoung.seed.domain.user.entity.UserGoal;
import com.sayyoung.seed.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static com.sayyoung.seed.domain.user.service.IntakeFixtures.*;

/**
 * 별도 검증용 MySQL DB에서만 실행한다. 테스트 데이터는 롤백한다.
 * SEED_INTAKE_DB_TEST=true 및 SPRING_DATASOURCE_URL/USERNAME/PASSWORD를 지정한다.
 * Flyway가 실행되므로 URL은 별도 검증 DB를 지정해야 한다.
 */
@SpringBootTest(properties = {"spring.profiles.active=", "spring.config.import=", "spring.jpa.hibernate.ddl-auto=validate"})
@EnabledIfEnvironmentVariable(named = "SEED_INTAKE_DB_TEST", matches = "true")
@Transactional
class UserIntakePersistenceTest {
    @Autowired UserRepository users;
    @Autowired UserService service;
    @Autowired DiagnosisInputService inputs;
    @Autowired EntityManager entityManager;
    @MockitoBean JwtProvider jwt;
    @MockitoBean RefreshTokenService refresh;

    @Test void 실제_DB에_정보를_저장하고_다시_조회한다() {
        User user = users.saveAndFlush(User.create("verify_" + UUID.randomUUID().toString().substring(0, 20)));
        var request = request("13");
        service.submitIntake(user.getId(), request);
        entityManager.flush(); entityManager.clear();
        var input = inputs.findByUserId(user.getId());
        assertThat(input.goals()).hasSize(1);
        var savedGoal = entityManager.find(UserGoal.class, input.goals().get(0).goalId());
        assertThat(savedGoal.getStatus()).isEqualTo("active");
        Number removedColumns = (Number) entityManager.createNativeQuery("""
                SELECT COUNT(*) FROM information_schema.columns
                WHERE table_schema = DATABASE() AND table_name = 'user_goals'
                  AND column_name IN ('priority_rank', 'flow_type')
                """).getSingleResult();
        assertThat(removedColumns.intValue()).isZero();
        assertThat(input.userProfile().income()).isEqualTo(request.getUserProfile().getIncome());
        assertThat(input.goals().get(0).description()).isEqualTo(request.getGoals().get(0).getDescription());
    }

    @Test void 재제출하면_목표_목록을_교체한다() {
        User user = users.saveAndFlush(User.create("verify_" + UUID.randomUUID().toString().substring(0, 20)));
        service.submitIntake(user.getId(), request("13"));
        entityManager.flush(); entityManager.clear();
        var saved = inputs.findByUserId(user.getId());
        assertThat(saved.goals()).hasSize(1);
        service.submitIntake(user.getId(), request("42"));
        entityManager.flush(); entityManager.clear();
        assertThat(inputs.findByUserId(user.getId()).goals()).extracting(g -> g.categoryId()).containsExactly("42");
    }
}
