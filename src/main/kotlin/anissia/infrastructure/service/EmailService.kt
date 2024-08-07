package anissia.infrastructure.service

import anissia.infrastructure.common.As
import anissia.shared.ResultWrapper
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.mail.Message
import jakarta.mail.internet.InternetAddress
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Service
import java.io.File
import java.util.*

/**
 * send email
 * it just have one sender
 *
 * ====================
 * conf file spec
 * ====================
 * from: [from email]
 * host: [smtp provider host]
 * port: [smtp provider port]
 * username: [smtp provider service id (normally the same email)]
 * password: [smtp provider service password]
 *
 * ====================
 * example (email.json)
 * ====================
{
"from": "auth@saro.me"
"host": "smtp.saro.me"
"port": "935"
"username": "auth@saro.me"
"password": "password"
}
 */
@Service
class EmailService (
    @Value("\${env}") private val env: String,
) {
    private val configFile: File = File("./email.json")
    private val enable: Boolean = configFile.exists()
    private val props: Map<String, String> = if (enable) ObjectMapper().readValue(configFile, object: TypeReference<Map<String, String>>(){}) else mapOf()
    private val sender: JavaMailSenderImpl = JavaMailSenderImpl()
    private val log: Logger = As.logger<EmailService>()

    init {
        if (enable) {
            sender.apply {
                username = props["username"]
                password = props["password"]
                javaMailProperties = Properties().apply {
                    setProperty("mail.transport.protocol", "smtp")
                    setProperty("mail.smtp.starttls.enable", "true")
                    setProperty("mail.smtp.ssl.trust", props["host"] ?: "")
                    setProperty("mail.smtp.host", props["host"])
                    setProperty("mail.smtp.auth", "true")
                    setProperty("mail.smtp.port", props["port"])
                    setProperty("mail.smtp.socketFactory.port", props["port"])
                    setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                    setProperty("mail.smtp.starttls.required", "true")
                    setProperty("mail.debug", "true")
                    setProperty("mail.smtp.ssl.enable", "true")
                }
            }
        } else if (env != "local") {
            throw RuntimeException("")
        }
    }

    /**
     * @param to receive to
     * @param subject mail subject
     * @param htmlContent mail content (html)
     */
    fun send(to: String, subject: String, htmlContent: String): ResultWrapper<Unit> = send(listOf(to), listOf(), subject, htmlContent)

    /**
     * @param to receive to
     * @param cc receive cc
     * @param subject mail subject
     * @param htmlContent mail content (html)
     */
    fun send(to: List<String>, cc: List<String>, subject: String, htmlContent: String): ResultWrapper<Unit> = try {
        if (enable) {
            sender.send {
                // from / to / cc / subject / text
                it.setFrom(InternetAddress(props["from"]))
                to.forEach { to -> it.addRecipient(Message.RecipientType.TO, InternetAddress(to)) }
                cc.forEach { cc -> it.addRecipient(Message.RecipientType.CC, InternetAddress(cc)) }
                it.subject = subject
                it.setText(htmlContent, "UTF-8", "html")
            }
            ResultWrapper.ok()
        } else {
            log.debug("""
                    EMAIL DEVELOP MODE
                    to: $to
                    cc: $cc
                    subject: $subject
                    content: $htmlContent
                """.trimIndent())
            ResultWrapper.ok()
        }
    } catch (e: Exception) {
        log.debug("""
            EMAIL ERROR
            to: $to
            cc: $cc
            subject: $subject
            content: $htmlContent
        """.trimIndent())
        log.error(e.message, e)
        ResultWrapper.error(e.message)
    }
}
