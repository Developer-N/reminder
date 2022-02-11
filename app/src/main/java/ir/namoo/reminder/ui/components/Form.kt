package ir.namoo.reminder.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.namoo.reminder.db.Word
import ir.namoo.reminder.scheduleAlarm
import ir.namoo.reminder.ui.WordViewModel
import kotlinx.coroutines.channels.Channel
import java.util.*


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ReminderForm(channel: Channel<String>, vm: WordViewModel = viewModel()) {

    Card(modifier = Modifier.padding(5.dp), elevation = 5.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var word by remember { mutableStateOf("") }
            val keyboardController = LocalSoftwareKeyboardController.current
            val focusManager = LocalFocusManager.current
            val context = LocalContext.current
            OutlinedTextField(
                modifier = Modifier.width(250.dp),
                singleLine = true,
                maxLines = 1,
                value = word,
                onValueChange = { word = it },
                label = { Text(text = "word") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() })
            )
            Spacer(modifier = Modifier.size(4.dp))
            OutlinedButton(
                onClick = {
                    keyboardController?.hide()
                    if (word.isNotEmpty()) {
                        vm.insertWord(Word(word = word, insertDate = Date().time))
                        scheduleAlarm(context, word)
                        word = ""
                        focusManager.clearFocus()
                        channel.trySend("Save Success!")
                    } else {
                        channel.trySend("please enter word for save!")
                    }
                },
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Save")
            }
        }
    }
}//end of