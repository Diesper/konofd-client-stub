package jp.assasans.konofd.stub

import android.content.Context
import java.nio.ByteBuffer
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import kotlin.io.encoding.Base64

// See [CodeStage.AntiCheat.ObscuredTypes.ObscuredPrefs$$.cctor]
const val OBSCURED_PREFS_KEY = "hoge"

fun obscuredXor(value: ByteArray, key: ByteArray): ByteArray {
  return ByteArray(value.size) { index ->
    (value[index].toInt() xor key[index % key.size].toInt()).toByte()
  }
}

fun getStringFromPreferences(name: String, context: Context): String? {
  val prefsName = "${context.packageName}.v2.playerprefs"
  val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
  println(prefs.all)

  for((key, value) in prefs.all) {
    println("Pref: $key = $value")

    try {
      // See [CodeStage.AntiCheat.ObscuredTypes.ObscuredPrefs$$SetString]
      val keyEncrypted = Base64.decode(key.replace("%3D", "=").replace("%2F", "/"))
      val keyDecrypted =
        obscuredXor(keyEncrypted, OBSCURED_PREFS_KEY.toByteArray()).decodeToString()
      if(keyDecrypted != name) continue

      // See [CodeStage.AntiCheat.ObscuredTypes.ObscuredPrefs$$EncryptData]
      val valueEncrypted = Base64.decode(
        (value as String).replace("%3D", "=").replace("%2F", "/")
      )
      // Hash is 4 bytes, Device hash is 3 bytes
      val valueEncryptedUnpadded = valueEncrypted.sliceArray(0 until (valueEncrypted.size - 7))
      val valueDecrypted =
        obscuredXor(valueEncryptedUnpadded, "${keyDecrypted}${OBSCURED_PREFS_KEY}".toByteArray()).decodeToString()

      println("Pref decrypted: $keyDecrypted = $valueDecrypted")
      return valueDecrypted
    } catch(e: Exception) {
      println("Failed to decrypt pref $key: $e")
    }
  }
  return null
}

/**
 * Generates a 9-digit HMAC-based TOTP from the given token.
 * Uses a 60-second time interval.
 *
 * @param token The secret token used for HMAC generation
 * @param time Optional timestamp in milliseconds (defaults to current time)
 * @return A 9-digit TOTP code as a string, zero-padded if necessary
 */
fun generateTotp(token: String, time: Long = System.currentTimeMillis()): String {
  val timeStep = 60
  val digits = 9

  // Calculate the counter value based on current time
  val counter = time / 1000 / timeStep

  // Convert counter to 8-byte big-endian array
  val counterBytes = ByteBuffer.allocate(8).putLong(counter).array()

  // Generate HMAC-SHA256
  val mac = Mac.getInstance("HmacSHA256")
  val keySpec = SecretKeySpec(token.toByteArray(Charsets.UTF_8), "HmacSHA256")
  mac.init(keySpec)
  val hmac = mac.doFinal(counterBytes)

  // Dynamic truncation
  val offset = hmac[hmac.size - 1].toInt() and 0x0F
  val binary = ((hmac[offset].toInt() and 0x7F) shl 24) or
               ((hmac[offset + 1].toInt() and 0xFF) shl 16) or
               ((hmac[offset + 2].toInt() and 0xFF) shl 8) or
               (hmac[offset + 3].toInt() and 0xFF)

  // Generate 9-digit code
  val otp = binary % 1_000_000_000

  return otp.toString().padStart(digits, '0')
}

/**
 * Formats a 9-digit TOTP code into 3x3 groups separated by spaces.
 * Example: "123456789" -> "123 456 789"
 */
fun formatTotpGrouped(totp: String): String {
  return totp.chunked(3).joinToString(" ")
}

/**
 * Returns the number of milliseconds remaining until the next TOTP code.
 * Uses a 60-second time interval.
 */
fun getTotpRemainingMillis(time: Long = System.currentTimeMillis()): Long {
  val timeStep = 60 * 1000L
  val currentPeriodStart = (time / timeStep) * timeStep
  val nextPeriod = currentPeriodStart + timeStep
  return nextPeriod - time
}
