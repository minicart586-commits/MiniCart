package uk.ac.tees.mad.minicart.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uk.ac.tees.mad.minicart.data.Repo
import uk.ac.tees.mad.minicart.model.ResultState
import uk.ac.tees.mad.minicart.model.UserData
import uk.ac.tees.mad.minicart.model.productItem
import uk.ac.tees.mad.minicart.model.CartItem

data class SignUpScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userdata: String? = null,
    val success: Boolean = false
)

data class LogInScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userdata: String? = null,
    val success: Boolean = false
)

data class ProductsScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val products: List<productItem>? = null,
    val success: Boolean = false
)

data class ProductItemScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val productItem: productItem? = null,
    val success: Boolean = false
)

data class OrderScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class AppViewModel(
    val repo: Repo
) : ViewModel() {


    private val _loginScreenState = mutableStateOf(LogInScreenState())
    val loginScreenState = _loginScreenState

    fun loginUser(userData: UserData) {
        viewModelScope.launch {
            repo.loginuserwithemailandpassword(userData).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _loginScreenState.value = LogInScreenState(isLoading = true)
                    }
                    is ResultState.Succes -> {
                        _loginScreenState.value = LogInScreenState(
                            success = true,
                            userdata = result.data
                        )
                    }
                    is ResultState.error -> {
                        _loginScreenState.value = LogInScreenState(
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun resetLoginState() {
        _loginScreenState.value = LogInScreenState()
    }



    private val _signupScreenState = mutableStateOf(SignUpScreenState())
    val signupScreenState = _signupScreenState

    fun  registerUser(userData: UserData) {
        viewModelScope.launch {
            repo.registeruserwithemailandpassword(userData).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _signupScreenState.value = SignUpScreenState(isLoading = true)
                    }
                    is ResultState.Succes -> {
                        _signupScreenState.value = SignUpScreenState(
                            success = true,
                            userdata = result.data
                        )
                    }
                    is ResultState.error -> {
                        _signupScreenState.value = SignUpScreenState(
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun resetSignupState() {
        _signupScreenState.value = SignUpScreenState()
    }

    private val _productsScreenState = mutableStateOf(ProductsScreenState())
    val productsScreenState = _productsScreenState

    fun getProducts() {
        viewModelScope.launch {
            repo.getproducts().collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _productsScreenState.value = ProductsScreenState(isLoading = true)
                    }
                    is ResultState.Succes -> {
                        _productsScreenState.value = ProductsScreenState(
                            success = true,
                            products = result.data
                        )
                    }
                    is ResultState.error -> {
                        _productsScreenState.value = ProductsScreenState(
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    private val _productItemScreenState = mutableStateOf(ProductItemScreenState())
    val productItemScreenState = _productItemScreenState

    fun getProductItem(id: Int) {
        viewModelScope.launch {
            repo.getproductItem(id).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _productItemScreenState.value = ProductItemScreenState(isLoading = true)
                    }
                    is ResultState.Succes -> {
                        _productItemScreenState.value = ProductItemScreenState(
                            success = true,
                            productItem = result.data
                        )
                    }
                    is ResultState.error -> {
                        _productItemScreenState.value = ProductItemScreenState(
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    private val _cartItems = mutableStateOf<List<CartItem>>(emptyList())
    val cartItems = _cartItems

    fun addToCart(product: productItem) {
        val currentItems = _cartItems.value.toMutableList()
        val existingItem = currentItems.find { it.product.id == product.id }
        if (existingItem != null) {
            val index = currentItems.indexOf(existingItem)
            currentItems[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            currentItems.add(CartItem(product))
        }
        _cartItems.value = currentItems
    }

    fun removeFromCart(product: productItem) {
        val currentItems = _cartItems.value.toMutableList()
        val existingItem = currentItems.find { it.product.id == product.id }
        if (existingItem != null) {
            if (existingItem.quantity > 1) {
                val index = currentItems.indexOf(existingItem)
                currentItems[index] = existingItem.copy(quantity = existingItem.quantity - 1)
            } else {
                currentItems.remove(existingItem)
            }
        }
        _cartItems.value = currentItems
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    private val _orderState = mutableStateOf(OrderScreenState())
    val orderState = _orderState

    fun placeOrder() {
        viewModelScope.launch {
            repo.placeOrder(_cartItems.value).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _orderState.value = OrderScreenState(isLoading = true)
                    }
                    is ResultState.Succes -> {
                        _orderState.value = OrderScreenState(success = true)
                        clearCart()
                    }
                    is ResultState.error -> {
                        _orderState.value = OrderScreenState(error = result.message)
                    }
                }
            }
        }
    }

    fun resetOrderState() {
        _orderState.value = OrderScreenState()
    }
}