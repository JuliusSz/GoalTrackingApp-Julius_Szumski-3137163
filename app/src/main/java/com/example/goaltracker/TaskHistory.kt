package com.example.goaltracker

import DatabaseManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.util.Date

class TaskHistory : ComponentActivity() {

    private var listOfGoals = ArrayList<Goal>()
    private lateinit var  databaseManager :DatabaseManager
    private lateinit var  database: SQLiteDatabase
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                Text(text = "Past Goals",fontSize = 30.sp,
                    modifier = Modifier.padding(20.dp))
                LazyColumn {                                                          // Creates A scrollable Column for the Goal Items
                    items(listOfGoals.size) { goal ->
                        CreateGoalItem(goal = listOfGoals[goal])
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Navigation()
                }
            }
        databaseManager = DatabaseManager(this, "tasks.db",null , version = 1)
        database = databaseManager.writableDatabase
    }
    @Composable
    fun CreateGoalItem(goal: Goal){                                         //Creates an Item to display Archived Goals
        Row(
            modifier = Modifier.border( 2.dp,color = Color.Black,  shape = RoundedCornerShape(4.dp)).padding(10.dp)
        ) {
            val thisItemGoal = goal
            Text(text = goal.title, Modifier.fillMaxWidth(0.75f))
        }
    }

    override fun onResume(){                                                //refreshes the temporary storageon Resume
        super.onResume()
        listOfGoals = retriveArchivedData()


    }
    private fun retriveArchivedData() : ArrayList<Goal>{                    // Retrieves Archived Goals from the Database,
        val table = "Tasks"
        val columns: Array<String> = arrayOf("Id","title","description","Goaltype","EndDate","Progress","taskGoal","completed","Archived")
        val cursor: Cursor = database.query(table,columns, "Archived = ?", arrayOf("1"),null,null, null )
        val goalList = ArrayList<Goal>()

        if(cursor.moveToFirst())
            do{
                var dateString: String =cursor.getString(4)
                var glType: GoalType = GoalType.valueOf(cursor.getString(3))
                var date: Date = Date(dateString)
                var addedGoal= Goal(cursor.getInt(0),cursor.getString(1),cursor.getString(2),glType,cursor.getInt(6),date)
                goalList.add(addedGoal)

            }while(cursor.moveToNext())
        return goalList
    }
    @Preview
    @Composable
    fun Navigation(){                                                                               //Adds Navigation, that allows the User to Return to the Main Activity
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