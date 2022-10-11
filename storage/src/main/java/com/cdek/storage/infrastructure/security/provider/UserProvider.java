package com.cdek.storage.infrastructure.security.provider;

/**
 * Интерфейс поставщика пользователя СДЭК ABAC.
 */
public interface UserProvider {

    /**
     * Метод возвращает токен пользователя.
     *
     * @return Токен.
     */
    String getToken();
}
