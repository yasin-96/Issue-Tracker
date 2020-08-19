package de.thm.webservices.issuetracker.util

import de.thm.webservices.issuetracker.model.CommentModel
import de.thm.webservices.issuetracker.model.IssueModel
import de.thm.webservices.issuetracker.model.UserModel
import java.util.*

/**
 * Checks the UUID against certain criteria to see if it is a UUIDV4
 *
 * @param uuidToCheck UUID? UUID to check
 * @return Boolean if valid returns true, else false
 */
fun checkUUID(uuidToCheck: UUID?): Boolean {

    if(uuidToCheck == null){
        return false
    }

    if (!uuidToCheck.toString().isNotEmpty()) {
        return false
    }

    if (uuidToCheck.toString().length == 36 && uuidToCheck.toString().split("-").size == 5) {
        return true
    }

    return false
}

/**
 * Checks whether the issue has a valid content
 *
 * @param issueModelToCheck IssueModel? Issue to check
 * @return Boolean If valid true, else false
 */
fun checkIssueModel(issueModelToCheck: IssueModel?): Boolean {

    if (issueModelToCheck?.id?.toString().isNullOrEmpty()
            || issueModelToCheck?.ownerId.toString().isNullOrEmpty()
            || issueModelToCheck?.title.isNullOrEmpty()
            || issueModelToCheck?.deadline.isNullOrEmpty()
    ) {
        return false
    }

    if (issueModelToCheck != null) {
        if (issueModelToCheck.id.toString().isNotEmpty()
                && issueModelToCheck.ownerId.toString().isNotEmpty()
                && issueModelToCheck.title.isNotEmpty()
                && issueModelToCheck.deadline.isNotEmpty()
        ) {
            return true
        }
    }

    return false
}

/**
 *
 * @param issueModelToCheck IssueModel?
 * @return Boolean
 */
fun checkNewIssueModel(issueModelToCheck: IssueModel?) : Boolean{
    if (issueModelToCheck?.id != null
            || issueModelToCheck?.ownerId.toString().isNullOrEmpty()
            || issueModelToCheck?.title.isNullOrEmpty()
            || issueModelToCheck?.deadline.isNullOrEmpty()
    ) {
        return false
    }

    if (issueModelToCheck != null) {
        if (issueModelToCheck.id == null
                && issueModelToCheck.ownerId.toString().isNotEmpty()
                && issueModelToCheck.title.isNotEmpty()
                && issueModelToCheck.deadline.isNotEmpty()
        ) {
            return true
        }
    }

    return false
}


/**
 * Checks if the patch object contains values
 *
 * @param patchObject Map<String, Any?>? Key value pair with data
 * @return Boolean valid object return true else false
 */
fun checkPatchObject(patchObject: Map<String, Any?>?): Boolean {

    if (!patchObject.isNullOrEmpty()) {
        for (x in patchObject) {
            if (!x.value?.toString().isNullOrEmpty()) {
                return true;
            }
        }
    }
    return false
}

/**
 *
 * @param commenId UUID?
 * @param issueId UUID?
 * @return Boolean
 */
fun checkMultiplyRequestParamForDeletingComment(commenId: UUID?, issueId: UUID?): Boolean{
    if(commenId.toString().isNullOrEmpty() || issueId.toString().isNullOrEmpty()) {
        return false
    }

    if(checkUUID(commenId) && checkUUID(issueId)){
        return true
    }
    return true
}

/**
 *
 * @param commentModel CommentModel?
 * @return Boolean
 */
fun checkCommentModel(commentModelToCheck: CommentModel?): Boolean {
    if (commentModelToCheck?.id?.toString().isNullOrEmpty()
            || commentModelToCheck?.userId.toString().isNullOrEmpty()
            || commentModelToCheck?.issueId.toString().isNullOrEmpty()
            || commentModelToCheck?.content.isNullOrEmpty()
            || commentModelToCheck?.creation.isNullOrEmpty()
    ) {
        return false
    }

    if (commentModelToCheck != null) {
        if (commentModelToCheck.id.toString().isNotEmpty()
                && commentModelToCheck.userId.toString().isNotEmpty()
                && commentModelToCheck.issueId.toString().isNotEmpty()
                && commentModelToCheck.content.isNotEmpty()
                && commentModelToCheck.creation.isNotEmpty()
        ) {
            return true
        }
    }

    return false
}

/**
 *
 * @param commentModelToCheck CommentModel?
 * @return Boolean
 */
fun checkNewCommentModel(commentModelToCheck: CommentModel?): Boolean {
    if (    commentModelToCheck?.id != null
            || commentModelToCheck?.userId.toString().isNullOrEmpty()
            || commentModelToCheck?.issueId.toString().isNullOrEmpty()
            || commentModelToCheck?.content.isNullOrEmpty()
            || commentModelToCheck?.creation != null
    ) {
        return false
    }

    if (commentModelToCheck != null) {
        if ( commentModelToCheck.id == null
                && commentModelToCheck.userId.toString().isNotEmpty()
                && commentModelToCheck.issueId.toString().isNotEmpty()
                && commentModelToCheck.content.isNotEmpty()
                && commentModelToCheck.creation == null
        ) {
            return true
        }
    }

    return false
}

/**
 *
 * @param userModelToCheck UserModel?
 * @return Boolean
 */
fun checkNewUserModel(userModelToCheck: UserModel?): Boolean {
    if (    userModelToCheck?.id != null
            || userModelToCheck?.username.toString().isNullOrEmpty()
            || userModelToCheck?.password.toString().isNullOrEmpty()
            || userModelToCheck?.role.isNullOrEmpty()
    ) {
        return false
    }

    if (userModelToCheck != null) {
        if ( userModelToCheck.id == null
                && userModelToCheck.username.isNotEmpty()
                && userModelToCheck.password.isNotEmpty()
                && userModelToCheck.role.isNotEmpty()
        ) {
            return true
        }
    }

    return false
}

