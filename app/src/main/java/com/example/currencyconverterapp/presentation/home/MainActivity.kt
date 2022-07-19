package com.example.currencyconverterapp.presentation.home

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.currencyconverterapp.R
import com.example.currencyconverterapp.presentation.adapters.ExchangeRatesAdapter
import com.example.currencyconverterapp.presentation.base.BaseActivity
import com.example.currencyconverterapp.data.local.models.CurrencyNamesEntity
import com.example.currencyconverterapp.data.local.models.CurrencyRatesEntity
import com.example.currencyconverterapp.data.remote.DataState
import com.example.currencyconverterapp.databinding.ActivityMainBinding
import com.example.currencyconverterapp.domain.domain_models.CurrencyNameDomainModel
import com.example.currencyconverterapp.domain.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.ArrayList
import com.example.currencyconverterapp.domain.utils.flowWithLifecycle
import com.example.currencyconverterapp.domain.utils.gone
import com.example.currencyconverterapp.domain.utils.visible
import com.example.currencyconverterapp.presentation.UIState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var currencyEntities: MutableList<CurrencyNameDomainModel> =
        ArrayList<CurrencyNameDomainModel>()
    private var selectedCurrency: String = Constants.DEFAULT_SOURCE_CURRENCY
    private lateinit var exchangeRatesAdapter: ExchangeRatesAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListener()
        collectFlows()
        initObservations()
    }

    fun showSnackbar(message: DataState.CustomMessages, binding: View) {

        val error = when (message) {
            is DataState.CustomMessages.emptyData -> {
                getString(R.string.no_data_found)
            }
            is DataState.CustomMessages.Timeout -> {
                getString(R.string.timeout)
            }
            is DataState.CustomMessages.ServerBusy -> {
                getString(R.string.server_is_busy)
            }

            is DataState.CustomMessages.HttpException -> {
                getString(R.string.no_internet_connection)
            }
            is DataState.CustomMessages.SocketTimeOutException -> {
                getString(R.string.no_internet_connection)
            }
            is DataState.CustomMessages.NoInternet -> {
                getString(R.string.no_internet_connection)
            }
            is DataState.CustomMessages.Unauthorized -> {
                getString(R.string.unauthorized)
            }
            is DataState.CustomMessages.InternalServerError -> {
                getString(R.string.internal_server_error)
            }
            is DataState.CustomMessages.BadRequest -> {
                getString(R.string.bad_request)
            }
            is DataState.CustomMessages.Conflict -> {
                getString(R.string.confirm)
            }
            is DataState.CustomMessages.NotFound -> {
                getString(R.string.not_found)
            }
            is DataState.CustomMessages.NotAcceptable -> {
                getString(R.string.not_acceptable)
            }
            is DataState.CustomMessages.ServiceUnavailable -> {
                getString(R.string.service_unavailable)
            }
            is DataState.CustomMessages.Forbidden -> {
                getString(R.string.forbidden)
            }

            else -> {
                "Something went Wrong."
            }
        }

        Snackbar.make(binding.rootView, error, Snackbar.LENGTH_LONG)
            .setActionTextColor(ContextCompat.getColor(this, R.color.white)).also {
                it.setAction(
                    "OK"
                ) { v ->

                    it.dismiss()
                }
            }
            .show()


    }

    private fun collectFlows() {
        viewModel.uiStateLiveData.observe(this) {
            when (it) {
                is UIState.LoadingState -> {
                    binding.ivConversionIcon.visible()
                    binding.progressBarCurrencies.visible()
                }
                is UIState.ContentState -> {
                    binding.ivConversionIcon.gone()
                    binding.progressBarCurrencies.gone()
                }
                is UIState.ErrorState -> {
                    showSnackbar(it.message, binding.rootView)
                }
            }


        }
    }

    private fun initListener() {

        binding.currenciesSpinner.onItemSelectedListener = this
        exchangeRatesAdapter = ExchangeRatesAdapter()
        binding.rvConvertedCurrencies.adapter = exchangeRatesAdapter
        binding.btnConvert.setOnClickListener {
            if (checkValidation()) {
                hideKeyboard()
                viewModel.fetchExchangeRates(
                    selectedCurrency,
                    binding.etAmount.text.toString().toDouble()
                )
            }
        }
    }

    private fun initObservations() {


        viewModel.currenciesLiveData.observe(this) { response ->
            // Update the UI, in this case
            response?.let {
                currencyEntities = response.toMutableList()
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    response.map { it.currencyCountryName }.sorted()
                )
                binding.currenciesSpinner.adapter = adapter
            }
        }


        val exchangeRatesObserver = Observer<List<CurrencyRatesEntity>> { response ->
            // Update the UI, in this case
            response?.let {
                if (response.isNotEmpty()) {
                    exchangeRatesAdapter.differ.submitList(response)
                }
            }
        }
        viewModel.exchangeRatesEntityLiveData.observe(this, exchangeRatesObserver)
    }


    private fun checkValidation(): Boolean {
        binding.etAmount.error = null
        if (binding.etAmount.text.isNullOrEmpty() ||
            binding.etAmount.text.isNullOrBlank() ||
            binding.etAmount.text?.trim().toString() == "."
        ) {
            binding.etAmount.error = getString(R.string.enter_amount_error)
            return false
        }
        return true
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        binding.currenciesSpinner.setSelection(pos)
        selectedCurrency = Constants.DEFAULT_SOURCE_CURRENCY + currencyEntities.single() {
            it.currencyCountryName == binding.currenciesSpinner.selectedItem.toString()
        }.currencyName
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}