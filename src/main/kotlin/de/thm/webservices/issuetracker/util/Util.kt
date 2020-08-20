package de.thm.webservices.issuetracker.util

import de.thm.webservices.issuetracker.model.CommentModel
import de.thm.webservices.issuetracker.model.IssueModel
import de.thm.webservices.issuetracker.model.UserModel
import reactor.core.publisher.Mono
import java.util.*

/**
 * Checks whether the issue has a valid content
 * This is good for checking the object for patching attr of an issue
 *
 * @param issueModelToCheck IssueModel? Issue to check
 * @return Boolean If valid true, else false
 */
fun checkIssueModel(issueModelToCheck: IssueModel): Mono<Boolean> {
    return Mono.just(
            issueModelToCheck.title.isNotEmpty()
                    && issueModelToCheck.deadline.isNotEmpty())
}

/**
 * Check if the new issue has the right values
 * @param issueModelToCheck IssueModel?
 * @return Boolean
 */
fun checkNewIssueModel(issueModelToCheck: IssueModel): Mono<Boolean> {
    return Mono.just(
            issueModelToCheck.id == null
                    && issueModelToCheck.ownerId.toString().isNotEmpty()
                    && issueModelToCheck.title.isNotEmpty()
                    && issueModelToCheck.deadline.isNotEmpty()
    )
}


/**
 * Checks if the patch object contains values
 *
 * @param patchObject Map<String, Any?>? Key value pair with data
 * @return Boolean valid object return true else false
 */
fun checkPatchObject(patchObject: Map<String, String>): Mono<Boolean> {

    for (x in patchObject) {
        if (x.value.isEmpty()) {
            return Mono.just(false);
        }
    }

    return Mono.just(true);
}


/**
 * Check if the new comment has the right values
 * @param commentModelToCheck CommentModel?
 * @return Boolean
 */
fun checkNewCommentModel(commentModelToCheck: CommentModel): Mono<Boolean> {
    return Mono.just(
            commentModelToCheck.id == null
                    && commentModelToCheck.userId.toString().isNotEmpty()
                    && commentModelToCheck.issueId.toString().isNotEmpty()
                    && commentModelToCheck.content.isNotEmpty()
    )
}

/**
 * Check if the new user has the rigth values
 * @param userModelToCheck UserModel?
 * @return Boolean
 */
fun checkNewUserModel(userModelToCheck: UserModel): Mono<Boolean> {
    return Mono.just(
            userModelToCheck.id == null
                    && userModelToCheck.username.isNotEmpty()
                    && userModelToCheck.password.isNotEmpty()
                    && userModelToCheck.role.isNotEmpty()
    )
}

