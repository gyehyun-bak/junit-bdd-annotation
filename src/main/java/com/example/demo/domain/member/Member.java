package com.example.demo.domain.member;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
public class Member {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    public static Member register(String name, String passwordHash) {
        Assert.hasText(name, "name은 null 혹은 빈 문자열일 수 없습니다.");
        Assert.hasText(passwordHash, "passwordHash은 null 혹은 빈 문자열일 수 없습니다.");
        return Member.builder()
                .name(name)
                .passwordHash(passwordHash)
                .status(MemberStatus.PENDING)
                .build();
    }

    public void activate() {
        Assert.state(MemberStatus.PENDING.equals(this.status), "대기(PENDING) 상태의 사용자만 활성화할 수 있습니다. status=" + status);

        status = MemberStatus.ACTIVE;
    }

    public void suspend() {
        Assert.state(MemberStatus.ACTIVE.equals(this.status), "활성(ACTIVE) 상태의 사용자만 정지할 수 있습니다. status=" + status);

        status = MemberStatus.SUSPENDED;
    }

    public void resume() {
        Assert.state(
                MemberStatus.SUSPENDED.equals(this.status), "중지(SUSPENDED) 상태의 사용자만 재활성할 수 있습니다. status=" + status);

        status = MemberStatus.ACTIVE;
    }

    public void withdraw() {
        Assert.state(
                MemberStatus.ACTIVE.equals(this.status) || MemberStatus.SUSPENDED.equals(this.status),
                "활성(ACTIVE) 혹은 중지(SUSPENDED) 상태의 사용자만 탈퇴할 수 있습니다. status=" + status);

        status = MemberStatus.WITHDRAWN;
    }

    public void ensureActive() {
        Assert.state(MemberStatus.ACTIVE.equals(this.status), "활성(ACTIVE) 상태의 사용자만 이용할 수 있습니다. status=" + status);
    }
}
