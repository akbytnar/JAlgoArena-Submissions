package com.jalgoarena

import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.Submission
import com.winterbe.expekt.should
import jetbrains.exodus.entitystore.PersistentEntityStores
import org.junit.AfterClass
import org.junit.Test
import java.io.File

class SubmissionsRepositorySpec {

    val dummySubmission = { problemId: String, userId: String, language: String -> Submission(problemId, 1, 0.5, "class Solution", "ACCEPTED", userId, language) }

    companion object {
        val testDbName = "./SubmissionsStoreForTest"
        val repository: SubmissionsRepository

        init {
            PersistentEntityStores.newInstance(testDbName).close()
            repository = SubmissionsRepository(testDbName)
        }

        @AfterClass
        @JvmStatic fun tearDown() {
            repository.destroy()
            File(testDbName).deleteRecursively()
        }
    }

    @Test
    fun should_allow_on_adding_new_submission() {
        val submission = repository.addOrUpdate(user1SubmissionForFibInKotlin)
        submission.id.should.not.be.`null`
    }

    @Test
    fun should_update_submission_if_userId_and_problemId_are_same() {
        val submissionKotlin = repository.addOrUpdate(user1SubmissionForFibInKotlin)
        val submissionJava = repository.addOrUpdate(user1SubmissionForFibInJava)

        submissionJava.id.should.equal(submissionKotlin.id)
    }

    @Test
    fun should_update_values_for_new_submission_with_same_problemId_and_userId() {
        val submissionKotlin = repository.addOrUpdate(user1SubmissionForFibInKotlin)
        repository.addOrUpdate(user1SubmissionForFibInJava)

        val submission = repository.find(submissionKotlin.id!!)
        submission!!.language.should.equal("java")
    }

    @Test
    fun should_delete_already_added_submission() {
        val submission = repository.addOrUpdate(user2SubmissionForFibInKotlin)
        repository.delete(submission.id!!)
        val deletedSubmission = repository.find(submission.id!!)

        deletedSubmission.should.be.`null`
    }

    private val user1SubmissionForFibInKotlin = dummySubmission("fib", "User#1", "kotlin")
    private val user1SubmissionForFibInJava = dummySubmission("fib", "User#1", "java")
    private val user2SubmissionForFibInKotlin = dummySubmission("fib", "User#2", "kotlin")
}