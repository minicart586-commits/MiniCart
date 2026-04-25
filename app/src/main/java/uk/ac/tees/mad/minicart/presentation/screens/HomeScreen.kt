package uk.ac.tees.mad.minicart.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.tees.mad.minicart.ViewModel.ProductsScreenState
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import uk.ac.tees.mad.minicart.ViewModel.AppViewModel
import uk.ac.tees.mad.minicart.model.productItem
import uk.ac.tees.mad.minicart.model.CartItem
import uk.ac.tees.mad.minicart.ui.theme.PrimaryTeal

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onCartClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.productsScreenState
    val cartItems by viewModel.cartItems

    LaunchedEffect(key1 = Unit) {
        viewModel.getProducts()
    }

    HomeScreenContent(
        state = state,
        cartItems = cartItems,
        onCartClick = onCartClick,
        onAddToCart = { viewModel.addToCart(it) },
        onSearchQueryChange = { /* Search is handled locally in Content for now */ },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    state: ProductsScreenState,
    cartItems: List<CartItem>,
    onCartClick: () -> Unit,
    onAddToCart: (productItem) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedProduct by remember { mutableStateOf<productItem?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold { paddingValues ->
        Column(
            modifier = modifier
                .padding(top = paddingValues.calculateTopPadding())
                .fillMaxSize()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "MiniCart",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal
                    )
                )
                IconButton(onClick = onCartClick) {
                    BadgedBox(
                        badge = {
                            if (cartItems.isNotEmpty()) {
                                Badge(containerColor = PrimaryTeal) {
                                    Text(cartItems.sumOf { it.quantity }.toString(), color = Color.White)
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = PrimaryTeal)
                    }
                }
            }


            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    onSearchQueryChange(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search Products...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryTeal,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Box(modifier = Modifier.weight(1f)) {
                when {
                    state.isLoading -> {
                        Row(modifier = Modifier.align(Alignment.Center)) {
                            CircularProgressIndicator(
                                color = PrimaryTeal
                            )
                        }
                    }
                    state.error != null -> {
                        Text(
                            text = state.error ?: "Unknown Error",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    state.products != null -> {
                        val filteredProducts = state.products!!.filter { 
                            it.title.contains(searchQuery, ignoreCase = true) 
                        }
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(filteredProducts) { product ->
                                HorizontalProductCard(
                                    product = product,
                                    onClick = { 
                                        selectedProduct = product
                                        showDialog = true
                                    },
                                    onAddToCart = { 
                                        onAddToCart(product)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showDialog && selectedProduct != null) {
            ProductDetailDialog(
                product = selectedProduct!!,
                onDismiss = { showDialog = false },
                onAddToCart = { 
                    onAddToCart(selectedProduct!!)
                    showDialog = false 
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val mockProducts = listOf(
        productItem(id = 1, title = "Product 1", price = 10.0, description = "Description 1", image = "", category = "Category 1"),
        productItem(id = 2, title = "Product 2", price = 20.0, description = "Description 2", image = "", category = "Category 2")
    )
    uk.ac.tees.mad.minicart.ui.theme.MiniCartTheme {
        HomeScreenContent(
            state = ProductsScreenState(products = mockProducts),
            cartItems = emptyList(),
            onCartClick = {},
            onAddToCart = {},
            onSearchQueryChange = {}
        )
    }
}

@Composable
fun HorizontalProductCard(
    product: productItem,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            AsyncImage(
                model = product.image,
                contentDescription = product.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${product.price}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryTeal
                        )
                    )

                    Button(
                        onClick = onAddToCart,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Add to Cart", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductDetailDialog(
    product: productItem,
    onDismiss: () -> Unit,
    onAddToCart: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = product.image,
                    contentDescription = product.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(bottom = 16.dp)
                )

                Text(
                    text = product.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "$${product.price}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryTeal
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Close")
                    }
                    Button(
                        onClick = onAddToCart,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                    ) {
                        Text("Add to Cart")
                    }
                }
            }
        }
    }
}
