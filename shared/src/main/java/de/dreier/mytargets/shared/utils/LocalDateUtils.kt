package de.dreier.mytargets.shared.utils

import java.util.Calendar
import org.threeten.bp.LocalDate

object LocalDateUtils {
    fun today(): LocalDate {
        val calendar = Calendar.getInstance()
        return LocalDate.of(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
}
