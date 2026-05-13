package com.example.pos_pamt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pos_pamt.navigation.AppNavigation
import com.example.pos_pamt.ui.theme.Pos_PamTTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pos_PamTTheme()  {
                AppNavigation()
            }
        }
    }
}
