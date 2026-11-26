package cz.kuvag.sms

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : Activity() {
    private lateinit var result: TextView
    private val permissionRequestCode = 41

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 40, 32, 32)
            gravity = Gravity.TOP
        }

        val heading = TextView(this).apply {
            text = "KUVAG SMS – kontrola SIM"
            textSize = 23f
        }
        result = TextView(this).apply {
            textSize = 17f
            text = "Aplikace zatím nic neodesílá. Pouze zjišťuje, které SIM jsou v telefonu aktivní."
            setPadding(0, 28, 0, 28)
        }
        val refresh = Button(this).apply {
            text = "Zkontrolovat SIM"
            setOnClickListener { checkPermissionAndRead() }
        }

        layout.addView(heading)
        layout.addView(result)
        layout.addView(refresh)
        setContentView(layout)

        checkPermissionAndRead()
    }

    private fun checkPermissionAndRead() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                permissionRequestCode
            )
            result.text = "Je potřeba povolit oprávnění Telefon, aby aplikace mohla zjistit sloty SIM."
            return
        }
        readSubscriptions()
    }

    @Suppress("MissingPermission")
    private fun readSubscriptions() {
        try {
            val manager = getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
            val subscriptions: List<SubscriptionInfo> =
                manager.activeSubscriptionInfoList ?: emptyList()

            if (subscriptions.isEmpty()) {
                result.text = "Android nevrátil žádnou aktivní SIM. Zkontroluj, zda jsou obě SIM zapnuté."
                return
            }

            val lines = subscriptions
                .sortedBy { it.simSlotIndex }
                .map { info ->
                    val slotNumber = info.simSlotIndex + 1
                    val carrier = info.carrierName?.toString()?.takeIf { it.isNotBlank() } ?: "Operátor není znám"
                    val label = info.displayName?.toString()?.takeIf { it.isNotBlank() } ?: "Bez názvu"
                    val purpose = if (info.simSlotIndex == 1) {
                        "DRUHÝ SLOT – kandidát pro služební SIM"
                    } else {
                        "PRVNÍ SLOT – nepoužívat pro firemní SMS"
                    }
                    "SIM ve slotu $slotNumber\nOperátor: $carrier\nNázev SIM: $label\n$purpose"
                }

            result.text = lines.joinToString("\n\n") +
                "\n\nPoznámka: Toto je pouze diagnostika. Aplikace zatím nemá oprávnění odesílat SMS a nic neodesílá."
        } catch (e: SecurityException) {
            result.text = "Android z bezpečnostních důvodů nedovolil načíst SIM. Odesílání musí zůstat zablokované."
        } catch (e: Exception) {
            result.text = "SIM se nepodařilo načíst: ${e.javaClass.simpleName}. Odesílání musí zůstat zablokované."
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readSubscriptions()
            } else {
                result.text = "Oprávnění nebylo povoleno. SIM nelze ověřit, proto se nic dalšího nepovolí."
            }
        }
    }
}
