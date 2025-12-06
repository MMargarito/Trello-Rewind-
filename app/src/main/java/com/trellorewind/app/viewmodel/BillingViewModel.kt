package com.trellorewind.app.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BillingViewModel : ViewModel() {
    
    companion object {
        const val PRODUCT_ID_UNLIMITED_BOARDS = "unlimited_boards_lifetime"
        const val FREE_BOARD_LIMIT = 3
    }
    
    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()
    
    private val _purchaseState = MutableStateFlow<PurchaseState>(PurchaseState.Idle)
    val purchaseState: StateFlow<PurchaseState> = _purchaseState.asStateFlow()
    
    private var billingClient: BillingClient? = null
    private var productDetails: ProductDetails? = null
    
    fun initializeBilling(activity: Activity) {
        billingClient = BillingClient.newBuilder(activity)
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        handlePurchase(purchase)
                    }
                }
            }
            .enablePendingPurchases()
            .build()
        
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProductDetails()
                    queryExistingPurchases()
                }
            }
            
            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request
            }
        })
    }
    
    private fun queryProductDetails() {
        viewModelScope.launch {
            val productList = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(PRODUCT_ID_UNLIMITED_BOARDS)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            )
            
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()
            
            billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    productDetails = productDetailsList.firstOrNull()
                }
            }
        }
    }
    
    private fun queryExistingPurchases() {
        viewModelScope.launch {
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
            
            billingClient?.queryPurchasesAsync(params) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val hasPremium = purchases.any { 
                        it.products.contains(PRODUCT_ID_UNLIMITED_BOARDS) &&
                        it.purchaseState == Purchase.PurchaseState.PURCHASED
                    }
                    _isPremium.value = hasPremium
                }
            }
        }
    }
    
    fun launchPurchaseFlow(activity: Activity) {
        val details = productDetails
        if (details == null) {
            _purchaseState.value = PurchaseState.Error("Product not available")
            return
        }
        
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(details)
                .build()
        )
        
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        
        billingClient?.launchBillingFlow(activity, billingFlowParams)
    }
    
    private fun handlePurchase(purchase: Purchase) {
        viewModelScope.launch {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (!purchase.isAcknowledged) {
                    acknowledgePurchase(purchase)
                }
                _isPremium.value = true
                _purchaseState.value = PurchaseState.Success
            } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                _purchaseState.value = PurchaseState.Pending
            }
        }
    }
    
    private fun acknowledgePurchase(purchase: Purchase) {
        viewModelScope.launch {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            
            billingClient?.acknowledgePurchase(params) { billingResult ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _isPremium.value = true
                }
            }
        }
    }
    
    fun canCreateBoard(currentBoardCount: Int): Boolean {
        return _isPremium.value || currentBoardCount < FREE_BOARD_LIMIT
    }
    
    fun resetPurchaseState() {
        _purchaseState.value = PurchaseState.Idle
    }
    
    override fun onCleared() {
        super.onCleared()
        billingClient?.endConnection()
    }
    
    sealed class PurchaseState {
        object Idle : PurchaseState()
        object Pending : PurchaseState()
        object Success : PurchaseState()
        data class Error(val message: String) : PurchaseState()
    }
}

