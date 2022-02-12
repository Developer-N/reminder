package ir.namoo.reminder.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.namoo.reminder.REMIND_ID
import ir.namoo.reminder.cancelSchedule
import ir.namoo.reminder.db.Word
import ir.namoo.reminder.ui.RemindActivity
import ir.namoo.reminder.ui.WordViewModel
import kotlinx.coroutines.channels.Channel
import java.sql.Date
import java.text.SimpleDateFormat


@SuppressLint("SimpleDateFormat")
@Composable
fun WordItem(index: Int, word: Word, channel: Channel<String>) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current


    Card(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .clickable(enabled = true) {
                context.startActivity(Intent(context, RemindActivity::class.java).apply {
                    putExtra(REMIND_ID, word.id)
                })
            },
        elevation = 5.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp, 5.dp)
        ) {
            Text(text = (index + 1).toString(), Modifier.width(20.dp), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = word.word, Modifier.width(150.dp))
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = SimpleDateFormat("yyyy-MM-dd").format(Date(word.insertDate)))
            Spacer(modifier = Modifier.size(10.dp))
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colors.error
                )
            }
            IconButton(onClick = { showUpdateDialog = true }) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colors.primary
                )
            }
            if (showDeleteDialog)
                DeleteDialog(
                    word = word,
                    showDeleteDialog,
                    onDismiss = { showDeleteDialog = false },
                    channel = channel
                )
            if (showUpdateDialog)
                UpdateDialog(
                    word = word,
                    showUpdateDialog,
                    onDismiss = { showUpdateDialog = false },
                    channel = channel
                )
        }
    }
}

@Composable
fun DeleteDialog(
    word: Word,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    vm: WordViewModel = viewModel(),
    channel: Channel<String>
) {
    val context = LocalContext.current
    if (showDialog)
        AlertDialog(onDismissRequest = { onDismiss() },
            title = { Text(text = "Warning") },
            text = { Text(text = "Are you sure for delete (${word.word})?") },
            buttons = {
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(5.dp)) {
                    TextButton(onClick = {
                        for (i in 0..4)
                            cancelSchedule(context, word.id * 5 + i)
                        vm.deleteWord(word)
                        channel.trySend("Delete success!")
                        onDismiss()
                    }) {
                        Text(text = "Yes", color = MaterialTheme.colors.error)
                    }
                    TextButton(onClick = { onDismiss() }) {
                        Text(text = "No")
                    }
                }
            }
        )
}

@Composable
fun UpdateDialog(
    word: Word,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    vm: WordViewModel = viewModel(),
    channel: Channel<String>
) {
    var w by remember { mutableStateOf(word.word) }
    if (showDialog)
        AlertDialog(onDismissRequest = { onDismiss() },
            title = { Text(text = "Warning") },
            text = {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = w,
                    onValueChange = { w = it })
            },
            buttons = {
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(5.dp)) {
                    Button(onClick = {
                        if (w.isNotEmpty()) {
                            word.word = w
                            vm.updateWord(word)
                            channel.trySend("Update success!")
                            onDismiss()
                        }
                    }) {
                        Text(text = "Save")
                    }
                    TextButton(onClick = { onDismiss() }) {
                        Text(text = "Cancel")
                    }
                }
            }
        )
}