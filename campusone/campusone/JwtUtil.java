package com.campusone.campusone.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;


@Component
public class JwtUtil {


    private final String SECRET_KEY =
            "campusoneSecretKeyForJwtAuthentication123456789";


    private final long EXPIRATION_TIME =
            1000 * 60 * 60; // 1 hour



    private Key getSigningKey(){

        return Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes()
        );

    }



    public String generateToken(String email){


        return Jwts.builder()

                .subject(email)

                .issuedAt(
                        new Date()
                )

                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                + EXPIRATION_TIME
                        )
                )

                .signWith(
                        getSigningKey()
                )

                .compact();

    }



    public String extractEmail(String token){


        return Jwts.parser()

                .verifyWith(
                        (javax.crypto.SecretKey)getSigningKey()
                )

                .build()

                .parseSignedClaims(token)

                .getPayload()

                .getSubject();

    }



    public boolean validateToken(String token){


        try{

            Jwts.parser()

                    .verifyWith(
                            (javax.crypto.SecretKey)getSigningKey()
                    )

                    .build()

                    .parseSignedClaims(token);


            return true;


        }
        catch(Exception e){

            return false;

        }

    }

}