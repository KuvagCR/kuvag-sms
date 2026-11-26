package cz.kuvag.sms

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.text.InputType
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var result: TextView
    private lateinit var phoneInput: EditText

    private val phonePermissionRequestCode = 41
    private val smsPermissionRequestCode = 42
    private val testMessage = "Testovaci SMS KUVAG – overeni sluzebni SIM."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 40, 32, 32)
            gravity = Gravity.TOP
        }

        val heading = TextView(this).apply {
            text = "KUVAG SMS – test služební SIM"
            textSize = 23f
        }
        result = TextView(this).apply {
            textSize = 17f
            text = "Aplikace ověří druhý slot. SMS odešle až po ručním potvrzení."
            setPadding(0, 24, 0, 24)
        }
        phoneInput = EditText(this).apply {
            hint = "Tvoje vlastní telefonní číslo"
            inputType = InputType.TYPE_CLASS_PHONE
            textSize = 17f
        }
        val messageLabel = TextView(this).apply {
            text = "Zkušební zpráva: $testMessage"
            textSize = 16f
            setPadding(0, 20, 0, 20)
        }
        val checkButton = Button(this).apply {
            text = "Zkontrolovat SIM"
            setOnClickListener { checkPermissionAndRead() }
        }
        val sendButton = Button(this).apply {
            text = "Připravit testovací SMS"
            setOnClickListener { prepareTestSms() }
        }

        content.addView(heading)
        content.addView(result)
        content.addView(checkButton)
        content.addView(phoneInput)
        content.addView(messageLabel)
        content.addView(sendButton)

        val scrollView = ScrollView(this).apply {
            isFillViewport = true
            addView(content)
        }
        setContentView(scrollView)

        checkPermissionAndRead()
    }

    private fun checkPermissionAndRead() {
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE), phonePermissionRequestCode)
            result.text = "Je potřeba povolit oprávnění Telefon, aby aplikace mohla ověřit druhý slot."
            return
        }
        showSimStatus()
    }

    @Suppress("MissingPermission")
    private fun verifiedBusinessSim(): SubscriptionInfo? {
        val manager = getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        val active = manager.activeSubscriptionInfoList ?: return null
        val secondSlot = active.filter { it.simSlotIndex == 1 }
        if (secondSlot.size != 1) return null
        val sim = secondSlot.single()
        val carrier = sim.carrierName?.toString().orEmpty()
        if (!carrier.contains("T-Mobile", ignoreCase = true)) return null
        return sim
    }

    @Suppress("MissingPermission")
    private fun showSimStatus() {
        try {
            val manager = getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
            val active = manager.activeSubscriptionInfoList ?: emptyList()
            val businessSim = verifiedBusinessSim()
            val lines = active.sortedBy { it.simSlotIndex }.joinToString("\n\n") { sim ->
                val slot = sim.simSlotIndex + 1
                val carrier = sim.carrierName?.toString()?.ifBlank { "Neznámý operátor" } ?: "Neznámý operátor"
                "SIM ve slotu $slot\nOperátor: $carrier\nSubscription ID: ${sim.subscriptionId}\nSlot Index (Android): ${sim.simSlotIndex}"
            }
            result.text = if (businessSim != null) {
                "$lines\n\nOVĚŘENO: T-Mobile ve druhém slotu. Pro odeslání se použije pouze Subscription ID ${businessSim.subscriptionId}. První slot se nikdy nepoužije."
            } else {
                "$lines\n\nNELZE OVĚŘIT služební SIM T-Mobile ve druhém slotu. Odesílání je zablokováno."
            }
        } catch (e: Exception) {
            result.text = "SIM se nepodařilo bezpečně ověřit. Odesílání je zablokováno."
        }
    }

    private fun prepareTestSms() {
        val number = phoneInput.text.toString().trim()
        if (number.length < 9) {
            phoneInput.error = "Zadej své vlastní telefonní číslo."
            return
        }
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            result.text = "Nejdříve je nutné povolit ověření SIM. Odesílání zůstává zablokováno."
            requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE), phonePermissionRequestCode)
            return
        }
        val sim = try { verifiedBusinessSim() } catch (_: Exception) { null }
        if (sim == null) {
            result.text = "Služební SIM T-Mobile ve druhém slotu nelze jednoznačně ověřit. SMS nebyla odeslána."
            return
        }
        if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.SEND_SMS), smsPermissionRequestCode)
            result.text = "Po povolení SMS ještě zobrazím potvrzení. Zpráva se zatím neposílá."
            return
        }
        showFinalConfirmation()
    }

    private fun showFinalConfirmation() {
        val number = phoneInput.text.toString().trim()
        val sim = try { verifiedBusinessSim() } catch (_: Exception) { null }
        if (sim == null) {
            result.text = "Služební SIM již nelze ověřit. Odesílání je zablokováno."
            return
        }
        AlertDialog.Builder(this)
            .setTitle("Potvrdit jednu testovací SMS")
            .setMessage("Odeslat na tvoje číslo:\n$number\n\nZpráva:\n$testMessage\n\nPoužije se výhradně T-Mobile ve slotu 2 (Subscription ID ${sim.subscriptionId}). Soukromá SIM ve slotu 1 se nepoužije.")
            .setNegativeButton("Zrušit", null)
            .setPositiveButton("Odeslat testovací SMS") { _, _ -> sendTestSms() }
            .show()
    }

    private fun sendTestSms() {
        val number = phoneInput.text.toString().trim()
        val sim = try { verifiedBusinessSim() } catch (_: Exception) { null }
        if (sim == null || checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            result.text = "Kontrola SIM nebo oprávnění selhala. SMS nebyla odeslána."
            return
        }
        try {
            @Suppress("DEPRECATION")
            val smsManager = SmsManager.getSmsManagerForSubscriptionId(sim.subscriptionId)
            smsManager.sendTextMessage(number, null, testMessage, null, null)
            result.text = "Požadavek na odeslání jedné testovací SMS byl předán Androidu přes T-Mobile ve slotu 2. Ověř doručení ve zprávách telefonu."
            Toast.makeText(this, "Testovací SMS předána k odeslání přes SIM 2.", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            result.text = "SMS se nepodařilo předat k odeslání. Chyba: ${e.javaClass.simpleName}. Soukromá SIM nebyla použita."
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == phonePermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSimStatus()
            } else {
                result.text = "Oprávnění k ověření SIM nebylo povoleno. Odesílání je zablokováno."
            }
        } else if (requestCode == smsPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showFinalConfirmation()
            } else {
                result.text = "Oprávnění k odesílání SMS nebylo povoleno. Nic nebylo odesláno."
            }
        }
    }
}
