package de.thm.webservices.issuetracker.security

import de.thm.webservices.issuetracker.model.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import java.util.function.Function

@Component
class JwtUtil(
        @Value("\${security.jwt.signing-key}") private val signingKey: String
) {
    companion object {
        val CLAIM_KEY = "role"
    }

    fun getUsernameFromToken(token: String?): String? {
        return getClaimFromToken(token, Function { obj: Claims -> obj.subject })
    }

    fun getExpirationDateFromToken(token: String?): Date {
        return getClaimFromToken(token, Function { obj: Claims -> obj.expiration })
    }

    fun <T> getClaimFromToken(token: String?, claimsResolver: Function<Claims, T>): T {
        val claims = getAllClaimsFromToken(token)
        return claimsResolver.apply(claims)
    }

    fun getAllClaimsFromToken(token: String?): Claims {
        return Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token)
                .getBody()
    }

    fun isTokenExpired(token: String?): Boolean {
        val expiration = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    fun generateToken(user: User): String {
        return Jwts.builder()
                .setSubject(user.id.toString())
                .claim(CLAIM_KEY, listOf(user.role))
                .signWith(SignatureAlgorithm.HS256, signingKey)
                .setIssuedAt(Date(System.currentTimeMillis()))
                .setExpiration(Date(System.currentTimeMillis() + 5 * 60 * 60 * 1000))
                .compact()
    }
}
