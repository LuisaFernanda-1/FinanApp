package com.ufpso.finanapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ufpso.finanapp.data.local.AppDatabase
import com.ufpso.finanapp.data.repository.MovimientoRepository
import com.ufpso.finanapp.ui.navigation.AppNavigation
import com.ufpso.finanapp.ui.theme.FinanAppTema
import com.ufpso.finanapp.ui.viewmodel.FinanzasViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repositorio = MovimientoRepository(
            AppDatabase.getInstance(applicationContext).movimientoDao()
        )

        setContent {
            FinanAppTema {
                val vm: FinanzasViewModel = viewModel(factory = FinanzasViewModel.Factory(repositorio))
                AppNavigation(vm)
            }
        }
    }
}
