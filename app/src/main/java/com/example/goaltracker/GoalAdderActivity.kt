package com.example.goaltracker

import DatabaseManager
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.experimental.Experimental
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.goaltracker.ui.theme.GoalTrackerTheme
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Calendar
import java.util.Date

class GoalAdderActivity : ComponentActivity() {

    private lateinit var  databaseManager :DatabaseManager
    private lateinit var  database: SQLiteDatabase
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {                                            // Creates an Interface to easily add Goals
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
                    OutlinedTextField(value = titleContent, onValueChange = {titleContent = it}, modifier = Modifier.fillMaxWidth(0.90f))  //text field for title
                }
                val options = GoalType.values()
                var selectedOptionText by remember {
                    mutableStateOf(options[0])
                }
                Row( modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly){
                   goalPicker(options,selectedOptionText)                                           //Dropdown Menu to Pick the type of Goal the User wants to create
                }
                Text(text = "Description:")
                var descContent by remember{
                    mutableStateOf("")
                }
                Row( modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly){
                    OutlinedTextField(value = descContent, onValueChange = {descContent = it}, modifier = Modifier.fillMaxWidth(0.90f).then(Modifier.fillMaxHeight(0.75f)) )//text field for Description
                }
                val selectedDate = remember{
                    mutableStateOf(Date())
                }
                Row( modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly){
                    calender(LocalContext.current,selectedDate)                                     // Adds a Calender, so the User can pick a Deadline for thier Goal
                }
                Navigation(titleContent,descContent,selectedOptionText,selectedDate.value)          // Adds the Save and Cancel Button
            }
        }
        databaseManager = DatabaseManager(this, "tasks.db",null , version = 1)
        database = databaseManager.writableDatabase
    }
    @Composable
    fun calender(context: Context, date: MutableState<Date>){                                       //Creates the Calender and temporarily stores the chosen Date
        val year: Int
        val month: Int
        val day: Int

        val thisCalender = Calendar.getInstance()
        year = thisCalender.get(Calendar.YEAR)
        month = thisCalender.get(Calendar.MONTH)
        day = thisCalender.get(Calendar.DAY_OF_MONTH)
        thisCalender.time = Date()


        val datePickerDialog = DatePickerDialog(context, {_: DatePicker, year: Int, month: Int, dayOfMonth: Int -> date.value =  Date(year,month,dayOfMonth)
        },year,month,day)

        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center){
            Text(text = "Selected Date: " + date.value.date+ "/" + date.value.month +"/" + date.value.year)
            Spacer(Modifier.size(16.dp))
            Button(onClick = { datePickerDialog.show() }) {
                Text(text = "Select a Deadline")
            }
        }
    }

    @Composable
    fun Navigation(title:String,desc:String,gltype : GoalType, endDate: Date){                                  //Adds Navigation
        val text = remember { mutableStateOf("foo") }
        var current = LocalContext.current
        Row( modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly){
            FloatingActionButton(
                modifier =  Modifier.padding(20.dp),
                onClick = {
                    if (title !=""){                                                                            // Saves the new Goal to the Database if the Title is not Empty
                        endDate.year -= 1900;
                        val goalToAdd =Goal(0, title,desc,gltype,null,endDate)
                        addData(goalToAdd)
                        text.value="bar"
                        finish()                                                                                 //returns to the Main Activity
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
                    finish()                                                                                    //returns to the Main Activity
                }
            ) {
                Text(text = "Cancel")
            }
        }
    }

    private fun addData(task: Goal){                                                                            // adds Data to the Database
        val taskToBeAdded : ContentValues = ContentValues().apply{
            put("title",task.title )
            put("description",task.desc)
            put("Goaltype",task.glType.toString())
            put("EndDate",task.enddate.toString())
            put("Progress",task.progress )
            put("taskGoal",task.taskGoal )
            put("completed",task.completed)
            put("Archived",task.archived )
        }
        database.insert("tasks",null, taskToBeAdded)
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalStdlibApi::class)
    @Composable
    fun goalPicker(options: Array<GoalType>, selectedOptionsText: MutableState<GoalType>) {         //Creates a Dropdown menu
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            TextField(
                value = selectedOptionsText.value.name,
                modifier = Modifier.menuAnchor(),
                readOnly = true,
                onValueChange = {},
                label = { Text("Label") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption.name) },
                        onClick = {
                           selectedOptionsText.value = selectionOption
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}