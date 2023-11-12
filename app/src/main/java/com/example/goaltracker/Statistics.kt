package com.example.goaltracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.goaltracker.ui.theme.GoalTrackerTheme

class Statistics : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                Text(text = "This is the Statistics Page")
                Spacer(modifier = Modifier.weight(1f))
                Navigation()
            }
        }
    }


    @Preview
    @Composable
    fun Navigation(){
        var current = LocalContext.current
        Row( modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly){
            Button(
                modifier = Modifier.fillMaxWidth(0.5f),
                onClick = {
                    var intent = Intent(current,MainActivity::class.java)
                    startActivity(intent)},
                shape = RectangleShape
            ) {
                Text(text = "Goals")
            }
            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                onClick = {
                    var intent = Intent(current,Statistics::class.java)
                    startActivity(intent)
                },
                shape = RectangleShape
            ) {
                Text(text = "Statistics")

            }
        }
    }
}

