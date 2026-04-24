package uk.ac.tees.mad.minicart.presentation.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import uk.ac.tees.mad.minicart.ViewModel.AppViewModel
import uk.ac.tees.mad.minicart.model.CartItem
import uk.ac.tees.mad.minicart.model.productItem
import uk.ac.tees.mad.minicart.ui.theme.PrimaryTeal
import uk.ac.tees.mad.minicart.util.NotificationHelper
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: AppViewModel,
    onBackClick: () -> Unit
) {
    val cartItems by viewModel.cartItems
    val orderState by viewModel.orderState
    val context = LocalContext.current

    val totalPrice = remember(cartItems) {
        cartItems.sumOf { it.product.price.toDouble() * it.quantity }
    }

    var showConfirmDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    LaunchedEffect(orderState.success) {
        if (orderState.success) {
            NotificationHelper.showOrderNotification(context, "ORD-${System.currentTimeMillis()}")
            Toast.makeText(context, "Order Placed Successfully!", Toast.LENGTH_SHORT).show()
            viewModel.resetOrderState()
            onBackClick()
        }
    }

    var itemToDelete by remember { mutableStateOf<CartItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Your Cart",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryTeal)
                    }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    tonalElevation = 8.dp,
                    shadowElevation = 12.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total:",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = "$${String.format("%.2f", totalPrice)}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Black
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { showConfirmDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                            enabled = !orderState.isLoading
                        ) {
                            if (orderState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                            } else {
                                Text("Proceed to Checkout", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize().background(Color(0xFFF9F9F9))) {
            if (cartItems.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Your cart is empty", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onBackClick,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                    ) {
                        Text("Continue Shopping")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(cartItems) { item ->
                        PremiumCartItemRow(
                            item = item,
                            onAdd = { viewModel.addToCart(item.product) },
                            onRemove = { 
                                if (item.quantity > 1) {
                                    viewModel.removeFromCart(item.product)
                                } else {
                                    itemToDelete = item
                                }
                            },
                            onDeleteEntirely = {
                                itemToDelete = item
                            }
                        )
                    }
                }
            }
        }

        if (itemToDelete != null) {
            DeleteItemConfirmDialog(
                item = itemToDelete!!,
                onDismiss = { itemToDelete = null },
                onConfirm = {
                    viewModel.deleteEntireItemFromCart(itemToDelete!!.product)
                    itemToDelete = null
                }
            )
        }

        if (showConfirmDialog) {
            OrderConfirmDialog(
                cartItems = cartItems,
                total = totalPrice,
                onDismiss = { showConfirmDialog = false },
                onConfirm = { 
                    viewModel.placeOrder()
                    showConfirmDialog = false
                },
                onShare = { shareOrderSummary(context, cartItems, totalPrice) }
            )
        }
    }
}

@Composable
fun PremiumCartItemRow(
    item: CartItem,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    onDeleteEntirely: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.product.image,
                contentDescription = item.product.title,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    text = "$${item.product.price}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = PrimaryTeal,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuantitySelector(
                        quantity = item.quantity,
                        onAdd = onAdd,
                        onRemove = onRemove
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDeleteEntirely) {
                        Text("Remove", color = Color.Red.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun QuantitySelector(
    quantity: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color(0xFFF5F5F5), RoundedCornerShape(24.dp))
            .padding(4.dp)
    ) {
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .size(32.dp)
                .background(Color.White, CircleShape),
            colors = IconButtonDefaults.iconButtonColors(contentColor = PrimaryTeal)
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
        }
        
        Text(
            text = quantity.toString(),
            modifier = Modifier.padding(horizontal = 12.dp),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
            color = Color.Black
        )
        
        IconButton(
            onClick = onAdd,
            modifier = Modifier
                .size(32.dp)
                .background(PrimaryTeal, CircleShape),
            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun DeleteItemConfirmDialog(
    item: CartItem,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Remove Item?") },
        text = {
            Column {
                Text("Are you sure you want to remove this item from your cart?")
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = item.product.image,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(item.product.title, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Remove", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun OrderConfirmDialog(
    cartItems: List<CartItem>,
    total: Double,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onShare: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(PrimaryTeal, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onShare) {
                        Icon(
                            imageVector = Icons.Default.Share, 
                            contentDescription = "Share", 
                            tint = Color.White
                        )
                    }
                }
                
                Text(
                    text = "Share Order Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    cartItems.take(3).forEach { item ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(item.product.title, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                            Text("$${item.product.price}", fontWeight = FontWeight.Bold)
                        }
                    }
                    if (cartItems.size > 3) {
                        Text("... and ${cartItems.size - 3} more", fontSize = 12.sp, color = Color.Gray)
                    }
                    Divider(Modifier.padding(vertical = 8.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total:", fontWeight = FontWeight.Bold)
                        Text("$${String.format("%.2f", total)}", fontWeight = FontWeight.ExtraBold, color = PrimaryTeal)
                    }
                }

                Button(
                    onClick = onConfirm,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                ) {
                    Text("Confirm Order", fontWeight = FontWeight.Bold)
                }
                
                TextButton(onClick = onDismiss, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        }
    }
}

fun shareOrderSummary(context: Context, items: List<CartItem>, total: Double) {
    val summary = StringBuilder("MiniCart Order Summary:\n\n")
    items.forEach {
        summary.append("- ${it.product.title} (${it.quantity}x) : $${it.product.price * it.quantity}\n")
    }
    summary.append("\nTotal Amount: $${String.format("%.2f", total)}")
    
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, summary.toString())
    }
    context.startActivity(Intent.createChooser(intent, "Share via"))
}
