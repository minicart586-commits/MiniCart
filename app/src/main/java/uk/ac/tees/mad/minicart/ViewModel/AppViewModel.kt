package uk.ac.tees.mad.minicart.ViewModel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    val repo: Repo,
    private val application: Application
) : ViewModel() {

    private val gson = Gson()
    private val prefs = application.getSharedPreferences("cart_prefs", Context.MODE_PRIVATE)

    init {
        loadCart()
    }

    private val _loginScreenState = mutableStateOf(LogInScreenState())
    val loginScreenState = _loginScreenState
    val auth= FirebaseAuth.getInstance()


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

    private fun saveCart() {
        val json = gson.toJson(_cartItems.value)
        prefs.edit().putString("cart_items", json).apply()
    }

    private fun loadCart() {
        val json = prefs.getString("cart_items", null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<CartItem>>() {}.type
                val items: List<CartItem> = gson.fromJson(json, type)
                _cartItems.value = items
            } catch (e: Exception) {
                _cartItems.value = emptyList()
            }
        }
    }

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
        saveCart()
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
        saveCart()
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        saveCart()
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

    fun signout() {
        auth.signOut()
    }

    fun clearCache() {
        viewModelScope.launch {
            repo.clearCache().collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _orderState.value = OrderScreenState(isLoading = true)
                    }
                    is ResultState.Succes -> {
                        _orderState.value = OrderScreenState(success = true)
                    }
                    is ResultState.error -> {
                        _orderState.value = OrderScreenState(error = result.message)
                    }
                }
            }
        }
    }
}