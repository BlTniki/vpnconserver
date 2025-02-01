package ru.bitniki.sms.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.bitniki.sms.controller.model.SubscriptionResponse;
import ru.bitniki.sms.controller.model.UserResponse;
import ru.bitniki.sms.controller.model.UserSubscriptionResponse;
import ru.bitniki.sms.domain.subscriptions.dto.Subscription;
import ru.bitniki.sms.domain.subscriptions.dto.UserSubscription;
import ru.bitniki.sms.domain.users.dto.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseMapperTest {

    @Test
    @DisplayName("User: check dto to response mapping")
    void toUserResponse() {
        var expectResponse = UserResponse.builder()
                .telegramId(123L)
                .username("lolkek")
                .role(UserResponse.RoleEnum.ACTIVATED_USER)
                .build();

        var actualResponse = ResponseMapper.toUserResponse(new User(
                123L, "lolkek", "ACTIVATED_USER"
        ));

        assertThat(actualResponse).isEqualTo(expectResponse);
    }

    @Test
    @DisplayName("Subscription: check dto to response mapping")
    void toSubscriptionResponse() {
        var expectResponse = SubscriptionResponse.builder()
                .id(1L)
                .role(SubscriptionResponse.RoleEnum.ACTIVATED_USER)
                .priceInRubles(BigDecimal.ONE)
                .allowedActivePeersCount(123)
                .period(Period.ofDays(123))
                .build();

        var actualResponse = ResponseMapper.toSubscriptionResponse(new Subscription(
                1L, "ACTIVATED_USER", BigDecimal.ONE, 123, Period.ofDays(123)
        ));

        assertThat(actualResponse).isEqualTo(expectResponse);
    }

    @Test
    @DisplayName("UserSubscription: check dto to response mapping")
    void toUserSubscriptionResponse() {
        var expectResponse = UserSubscriptionResponse.builder()
                .id(1L)
                .user(
                        UserResponse.builder()
                                .telegramId(123L)
                                .username("lolkek")
                                .role(UserResponse.RoleEnum.ACTIVATED_USER)
                                .build()
                )
                .subscription(
                        SubscriptionResponse.builder()
                                .id(1L)
                                .role(SubscriptionResponse.RoleEnum.ACTIVATED_USER)
                                .priceInRubles(BigDecimal.ONE)
                                .allowedActivePeersCount(123)
                                .period(Period.ofDays(123))
                                .build()
                )
                .expirationDate(LocalDate.now())
                .build();

        var actualResponse = ResponseMapper.toUserSubscriptionResponse(new UserSubscription(
            1L,
            new User(123L, "lolkek", "ACTIVATED_USER"),
            new Subscription(1L, "ACTIVATED_USER", BigDecimal.ONE, 123, Period.ofDays(123)),
            LocalDate.now()
        ));

        assertThat(actualResponse).isEqualTo(expectResponse);
    }
}