package de.dreier.mytargets.utils

import android.view.View
import android.widget.FrameLayout
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import de.dreier.mytargets.shared.models.Dimension

/**
 * DataBinding adapters for custom attributes
 */
object DataBindingAdapters {
    
    @JvmStatic
    @BindingAdapter("propertyShouldShow", "propertyShowAll", "propertyValue", requireAll = false)
    fun setPropertyVisibility(
        view: FrameLayout,
        shouldShow: Boolean?,
        showAll: Boolean?,
        propertyValue: Any?
    ) {
        val visible = (shouldShow == true) || (showAll == true)
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }
    
    @JvmStatic
    @BindingAdapter("selectedUnit")
    fun setSelectedUnit(spinner: Spinner, unit: Dimension.Unit?) {
        if (unit != null) {
            val position = when (unit) {
                Dimension.Unit.MILLIMETER -> 0
                Dimension.Unit.CENTIMETER -> 1
                Dimension.Unit.METER -> 2
                Dimension.Unit.INCH -> 3
                Dimension.Unit.FEET -> 4
                else -> 0
            }
            if (spinner.selectedItemPosition != position) {
                spinner.setSelection(position)
            }
        }
    }
    
    @JvmStatic
    @InverseBindingAdapter(attribute = "selectedUnit")
    fun getSelectedUnit(spinner: Spinner): Dimension.Unit {
        return when (spinner.selectedItemPosition) {
            0 -> Dimension.Unit.MILLIMETER
            1 -> Dimension.Unit.CENTIMETER
            2 -> Dimension.Unit.METER
            3 -> Dimension.Unit.INCH
            4 -> Dimension.Unit.FEET
            else -> Dimension.Unit.MILLIMETER
        }
    }
}
