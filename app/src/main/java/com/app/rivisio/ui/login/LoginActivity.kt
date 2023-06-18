package com.app.rivisio.ui.login

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.Observer
import com.app.rivisio.R
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.ui.home.HomeActivity
import com.app.rivisio.utils.NetworkResult
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private lateinit var user: User
    private val loginViewModel: LoginViewModel by viewModels()

    private val phoneResultHandler =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val phoneNumber = Identity.getSignInClient(this@LoginActivity)
                        .getPhoneNumberFromIntent(result.data)

                    user.mobile = phoneNumber
                    //show bottom sheet
                    showReferralBottomSheet()

                    Timber.e("PhoneNumber: $phoneNumber")
                } catch (e: ApiException) {
                    Timber.e(e)
                    showError("Error getting mobile")
                }
            } else {
                loginViewModel.setUserDetails(user)
                Timber.e("Phone Number not provided")
            }
        }

    private val loginResultHandler =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val credential: SignInCredential =
                        Identity.getSignInClient(this@LoginActivity)
                            .getSignInCredentialFromIntent(result.data)

                    Timber.e("Name: ${credential.displayName}")
                    Timber.e("Email: ${credential.id}")
                    Timber.e("First Name: ${credential.givenName}")
                    Timber.e("Last Name: ${credential.familyName}")
                    Timber.e("Profile picture: ${credential.profilePictureUri}")

                    user = User(
                        credential.displayName,
                        credential.id,
                        credential.givenName,
                        credential.familyName,
                        "",
                        credential.profilePictureUri.toString(),
                        ""
                    )

                    val request = GetPhoneNumberHintIntentRequest.builder().build()

                    Identity.getSignInClient(this@LoginActivity)
                        .getPhoneNumberHintIntent(request)
                        .addOnFailureListener { e: Exception ->
                            Timber.e(e)
                            showError(e.localizedMessage)
                            showReferralBottomSheet()
                            //workaround for: com.google.android.gms.common.api.ApiException: 16: No phone number is found on this device.
                        }.addOnSuccessListener { pendingIntent: PendingIntent ->
                            val intentSenderRequest =
                                IntentSenderRequest.Builder(pendingIntent.intentSender).build()
                            phoneResultHandler.launch(intentSenderRequest)
                        }

                } catch (e: ApiException) {
                    Timber.e(e)
                    showError("Error google login")
                }
            }
        }

    companion object {
        fun getStartIntent(context: Context) = Intent(context, LoginActivity::class.java)

        fun getStartIntentNewTask(context: Context): Intent {
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }
    }

    override fun getViewModel(): BaseViewModel = loginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setUpObserver()

        findViewById<AppCompatButton>(R.id.google_button).setOnClickListener {

            val request = GetSignInIntentRequest.builder()
                .setServerClientId(getString(R.string.server_client_id))
                .build()

            Identity.getSignInClient(this@LoginActivity)
                .getSignInIntent(request)
                .addOnSuccessListener { result: PendingIntent ->
                    try {
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(result.intentSender).build()
                        loginResultHandler.launch(intentSenderRequest)
                    } catch (e: SendIntentException) {
                        Timber.e("Google Sign-in failed")
                    }
                }
                .addOnFailureListener { e ->
                    Timber.e("Google Sign-in failed", e)
                }
        }
    }

    private fun setUpObserver() {
        loginViewModel.isUserLoggedIn.observe(this, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoading()
                    startActivity(HomeActivity.getStartIntent(this@LoginActivity))
                    finish()
                }

                is NetworkResult.Loading -> {
                    showLoading()
                }

                is NetworkResult.Error -> {
                    hideLoading()
                    showError(it.message)
                }

                is NetworkResult.Exception -> {
                    hideLoading()
                    showError(it.e.message)
                }

                else -> {
                    hideLoading()
                    Timber.e(it.toString())
                }
            }
        })
    }

    private fun showReferralBottomSheet() {
        val referralView = layoutInflater.inflate(R.layout.referral_dialog_layout, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(referralView)
        dialog.setCancelable(false)
        referralView.findViewById<AppCompatButton>(R.id.complete_sign_up).setOnClickListener {
            user.referralCode =
                referralView.findViewById<AppCompatEditText>(R.id.referral_code).text.toString()
            loginViewModel.setUserDetails(user)

            dialog.dismiss()
        }
        dialog.show()
    }

}