package com.cdek.storage.infrastructure.security;

import com.cdek.abac.pep.ICdekAbac;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class CdekAbacUserDetailsService implements UserDetailsService {

    private final ICdekAbac cdekAbac;

    @Override
    public UserDetails loadUserByUsername(String token) {
        try {
            return new CdekAbacUserDetails(cdekAbac.getUserFullInfo(token));
        } catch (Exception e) {
            throw new UsernameNotFoundException("Can't find user by token", e);
        }
    }
}
