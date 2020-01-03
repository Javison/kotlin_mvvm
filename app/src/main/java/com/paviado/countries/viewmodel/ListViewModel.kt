package com.paviado.countries.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.paviado.countries.di.DaggerApiComponent
import com.paviado.countries.model.CountriesService
import com.paviado.countries.model.Country
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ListViewModel: ViewModel() {

    @Inject
    lateinit var countriesService: CountriesService

    init {
        DaggerApiComponent.create().inject(this)
    }

    private val disponsable = CompositeDisposable()

    val countries = MutableLiveData<List<Country>>();
    val countryLoadError = MutableLiveData<Boolean>();
    val loading = MutableLiveData<Boolean>();

    fun refresh() {
        fetchCountries();
    }

    private fun fetchCountries() {

        loading.value = true
        disponsable.add(
            countriesService.getCountries()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableSingleObserver<List<Country>>() {
                    override fun onSuccess(value: List<Country>?) {
                        countries.value = value
                        countryLoadError.value = false
                        loading.value = false
                    }

                    override fun onError(e: Throwable?) {
                        countryLoadError.value = true
                        loading.value = false
                    }

                })
        )

        /*val mockData: List<Country> = listOf(Country("CountryA"),
            Country("CountryB"),
            Country("CountryC"),
            Country("CountryD"),
            Country("CountryE")
        )

        countryLoadError.value = false;
        loading.value = false;
        countries.value = mockData;*/
    }

    override fun onCleared() {
        super.onCleared()
        disponsable.clear()
    }
}