package com.example.goaltracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.goaltracker.ui.theme.GoalTrackerTheme

class GoalAdderActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Column{
                Text(text = "You can add goals here!")
                Text(text = "Title:")
                var titleContent by remember{
                    mutableStateOf("")
                }
                Row( modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly){
                    OutlinedTextField(value = titleContent, onValueChange = {titleContent = it}, modifier = Modifier.fillMaxWidth(0.90f))
                }

                Text(text = "Description:")
                var descContent by remember{
                    mutableStateOf("")
                }
                Row( modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly){
                    OutlinedTextField(value = descContent, onValueChange = {descContent = it}, modifier = Modifier.fillMaxWidth(0.90f).then(Modifier.fillMaxHeight(0.75f)) )
                }
                Spacer(modifier = Modifier.weight(1f))
                Navigation(titleContent,descContent)
            }
        }
    }

    @Composable
    fun Navigation(title:String,desc:String){
        var current = LocalContext.current
        Row( modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly){
            FloatingActionButton(
                modifier =  Modifier.padding(20.dp),
                onClick = {
                    if (title !=""){
                        var tempGoal = Goal(title, desc)
                        var intent = Intent(current,MainActivity::class.java)
                        //intent.putExtra("newgoal",tempGoal)
                        startActivity(intent)
                    }else{
                        Toast.makeText(current, "Please don't Leave the title empty", Toast.LENGTH_LONG).show()
                    }
                }
            ) {
                Text(text = "Save")
            }
            FloatingActionButton(
                modifier =  Modifier.padding(20.dp),
                onClick = {
                    var intent = Intent(current,MainActivity::class.java)
                    startActivity(intent)
                }
            ) {
                Text(text = "Cancel")
            }
        }
    }
}