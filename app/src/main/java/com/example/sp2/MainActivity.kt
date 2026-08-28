package com.example.sp2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.sp2.navigation.AppNavigation
import com.example.sp2.ui.theme.Sp2Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Sp2Theme {
                AppNavigation()
            }
        }
    }
}