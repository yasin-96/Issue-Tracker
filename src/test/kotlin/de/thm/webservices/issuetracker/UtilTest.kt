package de.thm.webservices.issuetracker

import de.thm.webservices.issuetracker.util.checkUUID
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class UtilTest {

    @Test
    fun `Check if checkUUID() is valid if string is length of 36 characters`() {
        println("Check if checkUUID() is valid if string is length of 36 characters")
        var testUUID: UUID = UUID.randomUUID()
        Assertions.assertThat(testUUID.toString()).isNotEmpty()
        Assertions.assertThat(checkUUID(testUUID)).isTrue()
        println("✔")
    }

    @Test
    fun `Check if checkUUID() is not valid then length of string is less 36 characters`() {
        println("Check if checkUUID() is not valid then length of string is less 36 characters")
        var testUUID: String = UUID.randomUUID().toString().substring(0, 20)

        Assertions.assertThat(testUUID).isNotEmpty()
        Assertions.assertThat(checkUUID(testUUID)).isFalse()
        println("✔")
    }

    @Test
    fun `Check if checkUUID() is not valid then length of string is more 36 characters`() {
        println("Check if checkUUID() is not valid then length of string is more 36 characters")
        var testUUID: String = UUID.randomUUID().toString()
        var newUUID = testUUID.split("-").toList().map { s: String ->  s.plus("abc") }.joinToString("-")
        Assertions.assertThat(newUUID).isNotEmpty()
        Assertions.assertThat(checkUUID(newUUID)).isFalse()
        println("✔")
    }

    @Test
    fun `Check if checkUUID() is not valid then string is null or empty`() {
        println("Check if checkUUID() is not valid then string is null or empty")
        var testUUIDIsEmpty = ""
        var testUUIDIsNull: String? = null

        Assertions.assertThat(checkUUID(testUUIDIsEmpty)).isFalse()
        Assertions.assertThat(checkUUID(testUUIDIsNull)).isFalse()
        println("✔")
    }

    @Test
    fun `Check if checkUUID() is not valid then string to many delimiters`() {
        println("Check if checkUUID() is not valid then string to many delimiters")
        var testUUID: String = UUID.randomUUID().toString() + UUID.randomUUID().toString()

        Assertions.assertThat(checkUUID(testUUID)).isFalse()
        println("✔")
    }

    @Test
    fun `Check if checkUUID() is not valid then string to less delimiters`() {
        println("Check if checkUUID() is not valid then string to less delimiters")
        var testUUID: String = UUID.randomUUID().toString().substring(0,15)

        Assertions.assertThat(checkUUID(testUUID)).isFalse()
        println("✔")
    }

    @Test
    fun `Check if checkUUID() is not valid then string has wrong delimiters`() {
        println("Check if checkUUID() is not valid then string has wrong delimiters")
        var testUUID: String = UUID.randomUUID().toString().replace("-","/")

        Assertions.assertThat(checkUUID(testUUID)).isFalse()
        println("✔")
    }
}