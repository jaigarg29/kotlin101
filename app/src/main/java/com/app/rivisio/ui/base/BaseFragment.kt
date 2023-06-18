package com.app.rivisio.ui.base

import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.view.View
import androidx.core.app.ActivityCompat
import com.app.rivisio.utils.CommonUtils

/**
 * Created by vicky on 11/2/18.
 */
abstract class BaseFragment : Fragment() {

    private var baseActivity: BaseActivity? = null
    private var progressDialog: Dialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp(view)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) {
            baseActivity = context
        }
    }

    protected abstract fun setUp(view: View)

    fun showLoading() {
        hideLoading()
        progressDialog = CommonUtils.showLoadingDialog(this.requireContext())
    }

    fun hideLoading() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.cancel()
        }
    }

    fun showMessage(message: String?) {
        if (baseActivity != null)
            baseActivity?.showMessage(message)
    }

    fun showError(message: String?) {
        if (baseActivity != null)
            baseActivity?.showError(message)
    }

    fun getBaseActivity(): BaseActivity? {
        return baseActivity
    }

    fun requestPermissionsSafely(permissions: Array<String>, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(requireActivity(), permissions, requestCode)
        }
    }

    fun hasPermissions(permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true
        return permissions.none {
            ContextCompat.checkSelfPermission(
                getBaseActivity()!!,
                it
            ) != PackageManager.PERMISSION_GRANTED
        }
    }
}