package com.app.rivisio.ui.subscribe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Observer
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.android.billingclient.api.QueryPurchasesParams
import com.app.rivisio.R
import com.app.rivisio.databinding.ActivitySubscribeBinding
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.ui.refer.ReferActivity
import com.google.gson.JsonParser
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SubscribeActivity : BaseActivity(), ProductDetailsResponseListener, PurchasesUpdatedListener,
    PurchasesResponseListener {

    private val subscribeViewModel: SubscribeViewModel by viewModels()

    private lateinit var binding: ActivitySubscribeBinding

    private lateinit var billingClient: BillingClient

    private lateinit var selectedSubscriptionPlanId: String
    private lateinit var selectedSubscription: ProductDetails.SubscriptionOfferDetails

    override fun getViewModel(): BaseViewModel = subscribeViewModel

    companion object {
        fun getStartIntent(context: Context) = Intent(context, SubscribeActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubscribeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subscribeViewModel.insert.observe(this, Observer {
            if (it > 0) {
                hideLoading()
                terminateBillingConnection()
                finish()
            }
        })

        setButtonListeners()

        billingClient = BillingClient.newBuilder(this@SubscribeActivity)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        showLoading()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingSteupResult: BillingResult) {
                if (billingSteupResult.responseCode == BillingClient.BillingResponseCode.OK) {

                    Timber.e("onBillingSetupFinished() : ")

                    val params = QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.SUBS)
                    val purchasesResult = billingClient.queryPurchasesAsync(
                        params.build(),
                        this@SubscribeActivity
                    )
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.

                Timber.e("Billing service disconnected")
            }
        })
    }

    private fun setButtonListeners() {
        binding.closeButton.setOnClickListener {
            finish()
        }


        binding.quarter.setOnClickListener {
            binding.quarter.setBackgroundResource(R.drawable.edit_text_bg)
            binding.year.setBackgroundResource(R.drawable.edit_text_bg)
            binding.lifetime.setBackgroundResource(R.drawable.edit_text_bg)

            binding.quarter.setBackgroundResource(R.drawable.selected_plan_bg)
            selectedSubscriptionPlanId = BasePlan.THREE_MONTH.subscriptionPlanId
        }
        binding.year.setOnClickListener {
            binding.quarter.setBackgroundResource(R.drawable.edit_text_bg)
            binding.year.setBackgroundResource(R.drawable.edit_text_bg)
            binding.lifetime.setBackgroundResource(R.drawable.edit_text_bg)

            binding.year.setBackgroundResource(R.drawable.selected_plan_bg)
            selectedSubscriptionPlanId = BasePlan.ONE_YEAR.subscriptionPlanId
        }

        //binding.lifetime.setOnClickListener { listener(it) }

        binding.upgradeButton.setOnClickListener {

            if (!this::selectedSubscriptionPlanId.isInitialized) {
                showMessage("Select a subscription plan")
                return@setOnClickListener
            }

            val productList = mutableListOf<Product>()

            productList.add(
                Product.newBuilder()
                    .setProductId("revu_basic_sub")
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            )

            val params = QueryProductDetailsParams.newBuilder()

            params.setProductList(productList)

            billingClient.queryProductDetailsAsync(
                params.build(),
                this@SubscribeActivity
            )
        }

        binding.referOnSub.setOnClickListener {
            val intent = Intent(this@SubscribeActivity, ReferActivity::class.java)
            finish()
            startActivity(intent)
        }
    }
    override fun onProductDetailsResponse(
        billingResult: BillingResult,
        productDetailsList: MutableList<ProductDetails>
    ) {
        val productDetailsParamsList = mutableListOf<ProductDetailsParams>()
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {

                Timber.e("onProductDetailsResponse() : ")

                productDetailsList.forEach {
                    if (it.subscriptionOfferDetails!![0].basePlanId == selectedSubscriptionPlanId) {
                        productDetailsParamsList.add(
                            ProductDetailsParams.newBuilder()
                                .setProductDetails(it)
                                .setOfferToken(it.subscriptionOfferDetails!![0].offerToken)
                                .build()
                        )
                        selectedSubscription = it.subscriptionOfferDetails!![0]
                    }
                    if (it.subscriptionOfferDetails!![1].basePlanId == selectedSubscriptionPlanId) {
                        productDetailsParamsList.add(
                            ProductDetailsParams.newBuilder()
                                .setProductDetails(it)
                                .setOfferToken(it.subscriptionOfferDetails!![1].offerToken)
                                .build()
                        )
                        selectedSubscription = it.subscriptionOfferDetails!![1]
                    }
                }

                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()

                val billingResult = billingClient.launchBillingFlow(
                    this@SubscribeActivity,
                    billingFlowParams
                )
            }

            else -> {
                Timber.d("onProductDetailsResponse: $responseCode $debugMessage")
            }
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            runOnUiThread {
                showLoading()
            }
            for (purchase in purchases) {
                acknowledgePurchases(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            runOnUiThread {
                showMessage("Cancelled")
            }
        } else {
            Timber.d("onPurchasesUpdated: ${billingResult.responseCode} ${billingResult.debugMessage}")
            // Handle any other error codes.
        }
    }

    private fun acknowledgePurchases(purchase: Purchase?) {
        purchase?.let {
            if (!it.isAcknowledged) {
                val params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(it.purchaseToken)
                    .build()

                billingClient.acknowledgePurchase(
                    params
                ) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK &&
                        it.purchaseState == Purchase.PurchaseState.PURCHASED
                    ) {
                        runOnUiThread {

                            //make a network call to the server
                            Timber.e("Purchase: ${it.toString()}")
                            Timber.e("Purchase orderId: ${it.orderId}")
                            Timber.e("Purchase productId: ${JsonParser().parse(it.originalJson).asJsonObject["productId"].asString}")
                            Timber.e("Purchase purchaseTime: ${it.purchaseTime}")
                            Timber.e("Purchase isAutoRenewing: ${it.isAutoRenewing}")

                            Timber.e("Subscription basePlanId: ${selectedSubscription.basePlanId}")
                            if (selectedSubscription.pricingPhases.pricingPhaseList.size > 0)
                                Timber.e("Subscription billingPeriod: ${selectedSubscription.pricingPhases.pricingPhaseList[0].billingPeriod}")

                            showMessage("Purchase acknowledged")

                            subscribeViewModel.insertPurchase(
                                com.app.rivisio.data.db.entity.Purchase(
                                    orderId = it.orderId!!,
                                    productId = JsonParser().parse(it.originalJson).asJsonObject["productId"].asString!!,
                                    purchaseTime = it.purchaseTime,
                                    isAutoRenewing = it.isAutoRenewing,
                                    basePlanId = selectedSubscription.basePlanId,
                                    billingPeriod = selectedSubscription.pricingPhases.pricingPhaseList[0].billingPeriod,
                                    userId = 0
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun terminateBillingConnection() {
        Timber.d("Terminating connection")
        billingClient.endConnection()
    }

    override fun onQueryPurchasesResponse(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            Timber.e("onQueryPurchasesResponse() : ${purchases.size}")
            hideLoading()

            if (purchases.size > 0) {
                runOnUiThread {
                    showMessage("You already have an active subscription")
                    terminateBillingConnection()
                    finish()
                }
            }
        } else {
            hideLoading()
            Timber.e("onQueryPurchasesResponse() : ${billingResult.responseCode} ${billingResult.debugMessage}")
        }
    }

    override fun onDestroy() {
        terminateBillingConnection()
        super.onDestroy()
    }
}