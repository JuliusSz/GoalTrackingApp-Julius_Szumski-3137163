package com.example.goaltracker

import android.content.Intent
import android.graphics.fonts.Font
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner.current
import androidx.activity.compose.setContent
import androidx.annotation.XmlRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.internal.composableLambdaInstance
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goaltracker.ui.theme.GoalTrackerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var listOfGoals = listOf<Goal>()
        setContent {
            Column {
                Text(
                    "Goals",
                    fontSize = 30.sp,
                    modifier = Modifier.padding(20.dp)
                )
                LazyColumn {
                    itemsIndexed(
                        listOfGoals
                    ){index, goal ->
                        CreateGoalItem(goal = goal)
                    }

                }

                Spacer(modifier = Modifier.weight(1f))
                AddGoalButton()
                Navigation()
            }


        }
    }

    @Composable
    fun CreateGoalItem(goal: Goal){
        Row {
            val thisItemGoal = goal
            Text(text = goal.title, Modifier.fillMaxWidth(0.75f))
            Button(onClick = {  }) {
               Icon(Icons.Filled.Check, "Completed" )
            }
        }
    }
    @Preview
    @Composable
    fun AddGoalButton(){
        var current = LocalContext.current
        Row (modifier = Modifier
            .fillMaxWidth()
            .then(Modifier.padding(20.dp)),
            horizontalArrangement = Arrangement.End){
            FloatingActionButton(
                onClick = {
                    var intent = Intent(current,GoalAdderActivity::class.java)
                    startActivity(intent)
                }) {
                Icon(Icons.Filled.Add, "Add a New Goal" )
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
            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth(0.5f),
                enabled = false,
                onClick = {
                    var intent = Intent(current,MainActivity::class.java)
                    startActivity(intent)
                },
                shape = RectangleShape
            ) {
                Text(text = "Goals")
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
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

class Goal(title:String, desc: String){
    var title : String = title
    var description : String = desc
    var completed = false
}