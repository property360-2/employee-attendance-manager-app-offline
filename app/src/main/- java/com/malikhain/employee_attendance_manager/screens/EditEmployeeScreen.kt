import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun EditEmployeeScreen(employee: Employee?) {
    if (employee == null) {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Edit Employee") })
            },
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Employee not found.")
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Edit Employee") })
        },
    ) { padding ->
        Column(
            modifier = Modifier
        ) {
            // Rest of the composable content
        }
    }
} 