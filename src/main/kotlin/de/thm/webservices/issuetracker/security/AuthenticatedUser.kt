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
     * Check if user is the owner of issue/comment or is admin
     * @param userIdToCheck UUID
     * @return Boolean
     */
    fun hasRightsOrIsAdmin(userIdToCheck: UUID): Boolean {
        return this.userId  == userIdToCheck.toString() || this.authorities.all {
            it!!.authority == "ADMIN"
        }
    }

    /**
     * Check if current user has admin rights
     * @return Boolean
     */
    fun hasAdminRights(): Boolean {
        return this.authorities.all {
            it!!.authority == "ADMIN"
        }
    }

}