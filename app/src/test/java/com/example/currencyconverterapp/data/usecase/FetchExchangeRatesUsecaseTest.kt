package com.example.currencyconverterapp.data.usecase

import com.example.currencyconverterapp.MockTestUtil
import com.example.currencyconverterapp.data.local.repository.LocalRepository
import com.example.currencyconverterapp.data.repository.Repository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FetchExchangeRatesUsecaseTest {

    @MockK
    private lateinit var repository: Repository

    @MockK
    private lateinit var stringUtils: StringUtils

    @MockK
    private lateinit var localRepository: LocalRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `test invoking FetchCurrencyRatesUsecase gives CurrenciesDTO`() = runBlocking {
        // Given
        val fetchExchangeRatesUsecase = FetchExchangeRatesUsecase(repository,stringUtils,localRepository)
        val givenExchangeRatesDTO = MockTestUtil.getMockCurrencyRates()

        // When
        coEvery { repository.getExchangeRates() }
            .returns(flowOf(DataState.success(givenExchangeRatesDTO)))

        // Invoke
        val exchangeRatesDTO = fetchExchangeRatesUsecase("PKR",12.2)

        // Then
        MatcherAssert.assertThat(exchangeRatesDTO, CoreMatchers.notNullValue())
    }
}