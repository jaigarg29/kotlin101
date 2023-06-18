package com.app.rivisio.ui.base

import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.app.rivisio.R
import com.app.rivisio.utils.CommonUtils
import es.dmoral.toasty.Toasty

abstract class BaseActivity : AppCompatActivity() {

    private var progressDialog: Dialog? = null
    private lateinit var baseViewModel: BaseViewModel

    abstract fun getViewModel(): BaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseViewModel = getViewModel()
        /*baseViewModel.loggedOut.observe(this, Observer {
            //startActivity(LoginActivity.getStartIntentNewTask(this))
            //finish()
        })*/
    }

    fun hasPermissions(permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true
        return permissions.none {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestPermissionsSafely(permissions: Array<String>, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, permissions, requestCode)
        }
    }

    fun showLoading() {
        hideLoading()
        progressDialog = CommonUtils.showLoadingDialog(this)
    }

    fun hideLoading() {
        if (progressDialog != null) {
            if (progressDialog?.isShowing == true)
                progressDialog?.cancel()
        }
    }

    fun showError(message: String?) {
        if (message != null)
            Toasty.custom(
                this,
                message,
                R.drawable.ic_error,
                R.color.red,
                Toast.LENGTH_SHORT,
                true,
                true
            ).show();
    }

    fun showMessage(message: String?) {
        if (message != null)
            Toasty.custom(
                this,
                message,
                R.drawable.ic_info,
                R.color.secondary,
                Toast.LENGTH_SHORT,
                true,
                true
            ).show()
    }
}