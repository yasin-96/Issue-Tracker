package de.thm.webservices.issuetracker.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.*


class AuthenticatedUser(private val userId: String, private val roles: List<SimpleGrantedAuthority?>) : Authentication {
    companion object {
        private const val serialVersionUID = 6861381095901879822L
    }

    private var authenticated = true
    override fun getAuthorities(): Collection<GrantedAuthority?> {
        return roles
    }

    override fun getCredentials(): Any {
        return userId
    }

    override fun getDetails(): Any? {
        return null
    }

    override fun getPrincipal(): Any {
        return userId
    }

    override fun isAuthenticated(): Boolean {
        return authenticated
    }

    @Throws(IllegalArgumentException::class)
    override fun setAuthenticated(b: Boolean) {
        authenticated = b
    }

    override fun getName(): String {
        return userId
    }

    override fun toString(): String {
        return "AuthenticatedUser(userId='$userId', roles=$roles, authenticated=$authenticated)"
    }

    /**
     * TODO
     * @param userIdToCheck UUID
     * @return Boolean
     */
    fun hasRightsOrIsAdmin(userIdToCheck: UUID): Boolean {
        return this.userId  == userIdToCheck.toString() || this.authorities.all {
            it!!.authority == "ADMIN"
        }
    }

    fun hasAdminRights(): Boolean {
        return this.authorities.all {
            it!!.authority == "ADMIN"
        }
    }

}