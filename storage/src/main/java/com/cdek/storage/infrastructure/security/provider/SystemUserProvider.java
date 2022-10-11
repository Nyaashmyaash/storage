package com.cdek.storage.infrastructure.security.provider;

import com.cdek.abac.pep.ICdekAbac;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor
public class SystemUserProvider implements UserProvider {
    /**
     * Сервис СДЭК ABAC.
     */
    private final ICdekAbac cdekAbac;

    /**
     * Логин пользователя.
     */
    @Setter
    private String login;

    /**
     * Пароль пользователя.
     */
    @Setter
    private String passwordHash;

    /**
     * Кэшированный токен.
     */
    private String token;

    /**
     * Код пользователя.
     */
    private String userCode;

    /**
     * После создания бина.
     */
    @PostConstruct
    public void postConstruct() {
        authorize();
    }

    @Override
    public String getToken() {
        return token;
    }

    /**
     * Авторизует пользователя и записывает его в свойство user.
     */
    private void authorize() {
        try {
            token = cdekAbac.authorizeHashed(login, passwordHash);
            userCode = cdekAbac.getUserFullInfo(token).getCode();
            log.info("User {} authorized", login);
        } catch (Exception e) {
            log.error("Unable to authorize provided user {}", login, e);
        }
    }
}
