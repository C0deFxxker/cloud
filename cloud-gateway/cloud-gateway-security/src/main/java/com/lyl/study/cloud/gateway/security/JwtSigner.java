package com.lyl.study.cloud.gateway.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyl.study.cloud.gateway.security.exception.InvalidJwtException;
import io.jsonwebtoken.*;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.InvalidClassException;
import java.util.Date;
import java.util.Map;

/**
 * JWT签名处理器
 *
 * @author liyilin
 */
@Data
public class JwtSigner {
    // jwt secret
    private String secret = "ThisIsASecret";
    // Token过期时间(秒)
    private long expire = 7200000L;
    // json serializer
    private ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    private Map<String, Object> serializeObject(Object object) {
        try {
            String json = objectMapper.writeValueAsString(object);
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Object deserializeObject(String clsName, Map<String, Object> map) {
        try {
            String json = objectMapper.writeValueAsString(map);
            Class<?> aClass = Class.forName(clsName);
            return objectMapper.readValue(json, aClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String serializeToken(Object object) {
        Map<String, Object> claims = serializeObject(object);
        return Jwts.builder()
                .setHeaderParam("cls", claims.getClass())
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Object deserializeToken(String token) throws InvalidJwtException {
        try {
            // parse the token.
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token);
            String cls = (String) claimsJws.getHeader().get("cls");
            Map<String, Object> body = claimsJws.getBody();

            // deserialize object to cls instance
            if (cls != null) {
                return deserializeObject(cls, body);
            } else {
                return body;
            }
        } catch (Exception e) {
            throw new InvalidJwtException("Invalid Token: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T deserializeToken(String token, Class<T> requireType) throws InvalidJwtException {
        try {
            // parse the token.
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token);

            // check jwt class
            String cls = (String) claimsJws.getHeader().get("cls");
            if (!requireType.getName().equals(cls)) {
                throw new InvalidClassException("JWT class should be " + cls);
            }

            Map<String, Object> body = claimsJws.getBody();

            // deserialize object to cls instance
            return (T) deserializeObject(cls, body);
        } catch (Exception e) {
            throw new InvalidJwtException("Invalid Token: " + e.getMessage());
        }
    }
}
