package de.dreier.mytargets.features.settings.migrate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.dreier.mytargets.features.settings.migrate.repository.Repository


class MainViewModelFactory(private val repository: Repository): ViewModelProvider.Factory {

    private lateinit var viewModel: MainViewModel

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }


}