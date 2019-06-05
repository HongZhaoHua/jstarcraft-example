package com.jstarcraft.example.common.utility;

import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * JWT工具
 * 
 * @author Birdy
 *
 */
public class JwtUtility {

    /**
     * 编码Token
     * 
     * @param content
     * @param secret
     * @return
     */
    public static String encodeToken(Map<String, Object> content, String secret) {
        Claims claims = Jwts.claims(content);
        String token = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }

    /**
     * 解码Token
     * 
     * @param token
     * @param secret
     * @return
     */
    public static Claims decodeToken(String token, String secret) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return claims;
    }

}
