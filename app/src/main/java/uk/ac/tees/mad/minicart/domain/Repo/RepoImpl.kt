package uk.ac.tees.mad.minicart.domain.Repo

import android.util.Log

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import uk.ac.tees.mad.minicart.data.Repo

import uk.ac.tees.mad.minicart.model.ResultState
import uk.ac.tees.mad.minicart.model.UserData
import uk.ac.tees.mad.minicart.model.CartItem
import uk.ac.tees.mad.minicart.model.productItem

class RepoImpl: Repo {
    private val auth = FirebaseAuth.getInstance()
    override fun registeruserwithemailandpassword(
        userdata: UserData
    ): Flow<ResultState<String>> = callbackFlow {


        trySend(ResultState.Loading)

        val email = userdata.email
        val password = userdata.password

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            trySend(ResultState.error("Email or Password cannot be empty"))
            close()
            return@callbackFlow
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySend(ResultState.Succes("Registration Successful"))
                } else {
                    trySend(
                        ResultState.error(
                            task.exception?.localizedMessage ?: "Unknown error"
                        )
                    )
                }
            }

        awaitClose { close() }
    }

    override fun loginuserwithemailandpassword(
        userdata: UserData
    ): Flow<ResultState<String>> = callbackFlow {

        trySend(ResultState.Loading)

        val email = userdata.email
        val password = userdata.password

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            trySend(ResultState.error("Email or Password cannot be empty"))
            close()
            return@callbackFlow
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySend(ResultState.Succes("Login Successful"))
                } else {
                    trySend(
                        ResultState.error(
                            task.exception?.localizedMessage ?: "Unknown error"
                        )
                    )
                }
            }

        awaitClose { close() }
    }

    override fun getproducts(): Flow<ResultState<List<productItem>>> = kotlinx.coroutines.flow.flow {
        emit(ResultState.Loading)
        try {
            val response = ApiBuilder.provedApi.getProducts()
            Log.d("RepoImpl", "Products fetched successfully: ${response.size} items")
            emit(ResultState.Succes(response))
        } catch (e: Exception) {
            Log.e("RepoImpl", "Error fetching products", e)
            emit(ResultState.error(e.localizedMessage ?: "Unknown error"))
        }
    }

    override fun getproductItem(id: Int): Flow<ResultState<productItem>> = kotlinx.coroutines.flow.flow {
        emit(ResultState.Loading)
        try {
            val response = ApiBuilder.provedApi.getProductById(id)
            Log.d("RepoImpl", "Product item fetched successfully: ${response.id}")
            emit(ResultState.Succes(response))
        } catch (e: Exception) {
            Log.e("RepoImpl", "Error fetching product item $id", e)
            emit(ResultState.error(e.localizedMessage ?: "Unknown error"))
        }
    }

    override fun placeOrder(cartItems: List<CartItem>): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val user = auth.currentUser
        if (user == null) {
            trySend(ResultState.error("User not logged in"))
            close()
            return@callbackFlow
        }

        val db = FirebaseFirestore.getInstance()
        val order = hashMapOf(
            "userId" to user.uid,
            "items" to cartItems,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("orders")
            .add(order)
            .addOnSuccessListener {
                trySend(ResultState.Succes("Order placed successfully"))
                close()
            }
            .addOnFailureListener { e ->
                trySend(ResultState.error(e.localizedMessage ?: "Failed to place order"))
                close()
            }
        
        awaitClose()
    }


}