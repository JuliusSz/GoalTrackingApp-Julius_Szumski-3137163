package com.example.goaltracker

import DatabaseManager
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
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

    private lateinit var  databaseManager :DatabaseManager
    private lateinit var  database: SQLiteDatabase
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
                //Navigation(titleContent,descContent,)
            }
        }
        databaseManager = DatabaseManager(this, "tasks.db",null , version = 1)
        database = databaseManager.writableDatabase
    }

    @Composable
    fun Navigation(title:String,desc:String,gltype : GoalType){
        var current = LocalContext.current
        Row( modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly){
            FloatingActionButton(
                modifier =  Modifier.padding(20.dp),
                onClick = {
                    if (title !=""){
                        addData(Goal(title, desc, gltype,null))
                        var intent = Intent(current,MainActivity::class.java)

                        (intent)
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

    fun addData(task: Goal){
        val taskToBeAdded : ContentValues = ContentValues().apply{
            put("task", task as ByteArray)
        }
        database.insert("tasks",null, taskToBeAdded)
    }

    @Preview
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalStdlibApi::class)
    @Composable
    fun DropDownExample() {
        val options = GoalType.values()
        var expanded by remember { mutableStateOf(false) }
        var selectedOptionText by remember { mutableStateOf(options[0]) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            TextField(
                value = selectedOptionText.name,
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
                            selectedOptionText = selectionOption
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}