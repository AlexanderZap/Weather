package ru.zapashnii.weather.domain.interactors.base

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Абстрактный класс для варианта использования (Interactor с точки зрения Clean Architecture).
 * Эта абстракция представляет собой исполнительную единицу для различных вариантов использования
 * (это означает, что любой вариант использования в приложении должен реализовывать этот контракт).
 *
 * По соглашению каждая реализация [UseCase] будет выполнять свою работу в фоновом потоке (kotlin coroutine)
 * и публиковать результат в потоке пользовательского интерфейса.
 *
 * @param Type      тип ожидаемого значения.
 * @param Params    тип определяющий набор параметров для выполнения метода run(…).
 */
abstract class UseCase<out Type, in Params> {
    /** Объект фонового контекста для выполнения работ. */
    var backgroundContext: CoroutineContext = Dispatchers.IO

    /** Объект UI контекста для выполнения работ. */
    var foregroundContext: CoroutineContext = Dispatchers.Main

    /** Объект, используемый для инициирования и отмены работы. */
    private var parentJob: Job = Job()

    /**
     * Абстрактная функция, имплементируемая в дочерних классах. Выполняется в фоновом контексте.
     * !!!!!! ВСЯ РАБОТА ДОЛЖНА ВЫПОЛНЯТЬСЯ ЗДЕСЬ !!!!!! чтобы можно было писать автотести на наследники этого класс.
     * @param params    Принимает объект параметризированного типа Params.
     * @return          Type – тип данных.
     */
    abstract suspend fun run(params: Params): Type?

    /**
     * Отменяет предыдущую(вызывает unsubscribe(…)) и инициирует новую работу.
     * С помощью функций launch и withContext разбивает работу между потоками(корутин контекстами).
     * Вызывает функцию run(…) в фоне. Вызывает функцию высшего порядка,
     * переданную в параметрах(onResult: ((Type>) -> Unit), в UI потоке.
     * Ничего не возвращает.
     *
     * @param params    объект параметризированного типа Params
     * @param onResult  ф-ция высшего порядка onResult(принимает Type, ничего не возвращает).
     */
    operator fun invoke(params: Params, onResult: (Type?) -> Unit = {}) {
        unsubscribe()
        parentJob = Job()

        CoroutineScope(foregroundContext + parentJob).launch {
            val result = withContext(backgroundContext) {
                run(params)
            }

            onResult(result)
        }
    }

    /** Отменяет работу. */
    private fun unsubscribe() {
        parentJob.apply {
            cancelChildren()
            cancel()
        }
    }
}