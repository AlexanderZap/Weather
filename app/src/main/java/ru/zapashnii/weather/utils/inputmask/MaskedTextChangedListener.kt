package ru.zapashnii.weather.utils.inputmask

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import ru.zapashnii.weather.utils.inputmask.helper.AffinityCalculationStrategy
import ru.zapashnii.weather.utils.inputmask.helper.Mask
import ru.zapashnii.weather.utils.inputmask.helper.RTLMask
import ru.zapashnii.weather.utils.inputmask.model.CaretString
import ru.zapashnii.weather.utils.inputmask.model.Notation
import java.lang.ref.WeakReference
import java.util.*

/**
 * Реализация TextWatcher.
 *
 * Реализация TextWatcher, которая применяет маскировку к пользовательскому вводу, выбирая наиболее подходящую маску для текста.
 *
 * Может использоваться как декоратор, который перенаправляет вызовы TextWatcher своему собственному слушателю.
 *
 * @author https://github.com/RedMadRobot/input-mask-android/wiki
 */
open class MaskedTextChangedListener(
    var primaryFormat: String,
    var affineFormats: List<String> = emptyList(),
    var customNotations: List<Notation> = emptyList(),
    var affinityCalculationStrategy: AffinityCalculationStrategy = AffinityCalculationStrategy.WHOLE_STRING,
    var autocomplete: Boolean = true,
    var autoskip: Boolean = false,
    field: EditText,
    var listener: TextWatcher? = null,
    var valueListener: ValueListener? = null,
    var rightToLeft: Boolean = false,
) : TextWatcher, View.OnFocusChangeListener {

    interface ValueListener {
        fun onTextChanged(maskFilled: Boolean, extractedValue: String, formattedValue: String)
    }

    private val primaryMask: Mask
        get() = this.maskGetOrCreate(this.primaryFormat, this.customNotations)

    private var afterText: String = ""
    private var caretPosition: Int = 0

    private val field: WeakReference<EditText> = WeakReference(field)

    /**
     * Convenience constructor.
     */
    constructor(format: String, field: EditText) :
            this(format, field, null)

    /**
     * Convenience constructor.
     */
    constructor(format: String, field: EditText, valueListener: ValueListener?) :
            this(format, field, null, valueListener)

    /**
     * Convenience constructor.
     */
    constructor(format: String, field: EditText, listener: TextWatcher?, valueListener: ValueListener?) :
            this(format, true, field, listener, valueListener)

    /**
     * Convenience constructor.
     */
    constructor(
        format: String, autocomplete: Boolean, field: EditText, listener: TextWatcher?,
        valueListener: ValueListener?,
    ) :
            this(
                format, emptyList(), emptyList(), AffinityCalculationStrategy.WHOLE_STRING,
                autocomplete, false, field, listener, valueListener
            )

    /**
     * Convenience constructor.
     */
    constructor(primaryFormat: String, affineFormats: List<String>, field: EditText) :
            this(primaryFormat, affineFormats, field, null)

    /**
     * Convenience constructor.
     */
    constructor(
        primaryFormat: String,
        affineFormats: List<String>,
        field: EditText,
        valueListener: ValueListener?,
    ) :
            this(primaryFormat, affineFormats, field, null, valueListener)

    /**
     * Convenience constructor.
     */
    constructor(
        primaryFormat: String, affineFormats: List<String>, field: EditText, listener: TextWatcher?,
        valueListener: ValueListener?,
    ) :
            this(primaryFormat, affineFormats, true, field, listener, valueListener)

    /**
     * Convenience constructor.
     */
    constructor(
        primaryFormat: String, affineFormats: List<String>, autocomplete: Boolean, field: EditText,
        listener: TextWatcher?, valueListener: ValueListener?,
    ) :
            this(
                primaryFormat,
                affineFormats,
                AffinityCalculationStrategy.WHOLE_STRING,
                autocomplete,
                field,
                listener,
                valueListener
            )

    /**
     * Convenience constructor.
     */
    constructor(
        primaryFormat: String, affineFormats: List<String>,
        affinityCalculationStrategy: AffinityCalculationStrategy, autocomplete: Boolean, field: EditText,
        listener: TextWatcher?, valueListener: ValueListener?,
    ) :
            this(
                primaryFormat, affineFormats, emptyList(), affinityCalculationStrategy,
                autocomplete, false, field, listener, valueListener
            )

    /**
     * Установите текст и примените форматирование.
     * @param text - текст; может быть простым, может уже иметь какое-то форматирование.
     */
    open fun setText(text: String, autocomplete: Boolean? = null): Mask.Result? {
        return this.field.get()?.let {
            val result = setText(text, it, autocomplete)
            this.afterText = result.formattedText.string
            this.caretPosition = result.formattedText.caretPosition
            this.valueListener?.onTextChanged(result.complete, result.extractedValue, afterText)
            return result
        }
    }

    /**
     * Установите текст и примените форматирование.
     * @param text - текст; может быть простым, может уже иметь некоторое форматирование;
     * @param field - поле, куда поместить форматированный текст.
     */
    open fun setText(text: String, field: EditText, autocomplete: Boolean? = null): Mask.Result {
        val useAutocomplete: Boolean = autocomplete ?: this.autocomplete
        val textAndCaret = CaretString(text, text.length, CaretString.CaretGravity.FORWARD(useAutocomplete))
        val result: Mask.Result = this.pickMask(textAndCaret).apply(textAndCaret)

        with(field) {
            setText(result.formattedText.string)
            setSelection(result.formattedText.caretPosition)
        }

        return result
    }

    /**
     * Создать заполнитель.
     *
     * @return Строка-заполнитель.
     */
    fun placeholder(): String = this.primaryMask.placeholder()

    /**
     * Минимальная длина текста внутри поля для заполнения всех обязательных символов в маске.
     *
     * @return Минимальное удовлетворительное количество символов внутри текстового поля.
     */
    fun acceptableTextLength(): Int = this.primaryMask.acceptableTextLength()

    /**
     *  Максимальная длина текста внутри поля.
     *
     *  @return Общее доступное количество обязательных и необязательных символов внутри текстового поля.
     */
    fun totalTextLength(): Int = this.primaryMask.totalTextLength()

    /**
     * Минимальная длина извлеченного значения с заполненными всеми обязательными символами.
     *
     * @return Минимальное удовлетворительное количество символов в извлеченном значении.
     */
    fun acceptableValueLength(): Int = this.primaryMask.acceptableValueLength()

    /**
     * Максимальная длина извлекаемого значения.
     *
     * @return Общее доступное количество обязательных и дополнительных символов для извлеченного значения.
     */
    fun totalValueLength(): Int = this.primaryMask.totalValueLength()

    override fun afterTextChanged(edit: Editable?) {
        this.field.get()?.removeTextChangedListener(this)
        edit?.replace(0, edit.length, this.afterText)
        this.field.get()?.setSelection(this.caretPosition)
        this.field.get()?.addTextChangedListener(this)
        this.listener?.afterTextChanged(edit)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        this.listener?.beforeTextChanged(s, start, count, after)
    }

    override fun onTextChanged(text: CharSequence, cursorPosition: Int, before: Int, count: Int) {
        val isDeletion: Boolean = before > 0 && count == 0
        val useAutocomplete = if (isDeletion) false else this.autocomplete
        val useAutoskip = if (isDeletion) this.autoskip else false
        val caretGravity =
            if (isDeletion) CaretString.CaretGravity.BACKWARD(useAutoskip) else CaretString.CaretGravity.FORWARD(
                useAutocomplete)

        val caretPosition = if (isDeletion) cursorPosition else cursorPosition + count
        val textAndCaret = CaretString(text.toString(), caretPosition, caretGravity)

        val mask: Mask = this.pickMask(textAndCaret)
        val result: Mask.Result = mask.apply(textAndCaret)

        this.afterText = result.formattedText.string
        this.caretPosition = result.formattedText.caretPosition

        this.valueListener?.onTextChanged(result.complete, result.extractedValue, afterText)
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (this.autocomplete && hasFocus) {
            val text: String = if (this.field.get()?.text!!.isEmpty()) {
                ""
            } else {
                this.field.get()?.text.toString()
            }

            val textAndCaret =
                CaretString(text, text.length, CaretString.CaretGravity.FORWARD(this.autocomplete))

            val result: Mask.Result =
                this.pickMask(textAndCaret).apply(textAndCaret)

            this.afterText = result.formattedText.string
            this.caretPosition = result.formattedText.caretPosition
            this.field.get()?.setText(afterText)
            this.field.get()?.setSelection(result.formattedText.caretPosition)
            this.valueListener?.onTextChanged(result.complete, result.extractedValue, afterText)
        }
    }

    private fun pickMask(
        text: CaretString,
    ): Mask {
        if (this.affineFormats.isEmpty()) return this.primaryMask

        data class MaskAffinity(val mask: Mask, val affinity: Int)

        val primaryAffinity: Int = this.calculateAffinity(this.primaryMask, text)

        val masksAndAffinities: MutableList<MaskAffinity> = ArrayList()
        for (format in this.affineFormats) {
            val mask: Mask = this.maskGetOrCreate(format, this.customNotations)
            val affinity: Int = this.calculateAffinity(mask, text)
            masksAndAffinities.add(MaskAffinity(mask, affinity))
        }

        masksAndAffinities.sortByDescending { it.affinity }

        var insertIndex: Int = -1

        for ((index, maskAffinity) in masksAndAffinities.withIndex()) {
            if (primaryAffinity >= maskAffinity.affinity) {
                insertIndex = index
                break
            }
        }

        if (insertIndex >= 0) {
            masksAndAffinities.add(insertIndex, MaskAffinity(this.primaryMask, primaryAffinity))
        } else {
            masksAndAffinities.add(MaskAffinity(this.primaryMask, primaryAffinity))
        }

        return masksAndAffinities.first().mask
    }

    private fun maskGetOrCreate(format: String, customNotations: List<Notation>): Mask =
        if (this.rightToLeft) {
            RTLMask.getOrCreate(format, customNotations)
        } else {
            Mask.getOrCreate(format, customNotations)
        }

    private fun calculateAffinity(
        mask: Mask,
        text: CaretString,
    ): Int {
        return this.affinityCalculationStrategy.calculateAffinityOfMask(
            mask,
            text
        )
    }

    companion object {
        /**
         * Создайте экземпляр MaskedTextChangedListener и назначьте его как поля TextWatcher и onFocusChangeListener.
         */
        fun installOn(
            editText: EditText,
            primaryFormat: String,
            valueListener: ValueListener? = null,
        ): MaskedTextChangedListener = installOn(
            editText,
            primaryFormat,
            emptyList(),
            AffinityCalculationStrategy.WHOLE_STRING,
            valueListener
        )

        /**
         * Создайте экземпляр MaskedTextChangedListener и назначьте его как поля TextWatcher и onFocusChangeListener.
         */
        fun installOn(
            editText: EditText,
            primaryFormat: String,
            affineFormats: List<String> = emptyList(),
            affinityCalculationStrategy: AffinityCalculationStrategy = AffinityCalculationStrategy.WHOLE_STRING,
            valueListener: ValueListener? = null,
        ): MaskedTextChangedListener = installOn(
            editText,
            primaryFormat,
            affineFormats,
            emptyList(),
            affinityCalculationStrategy,
            true,
            false,
            null,
            valueListener
        )

        /**
         * Создайте экземпляр MaskedTextChangedListener и назначьте его как поля TextWatcher и onFocusChangeListener.
         */
        fun installOn(
            editText: EditText,
            primaryFormat: String,
            affineFormats: List<String> = emptyList(),
            customNotations: List<Notation> = emptyList(),
            affinityCalculationStrategy: AffinityCalculationStrategy = AffinityCalculationStrategy.WHOLE_STRING,
            autocomplete: Boolean = true,
            autoskip: Boolean = false,
            listener: TextWatcher? = null,
            valueListener: ValueListener? = null,
        ): MaskedTextChangedListener {
            val maskedListener = MaskedTextChangedListener(
                primaryFormat,
                affineFormats,
                customNotations,
                affinityCalculationStrategy,
                autocomplete,
                autoskip,
                editText,
                listener,
                valueListener
            )
            editText.addTextChangedListener(maskedListener)
            editText.onFocusChangeListener = maskedListener
            return maskedListener
        }
    }

}
