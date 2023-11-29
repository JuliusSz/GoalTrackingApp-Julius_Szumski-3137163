package com.example.goaltracker

import DatabaseManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.util.Date

class Statistics : ComponentActivity() {

    private var listOfGoals = ArrayList<Goal>()
    private lateinit var  databaseManager :DatabaseManager
    private lateinit var  database: SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                Text(text = "you Can See your Past Goals here")
                Spacer(modifier = Modifier.weight(1f))
                Navigation()
                LazyColumn {
                    itemsIndexed(
                        listOfGoals
                    ){index, goal ->
                        if(goal.archived){
                            CreateGoalItem(goal = goal)
                        }

                    }

                }
            }
        }
    }
    @Composable
    fun CreateGoalItem(goal: Goal){
        Row(
            modifier = Modifier.border( 2.dp,color = Color.Black,  shape = RoundedCornerShape(4.dp)).padding(
                PaddingValues(horizontal = 8.dp)
            )
        ) {
            val thisItemGoal = goal
            Text(text = goal.title, Modifier.fillMaxWidth(0.75f))
        }
    }

    override fun onResume(){
        super.onResume()
        listOfGoals = retriveData()

    }
    private fun retriveData() : ArrayList<Goal>{
        val table = "tasks"
        val columns: Array<String> = arrayOf("Id","task")
        val cursor: Cursor = database.query(table,columns,null,null,null,null, null )
        val goalList = ArrayList<Goal>()
        cursor.moveToFirst()
        for (i in 0 until cursor.count){
            goalList.add(cursor.getBlob(1) as Goal)
        }
        return goalList
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
                    finish()
                    },
                shape = RectangleShape
            ) {
                Text(text = "Goals")
            }
            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                onClick = {
                    finish()
                },
                shape = RectangleShape
            ) {
                Text(text = "History")

            }
        }
    }
}

