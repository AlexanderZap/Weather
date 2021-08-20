package ru.zapashnii.weather.domain.interactors.base

/** Реализуют UseCase, которые должны очищать ресурсы. */
interface IDispatchUseCase {
    /** Очистить ресурсы */
    fun dispatch()
}