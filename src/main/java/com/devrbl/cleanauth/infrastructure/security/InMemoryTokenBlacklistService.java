package com.devrbl.cleanauth.infrastructure.security;

import com.devrbl.cleanauth.application.service.TokenBlacklistService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryTokenBlacklistService implements TokenBlacklistService {

    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    @Override
    public void blacklist(String jti, Instant expiresAt) {
        blacklist.put(jti, expiresAt);
    }

    @Override
    public boolean isBlacklisted(String jti) {
        Instant expiry = blacklist.get(jti);
        if (expiry == null) return false;
        if (expiry.isBefore(Instant.now())) {
            blacklist.remove(jti);
            return false;
        }
        return true;
    }
}
