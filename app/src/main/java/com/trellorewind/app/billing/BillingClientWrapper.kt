package com.trellorewind.app.billing

import android.app.Activity
import com.android.billingclient.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Wrapper class for Google Play Billing operations
 * Handles connection, product queries, and purchase flows
 */
class BillingClientWrapper(
    private val activity: Activity,
    private val onPurchaseSuccess: () -> Unit,
    private val onPurchaseError: (String) -> Unit
) {
    
    private var billingClient: BillingClient? = null
    
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()
    
    fun initialize() {
        billingClient = BillingClient.newBuilder(activity)
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        handlePurchase(purchase)
                    }
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                    onPurchaseError("Purchase cancelled")
                } else {
                    onPurchaseError("Purchase failed: ${billingResult.debugMessage}")
                }
            }
            .enablePendingPurchases()
            .build()
        
        startConnection()
    }
    
    private fun startConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _connectionState.value = ConnectionState.CONNECTED
                } else {
                    _connectionState.value = ConnectionState.ERROR
                }
            }
            
            override fun onBillingServiceDisconnected() {
                _connectionState.value = ConnectionState.DISCONNECTED
            }
        })
    }
    
    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                acknowledgePurchase(purchase)
            }
            onPurchaseSuccess()
        }
    }
    
    private fun acknowledgePurchase(purchase: Purchase) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        
        billingClient?.acknowledgePurchase(params) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                onPurchaseSuccess()
            }
        }
    }
    
    fun disconnect() {
        billingClient?.endConnection()
        _connectionState.value = ConnectionState.DISCONNECTED
    }
    
    enum class ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        ERROR
    }
}

