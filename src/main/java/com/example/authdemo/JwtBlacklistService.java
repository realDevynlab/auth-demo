package com.example.authdemo;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class JwtBlacklistService {

    private final Set<String> blacklistedTokens = new HashSet<>();

    public boolean isTokenBlacklisted(String jti) {
        return blacklistedTokens.contains(jti);
    }

    public void blacklistToken(String jti) {
        blacklistedTokens.add(jti);
    }

}
