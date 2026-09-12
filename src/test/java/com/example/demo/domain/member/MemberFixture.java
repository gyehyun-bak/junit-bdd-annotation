package com.example.demo.domain.member;

import static org.instancio.Instancio.gen;
import static org.instancio.Select.field;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Model;

public final class MemberFixture {

    private static final Model<Member> BASE = Instancio.of(Member.class)
            .ignore(field(Member::getId))
            .generate(field(Member::getName), gen -> gen.text().word().noun())
            .generate(field(Member::getPasswordHash), gen -> gen.string().hex().length(60))
            .toModel();

    private MemberFixture() {}

    public static InstancioApi<Member> of(MemberStatus status) {
        return Instancio.of(BASE).set(field(Member::getStatus), status);
    }

    public static Member pending() {
        return of(MemberStatus.PENDING).create();
    }

    public static Member active() {
        return of(MemberStatus.ACTIVE).create();
    }

    public static Member suspended() {
        return of(MemberStatus.SUSPENDED).create();
    }

    public static String generateName() {
        return gen().text().word().noun().get();
    }

    public static String generatePasswordHash() {
        return gen().string().hex().length(60).get();
    }
}
