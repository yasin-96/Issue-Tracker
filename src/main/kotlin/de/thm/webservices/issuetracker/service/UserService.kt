package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.exception.ForbiddenException
import de.thm.webservices.issuetracker.exception.NoContentException
import de.thm.webservices.issuetracker.model.UserModel
import de.thm.webservices.issuetracker.repository.UserRepository
import de.thm.webservices.issuetracker.security.AuthenticatedUser
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class UserService(
        private val userRepository: UserRepository
       // private val authenticatedUser: AuthenticatedUser
) {


       fun getCurrentUserRole() : Mono<String> {
           return ReactiveSecurityContextHolder.getContext()
                   .map { securityContext ->
                       securityContext.authentication
                   }
                   .cast(AuthenticatedUser::class.java)
                   .filter { authenticatedUser ->
                       authenticatedUser.authorities.all {
                           it!!.authority == "admin"
                       }
                   }
                   .switchIfEmpty(Mono.error(ForbiddenException()))
                   .map { authenticatedUser ->
                       authenticatedUser.authorities.toString()
                   }

       }





    fun get(id: UUID): Mono<UserModel> {
        return userRepository.findById(id)
    }

    fun getByUsername(username: String): Mono<UserModel> {
        return userRepository.findByUsername(username)
    }

    fun post(userModel: UserModel): Mono<UserModel> {
        return userRepository.save(userModel)
    }

    /*fun delete(id: UUID): Mono<Void> {
       // if (id == authenticatedUser.credentials as UUID) {
            return userRepository.deleteById(id)
        }
        return Mono.error(NoContentException("Wrong UserID"))

    }*/
}
