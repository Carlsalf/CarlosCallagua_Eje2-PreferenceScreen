package com.mastermovilesua.persistencia.preferencias.sharedpreference2

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager

class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var etInput: EditText
    private lateinit var btnPreview: Button
    private lateinit var btnClose: Button
    private lateinit var tvPreview: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // IDs que deben coincidir con fragment_main.xml
        etInput = view.findViewById(R.id.input_text)
        btnPreview = view.findViewById(R.id.btn_preview)
        btnClose = view.findViewById(R.id.btn_close)
        tvPreview = view.findViewById(R.id.preview_text)

        // Estado base
        tvPreview.text = "(preview)"
        resetPreviewTransforms()

        btnPreview.setOnClickListener {
            // Evita acumulación
            resetPreviewTransforms()

            val text = etInput.text?.toString().orEmpty().trim()
            tvPreview.text = if (text.isEmpty()) "(preview)" else text

            applyPreferences()
        }

        btnClose.setOnClickListener {
            etInput.setText("")
            tvPreview.text = "(preview)"
            resetPreviewTransforms()
        }
    }

    private fun applyPreferences() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())

        // ----- BASIC FORMAT -----
        val sizeSp = prefs.getString("pref_size", null)?.toFloatOrNull() ?: 18f

        val fgColorStr = prefs.getString("pref_fg_color", null)
        val bgColorStr = prefs.getString("pref_bg_color", null)

        val bold = prefs.getBoolean("pref_bold", false)
        val italic = prefs.getBoolean("pref_italic", false)

        tvPreview.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp)

        tvPreview.setTextColor(parseColorOrDefault(fgColorStr, defaultHex = "#FF800080"))
        tvPreview.setBackgroundColor(parseColorOrDefault(bgColorStr, defaultHex = "#FF888888"))

        tvPreview.setTypeface(
            null,
            when {
                bold && italic -> Typeface.BOLD_ITALIC
                bold -> Typeface.BOLD
                italic -> Typeface.ITALIC
                else -> Typeface.NORMAL
            }
        )

        // ----- ADVANCED FORMAT -----
        val alphaPct = prefs.getInt("pref_alpha_pct", 60).coerceIn(0, 100)
        tvPreview.alpha = alphaPct / 100f

        // Rotación:
        // - Si tu XML tiene defaultValue=0, por defecto NO rota.
        // - Sólo rota cuando el usuario pone un valor > 0.
        val rotationDeg = prefs.getInt("pref_rotation", 0).coerceIn(0, 360)
        tvPreview.rotation = if (rotationDeg > 0) rotationDeg.toFloat() else 0f
    }

    private fun resetPreviewTransforms() {
        tvPreview.apply {
            rotation = 0f
            alpha = 1f
            scaleX = 1f
            scaleY = 1f
            translationX = 0f
            translationY = 0f
        }
    }

    private fun parseColorOrDefault(value: String?, defaultHex: String): Int {
        return try {
            if (!value.isNullOrBlank()) Color.parseColor(value) else Color.parseColor(defaultHex)
        } catch (_: IllegalArgumentException) {
            Color.parseColor(defaultHex)
        }
    }
}
