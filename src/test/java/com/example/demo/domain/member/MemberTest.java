package com.example.demo.domain.member;

import static com.example.demo.domain.member.MemberFixture.*;
import static org.assertj.core.api.Assertions.*;

import com.example.demo.support.BDD;
import com.example.demo.support.Should;
import com.example.demo.support.When;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@BDD
class MemberTest {

    @Nested
    @When("새로운 사용자를 등록할 때")
    class WhenRegister {
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", " "})
        @When("name이 null, 빈 문자열 혹은 공백이면")
        @Should("IllegalArgumentException을 던져야 한다.")
        void shouldThrow_whenNameEmpty(String name) {
            assertThatThrownBy(() -> Member.register(name, generatePasswordHash()))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", " "})
        @When("passwordHash가 null, 빈 문자열 혹은 공백이면")
        @Should("IllegalArgumentException을 던져야 한다.")
        void shouldThrow_whenPasswordHashEmpty(String passwordHash) {
            assertThatThrownBy(() -> Member.register(generateName(), passwordHash))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @Should("반환된 멤버의 상태는 대기(PENDING)여야 한다.")
        void shouldReturnPendingMember() {
            String name = generateName();
            String passwordHash = generatePasswordHash();

            var member = Member.register(name, passwordHash);

            assertThat(member.getName()).isEqualTo(name);
            assertThat(member.getPasswordHash()).isEqualTo(passwordHash);
            assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
        }
    }

    @Nested
    @When("사용자를 활성화할 때")
    class WhenActivate {
        @Test
        @When("사용자가 대기(PENDING) 상태라면")
        @Should("상태를 활성(ACTIVE)으로 바꿔야한다.")
        void shouldSuccessfullyActive() {
            Member member = pending();

            member.activate();

            assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
        }

        @ParameterizedTest
        @EnumSource(
                value = MemberStatus.class,
                names = {"ACTIVE", "SUSPENDED", "WITHDRAWN"})
        @When("사용자가 대기(PENDING)가 아닌 다른 상태라면")
        @Should("IllegalStateException을 던져야 한다.")
        void shouldThrowException_WhenNotPending(MemberStatus status) {
            Member member = of(status).create();

            assertThatThrownBy(() -> member.activate()).isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @When("사용자를 중지시킬 때")
    class WhenSuspend {
        @Test
        @When("사용자가 활성(Active) 상태라면")
        @Should("사용자의 상태를 중지(SUSPENDED)로 바꿔야 한다.")
        void shouldSuccessfullySuspend_whenActive() {
            Member member = active();

            member.suspend();

            assertThat(member.getStatus()).isEqualTo(MemberStatus.SUSPENDED);
        }

        @ParameterizedTest
        @EnumSource(
                value = MemberStatus.class,
                names = {"PENDING", "SUSPENDED", "WITHDRAWN"})
        @When("사용자가 활성(ACTIVE)이 아닌 다른 상태라면")
        @Should("IllegalStateException을 던져야 한다.")
        void shouldThrowException_WhenNotActive(MemberStatus status) {
            Member member = of(status).create();

            assertThatThrownBy(() -> member.suspend()).isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @When("사용자를 재활성할 때")
    class WhenResume {
        @Test
        @When("사용자가 중지(SUSPENDED) 상태라면")
        @Should("사용자의 상태를 활성(ACTIVE)으로 바꿔야 한다.")
        void shouldSuccessfullyResume_whenSuspended() {
            Member member = suspended();

            member.resume();

            assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
        }

        @ParameterizedTest
        @EnumSource(
                value = MemberStatus.class,
                names = {"PENDING", "ACTIVE", "WITHDRAWN"})
        @When("사용자가 중지(SUSPENDED)가 아닌 다른 상태라면")
        @Should("IllegalStateException을 던져야 한다.")
        void shouldThrowException_WhenNotSuspended(MemberStatus status) {
            Member member = of(status).create();

            assertThatThrownBy(() -> member.resume()).isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @When("사용자를 탈퇴시킬 때")
    class WhenWithdraw {
        @Test
        @When("사용자가 활성(ACTIVE) 상태라면")
        @Should("사용자의 상태를 탈퇴(WITHDRAWN)로 바꿔야 한다.")
        void shouldSuccessfullyWithdraw_whenActive() {
            Member member = active();

            member.withdraw();

            assertThat(member.getStatus()).isEqualTo(MemberStatus.WITHDRAWN);
        }

        @Test
        @When("사용자가 중지(SUSPENDED) 상태라면")
        @Should("사용자의 상태를 탈퇴(WITHDRAWN)로 바꿔야 한다.")
        void shouldSuccessfullyWithdraw_whenSuspended() {
            Member member = suspended();

            member.withdraw();

            assertThat(member.getStatus()).isEqualTo(MemberStatus.WITHDRAWN);
        }

        @ParameterizedTest
        @EnumSource(
                value = MemberStatus.class,
                names = {"PENDING", "WITHDRAWN"})
        @When("사용자가 활성(ACTIVE) 또는 중지(SUSPENDED)가 아닌 다른 상태라면")
        @Should("IllegalStateException을 던져야 한다.")
        void shouldThrowException_WhenNotActiveOrSuspended(MemberStatus status) {
            Member member = of(status).create();

            assertThatThrownBy(() -> member.withdraw()).isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @When("사용자의 활성 상태를 체크할 때")
    class WhenEnsureActive {
        @Test
        @When("사용자의 활성(ACTIVE) 상태라면")
        @Should("그 어떤 예외도 던지지 말아야 한다.")
        void shouldSuccessfullyEnsureActive_whenActive() {
            Member member = active();

            assertThatCode(() -> member.ensureActive()).doesNotThrowAnyException();
        }

        @ParameterizedTest
        @EnumSource(
                value = MemberStatus.class,
                names = {"PENDING", "SUSPENDED", "WITHDRAWN"})
        @When("사용자가 활성(ACTIVE)이 아닌 다른 상태라면")
        @Should("IllegalStateException을 던져야 한다.")
        void shouldThrowException_WhenNotActive(MemberStatus status) {
            Member member = of(status).create();

            assertThatThrownBy(() -> member.ensureActive()).isInstanceOf(IllegalStateException.class);
        }
    }
}
