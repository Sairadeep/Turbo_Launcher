package com.anxer.bottomsheet

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anxer.bottomsheet.ui.theme.BottomSheetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BottomSheetTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BottomSheetDemo()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetDemo() {
    val myContext = LocalContext.current
    val toDisplayBS = remember { mutableStateOf(false) }
    val textValue = remember { mutableStateOf("Name") }
    val buttonText = remember { mutableStateOf("Open BS") }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                // Bottom sheet
                Toast.makeText(myContext, "Button Clicked !", Toast.LENGTH_SHORT).show()
                toDisplayBS.value = true
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red
            )
        ) {
            Text(
                text = buttonText.value,
                fontSize = 18.sp
            )
            if (toDisplayBS.value) {
                ModalBottomSheet(
                    onDismissRequest = {
                        toDisplayBS.value = false
                        buttonText.value = textValue.value
                    },
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(30.dp),
                    scrimColor = Color.DarkGray,
                    sheetState = SheetState(
                        skipPartiallyExpanded = true,
                        skipHiddenState = false
                    ),
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(100.dp),
                        modifier = Modifier.padding(15.dp),
                        content = {
                            items(
                                count = 24,
                                itemContent = {
                                    Icon(
                                        Icons.TwoTone.Home,
                                        contentDescription = "",
                                        modifier = Modifier
                                            .size(100.dp)
                                            .background(Color.Gray),
                                        tint = Color.Red
                                    )
                                    /* TextField(
                                         value = textValue.value,
                                         onValueChange = { textValue.value = it },
                                         shape = RoundedCornerShape(5.dp),
                                         modifier = Modifier.align(Alignment.CenterHorizontally)
                                     )*/
                                }
                            )
                        }
                    )

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BottomSheetTheme {
        BottomSheetDemo()
    }
}