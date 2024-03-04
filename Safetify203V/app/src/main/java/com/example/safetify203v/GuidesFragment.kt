package com.example.safetify203v

import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import java.io.File
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class GuidesFragment : Fragment() {
    private var myDownloadId: Long = 0
    private lateinit var downloadReceiver: BroadcastReceiver
    private lateinit var dm: DownloadManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_guides, container, false)

        // Find the floating action button by its ID
        val floatingbutton = rootView.findViewById<FloatingActionButton>(R.id.floatingbutton)

        // Initialize the BroadcastReceiver
        downloadReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == myDownloadId) {
                    Toast.makeText(context, "Safetify Guides Download Completed", Toast.LENGTH_LONG).show()

                    // Open the downloaded file using an Intent
                    val downloadedFileUri = dm.getUriForDownloadedFile(myDownloadId)
                    openDownloadedFile(downloadedFileUri)
                }
            }
        }

        // Set a click listener on the floating action button
        floatingbutton.setOnClickListener {
            val request = DownloadManager.Request(
                Uri.parse("https://atmiyauni.ac.in/public/file/1675762289.Atmiya-University-Main-Brochure.pdf")
            )
                .setTitle("Safetify_Full_Guides")
                .setDescription("Safetify Guides Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setAllowedOverMetered(true)

            dm = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            myDownloadId = dm.enqueue(request)
        }

        // Register the BroadcastReceiver
        requireContext().registerReceiver(
            downloadReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Unregister the BroadcastReceiver when the fragment is destroyed
        requireContext().unregisterReceiver(downloadReceiver)
    }

    private fun openDownloadedFile(fileUri: Uri?) {
        fileUri?.let {
            val openIntent = Intent(Intent.ACTION_VIEW)
            openIntent.setDataAndType(it, "application/pdf") // Change the MIME type if needed
            openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            try {
                startActivity(openIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    requireContext(),
                    "No application found to open the file",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
