package com.app.rivisio.ui.home.fragments.account

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.app.rivisio.databinding.FragmentAccountBinding
import com.app.rivisio.ui.base.BaseFragment
import com.app.rivisio.ui.login.LoginActivity
import com.app.rivisio.ui.notification.NotificationActivity
import com.app.rivisio.ui.profile.ProfileActivity
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.identity.Identity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class AccountFragment : BaseFragment() {

    private var _binding: FragmentAccountBinding? = null
    private val accountViewModel: AccountViewModel by viewModels()

    private val binding
        get() = _binding!!

    companion object {
        @JvmStatic
        fun newInstance() = AccountFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setUp(view: View) {

        binding.manageSubscription.setOnClickListener {

            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/account/subscriptions?sku=revu_basic_sub&package=com.app.rivisio")
            )
            startActivity(browserIntent)

        }

        accountViewModel.logout.observe(this, Observer {
            if (it)
                startActivity(LoginActivity.getStartIntentNewTask(requireContext()))
        })

        binding.logout.setOnClickListener {
            Identity.getSignInClient(requireActivity())
                .signOut()
                .addOnSuccessListener {
                    Timber.e("Logout successful")
                    accountViewModel.logout()
                }
                .addOnFailureListener {
                    Timber.e("Logout failed")
                    showError("Logout failed")
                }

        }
        accountViewModel.userEmail.observe(this, Observer {
            binding.accountEmail.text = it
        })

        accountViewModel.userName.observe(this, Observer {
            binding.accountName.text = it
        })

        accountViewModel.userProfilePic.observe(this, Observer {
            Glide.with(requireActivity())
                .asBitmap()
                .load(it)
                .into(binding.profileImage)
        })

        binding.profileContainer.setOnClickListener {
            startActivity(ProfileActivity.getStartIntent(requireContext()))
        }

        binding.notificationContainer.setOnClickListener {
            startActivity(NotificationActivity.getStartIntent(requireContext()))
        }

        binding.privacyPolicyContainer.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://docs.google.com/document/d/12bESUwLt-AQ88TE3Agz1wd3wFWWJ6n_OIG0CU_BkmSw/edit")
            )
            startActivity(browserIntent)
        }

        accountViewModel.getUserDetails()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}