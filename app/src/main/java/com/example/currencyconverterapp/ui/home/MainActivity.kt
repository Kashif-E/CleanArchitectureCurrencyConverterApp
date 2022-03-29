package com.example.currencyconverterapp.ui.home

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
import com.bumptech.glide.Glide
import com.example.currencyconverterapp.R
import com.example.currencyconverterapp.adapters.ExchangeRatesAdapter
import com.example.currencyconverterapp.base.BaseActivity
import com.example.currencyconverterapp.data.local.models.CurrencyNamesEntity
import com.example.currencyconverterapp.data.local.models.CurrencyRatesEntity
import com.example.currencyconverterapp.data.remote.DataState
import com.example.currencyconverterapp.databinding.ActivityMainBinding
import com.example.currencyconverterapp.utils.Constants
import com.example.currencyconverterapp.utils.gone
import com.example.currencyconverterapp.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.ArrayList
import com.example.currencyconverterapp.data.remote.DataState.CustomMessages.*
import com.example.currencyconverterapp.utils.flowWithLifecycle
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var currencyEntities: MutableList<CurrencyNamesEntity> = ArrayList<CurrencyNamesEntity>()
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

    private fun collectFlows() {
        lifecycleScope.launch {
        launch {
            viewModel.responseMessage.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect {
                showSnackbar(it, binding.rootView)
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


        val currenciesObserver = Observer<List<CurrencyNamesEntity>> { response ->
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
        viewModel.currenciesLiveData.observe(this, currenciesObserver)

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
            binding.etAmount.text?.trim().toString() == ".") {
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