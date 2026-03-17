package uk.ac.tees.mad.minicart.data

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.minicart.model.ResultState
import uk.ac.tees.mad.minicart.model.UserData
import uk.ac.tees.mad.minicart.model.product
import uk.ac.tees.mad.minicart.model.productItem

interface Repo {
    fun registeruserwithemailandpassword(userdata: UserData): Flow<ResultState<String>>
    fun loginuserwithemailandpassword(userdata: UserData): Flow<ResultState<String>>
    fun getproducts(): Flow<ResultState<product>>
    fun getproductItem(id: Int): Flow<ResultState<productItem>>
}