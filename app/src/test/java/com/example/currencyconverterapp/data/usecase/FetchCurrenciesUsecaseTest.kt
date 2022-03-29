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
class FetchCurrenciesUsecaseTest {

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
    fun `test invoking FetchCurrencyNamesUsecase gives CurrenciesDTO`() = runBlocking {
        // Given
        val fetchCurrencyNamesUsecase = FetchCurrenciesUsecase(repository,stringUtils,localRepository)
        val givenCurrencyDTO = MockTestUtil.getMockCurrencyDTO()

        // When
        coEvery { repository.getCurrencies() }
            .returns(flowOf(DataState.success(givenCurrencyDTO)))

        // Invoke
        val currencyDTO = fetchCurrencyNamesUsecase()

        // Then
        MatcherAssert.assertThat(currencyDTO, CoreMatchers.notNullValue())
    }
}