package de.thm.webservices.issuetracker.util

import de.thm.webservices.issuetracker.model.IssueModel
import java.util.*

/**
 * Checks the UUID against certain criteria to see if it is a UUIDV4
 *
 * @param uuidToCheck UUID to check
 * @return if valid returns true, else false
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

fun checkUUID(uuidToCheck: String?): Boolean {

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
 * @param issueModelToCheck issue to check
 * @return if valid returns true, else false
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
        if (issueModelToCheck?.id.toString().isNotEmpty()
                && issueModelToCheck?.ownerId.toString()!!.isNotEmpty()
                && issueModelToCheck.title.isNotEmpty()
                && issueModelToCheck.deadline.isNotEmpty()
        ) {
            return true
        }
    }

    return false
}

fun checkImportantProps(issueModelToCheck: IssueModel?) : Boolean{
    if (issueModelToCheck?.id != null
            || issueModelToCheck?.ownerId.toString().isNullOrEmpty()
            || issueModelToCheck?.title.isNullOrEmpty()
            || issueModelToCheck?.deadline.isNullOrEmpty()
    ) {
        return false
    }

    if (issueModelToCheck != null) {
        if (issueModelToCheck?.id == null
                && issueModelToCheck?.ownerId.toString()!!.isNotEmpty()
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
 * @param patchObject key value pair with data
 * @return valid object return true else false
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

fun checkMultiplyRequestParamForDeletingComment(commenId: UUID?, issueId: UUID?): Boolean{
    if(commenId.toString().isNullOrEmpty() || issueId.toString().isNullOrEmpty()) {
        return false
    }

    if(checkUUID(commenId) && checkUUID(issueId)){
        return true
    }
    return true
}


