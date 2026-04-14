package uk.ac.tees.mad.minicart.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.tees.mad.minicart.ViewModel.OrderScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    orderState: OrderScreenState,
    onResetOrderState: () -> Unit,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onClearCacheClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LaunchedEffect(orderState.success) {
        if (orderState.success) {
            Toast.makeText(context, "Cache cleared successfully", Toast.LENGTH_SHORT).show()
            onResetOrderState()
        }
    }

    LaunchedEffect(orderState.error) {
        if (orderState.error != null) {
            Toast.makeText(context, "Error clearing cache: ${orderState.error}", Toast.LENGTH_SHORT).show()
            onResetOrderState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsItem(
                title = "Clear cart cache",
                icon = Icons.Default.Delete,
                onClick = onClearCacheClick,
                color = MaterialTheme.colorScheme.onSurface
            )

            SettingsItem(
                title = "Logout",
                icon = Icons.Default.ExitToApp,
                onClick = onLogoutClick,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    color: Color
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                ),
                color = color
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MaterialTheme {
        SettingsScreen(
            orderState = OrderScreenState(),
            onResetOrderState = {},
            onBackClick = {},
            onLogoutClick = {},
            onClearCacheClick = {},
            modifier = Modifier
        )
    }
}
