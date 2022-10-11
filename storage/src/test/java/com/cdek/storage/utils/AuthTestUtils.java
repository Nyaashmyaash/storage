package com.cdek.storage.utils;

import com.cdek.abac.pep.dto.tokenauth.AbacAuthUserDto;

public class AuthTestUtils {

    public static AbacAuthUserDto getValidUser() {
        AbacAuthUserDto user = new AbacAuthUserDto();
        user.setCode("111");
        return user;
    }
}
