package com.sayyoung.seed.domain.savings.entity;

import com.sayyoung.seed.domain.policy.entity.Category;
import com.sayyoung.seed.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 사용자가 등록한 저축(적립) 내역을 나타내는 엔티티입니다.
 */
@Entity
@Getter
@Table(name = "savings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Savings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "saving_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "amount", nullable = false, precision = 15, scale = 0)
    private BigDecimal amount;

    @Column(name = "saved_at", nullable = false)
    private LocalDate savedAt;

    private Savings(
            Category category,
            User user,
            String title,
            BigDecimal amount,
            LocalDate savedAt
    ) {
        this.category = category;
        this.user = user;
        this.title = title;
        this.amount = amount;
        this.savedAt = savedAt;
    }

    /**
     * 저축 내역 등록(S3) 요청으로부터 저축 엔티티를 생성합니다.
     */
    public static Savings create(
            Category category,
            User user,
            String title,
            BigDecimal amount,
            LocalDate savedAt
    ) {
        return new Savings(category, user, title, amount, savedAt);
    }
}
