package com.stock.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Component;

import java.util.Calendar;


@Component
public class JwtUtils {

    private static final String SING = "Hello";

    public String getToken() {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE, 60 * 24);
        JWTCreator.Builder builder = JWT.create();
        return builder.withExpiresAt(instance.getTime()).sign(Algorithm.HMAC256(SING));
    }

    public void verify(String token) {
        JWT.require(Algorithm.HMAC256(SING)).build().verify(token);
    }
}
