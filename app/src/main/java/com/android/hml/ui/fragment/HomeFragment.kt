package com.android.hml.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.hml.BuildConfig
import com.google.android.hml.R
import com.google.android.hml.databinding.FragmentHomeBinding
import com.android.hml.sysApp
import com.android.hml.service.ConfigManager
import com.android.hml.service.ServiceClient
import com.android.hml.ui.activity.AboutActivity
import com.android.hml.ui.util.ThemeUtils.getColor
import com.android.hml.ui.util.ThemeUtils.themeColor
import com.android.hml.ui.util.makeToast
import com.android.hml.ui.util.navController
import com.android.hml.ui.util.setupToolbar
import java.io.IOException

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding<FragmentHomeBinding>()

    private val backupSAFLauncher =
        registerForActivityResult(CreateDocument("application/json")) backup@{ uri ->
            if (uri == null) return@backup
            ConfigManager.configFile.inputStream().use { input ->
                sysApp.contentResolver.openOutputStream(uri).use { output ->
                    if (output == null) makeToast(R.string.home_export_failed)
                    else input.copyTo(output)
                }
            }
            makeToast(R.string.home_exported)
        }

    private val restoreSAFLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) restore@{ uri ->
            if (uri == null) return@restore
            runCatching {
                val backup = sysApp.contentResolver
                    .openInputStream(uri)?.reader().use { it?.readText() }
                    ?: throw IOException(getString(R.string.home_import_file_damaged))
                ConfigManager.importConfig(backup)
                makeToast(R.string.home_import_successful)
            }.onFailure {
                it.printStackTrace()
                MaterialAlertDialogBuilder(requireContext())
                    .setCancelable(false)
                    .setTitle(R.string.home_import_failed)
                    .setMessage(it.message)
                    .setPositiveButton(android.R.string.ok, null)
                    .setNegativeButton(R.string.show_crash_log) { _, _ ->
                        MaterialAlertDialogBuilder(requireActivity())
                            .setCancelable(false)
                            .setTitle(R.string.home_import_failed)
                            .setMessage(it.stackTraceToString())
                            .setPositiveButton(android.R.string.ok, null)
                            .show()
                    }
                    .show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialElevationScale(false)
        reenterTransition = MaterialElevationScale(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolbar(
            toolbar = binding.toolbar,
            title = getString(R.string.app_name),
            menuRes = R.menu.menu_about,
            onMenuOptionSelected = {
                startActivity(Intent(requireContext(), AboutActivity::class.java))
            }
        )

        binding.templateManage.setOnClickListener {
            val extras = FragmentNavigatorExtras(binding.manageCard to "transition_manage")
            navController.navigate(R.id.nav_template_manage, null, null, extras)
        }
        binding.appManage.setOnClickListener {
            navController.navigate(R.id.nav_app_manage)
        }
        binding.backupConfig.setOnClickListener {
            backupSAFLauncher.launch("HML_Config.json")
        }
        binding.restoreConfig.setOnClickListener {
            restoreSAFLauncher.launch("application/json")
        }
    }

    override fun onStart() {
        super.onStart()
        val serviceVersion = ServiceClient.serviceVersion
        val color = when {
            !sysApp.isHooked -> getColor(R.color.gray)
            serviceVersion == 0 -> getColor(R.color.invalid)
            else -> themeColor(android.R.attr.colorPrimary)
        }
        binding.statusCard.setCardBackgroundColor(color)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            binding.statusCard.outlineAmbientShadowColor = color
            binding.statusCard.outlineSpotShadowColor = color
        }
        if (sysApp.isHooked) {
            binding.moduleStatusIcon.setImageResource(R.drawable.outline_done_all_24)
            val versionNameSimple = BuildConfig.VERSION_NAME.substringBefore(".r")
            binding.moduleStatus.text = getString(R.string.home_xposed_activated, versionNameSimple, BuildConfig.VERSION_CODE)
        } else {
            binding.moduleStatusIcon.setImageResource(R.drawable.outline_extension_off_24)
            binding.moduleStatus.setText(R.string.home_xposed_not_activated)
        }
        if (serviceVersion != 0) {
            if (serviceVersion < com.android.hml.common.BuildConfig.SERVICE_VERSION) {
                binding.serviceStatus.text = getString(R.string.home_xposed_service_old)
            } else {
                binding.serviceStatus.text = getString(R.string.home_xposed_service_on, serviceVersion)
            }
        } else {
            binding.serviceStatus.setText(R.string.home_xposed_service_off)
        }
    }
}
