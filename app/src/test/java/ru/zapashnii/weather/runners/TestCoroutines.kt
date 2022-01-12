package ru.zapashnii.weather.runners

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Rule

abstract class TestCoroutines {

    @ExperimentalCoroutinesApi
    val testDispatcher = TestCoroutineDispatcher()

    @get:Rule
    val rule = InstantTaskExecutorRule()

}