package com.example.mms.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.mms.R
import com.example.mms.constant.LIEN_EFFETS_INDESIRABLES
import com.example.mms.database.inApp.SingletonDatabase
import com.example.mms.databinding.FragmentConseilsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import org.osmdroid.api.IMapController
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class ConseilsFragment : Fragment() {

    private var _binding: FragmentConseilsBinding? = null
    private lateinit var controller: IMapController
    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentConseilsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // set text
        val smsBtn = binding.itemMedecin.btnSms
        val mailBtn = binding.itemMedecin.btnMail

        val name = binding.itemMedecin.nomMedecin
        val number = binding.itemMedecin.numeroMedecin
        val mail = binding.itemMedecin.mailMedecin

        val getDoctorThread = Thread {
            val db = SingletonDatabase.getDatabase(this.requireContext())
            val actualDoctor = db.doctorDao().get()

            if (actualDoctor != null) {
                name.text = actualDoctor.getDisplayName()
                number.text = actualDoctor.phone ?: getString(R.string.pas_d_informations)
                mail.text = actualDoctor.email ?: getString(R.string.pas_d_informations)
            }
        }
        getDoctorThread.start()
        getDoctorThread.join()

        if (name.text == "" && number.text == "" && mail.text == "") {
            name.text = getString(R.string.aucun_medecin_enregistre)
            number.text = getString(R.string.cliquez_sur_le_crayon)
            mail.text = getString(R.string.pour_ajouter_un_medecin)
        }

        // set the state of the buttons
        if (this.isDoctorFieldIsEmpty(number)) {
            smsBtn.setBackgroundResource(R.drawable.button_style_3_disable)
            smsBtn.setTextColor(resources.getColor(R.color.clickable_blue_disable))
            smsBtn.isEnabled = false
        } else {
            smsBtn.setBackgroundResource(R.drawable.button_style_3)
            smsBtn.setTextColor(resources.getColor(R.color.clickable_blue))
            smsBtn.isEnabled = true
        }

        if (this.isDoctorFieldIsEmpty(mail)) {
            mailBtn.setBackgroundResource(R.drawable.button_style_3_disable)
            mailBtn.setTextColor(resources.getColor(R.color.clickable_blue_disable))
            mailBtn.isEnabled = false
        } else {
            mailBtn.setBackgroundResource(R.drawable.button_style_3)
            mailBtn.setTextColor(resources.getColor(R.color.clickable_blue))
            mailBtn.isEnabled = true
        }

        // We set the listener to open the sms app with the intent action send to and the phone number as data
        smsBtn.setOnClickListener {
            val phoneNumber = number.text.toString()
            if (phoneNumber.isNotEmpty()) {
                val smsUri = Uri.parse("smsto:$phoneNumber")
                val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri)
                try {
                    startActivity(smsIntent)
                } catch (ex: Exception) {
                    Toast.makeText(
                        this.requireContext(),
                        getString(R.string.erreur_ouverture_sms),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this.requireContext(),
                    getString(R.string.aucun_numero_telephone),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // We set the listener to open the mail app with the intent action send to and the mail address as data
        mailBtn.setOnClickListener {
            val mailAddress = mail.text.toString()
            if (mailAddress.isNotEmpty()) {
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(mailAddress))
                }
                try {
                    startActivity(emailIntent)
                } catch (ex: Exception) {
                    Toast.makeText(
                        this.requireContext(),
                        getString(R.string.erreur_lors_de_ouverture_mails),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this.requireContext(),
                    getString(R.string.aucune_adresse_mail),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.itemMedecin.medecinModifier.setOnClickListener {
            val navHostFragment =
                requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(R.id.action_navigation_notifications_to_navigation_modify_medecin)
        }


        // MAP PART NOT FINISHED

        /**map = binding.itemMap.osmmap
        map.setTileSource(TileSourceFactory.USGS_SAT)
        map.setMultiTouchControls(true)
        map.mapCenter
        map.getLocalVisibleRect(Rect())

        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this.requireContext()), map)
        controller = map.controller

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())
        requestLocation()

        controller.setZoom(15.0)
        map.overlays.add(myLocationOverlay)

        map.addMapListener(object : MapListener {
        override fun onScroll(event: ScrollEvent?): Boolean {
        // event?.source?.getMapCenter()
        Log.e("CC", "onCreate:la ${event?.source?.mapCenter?.latitude}")
        Log.e("CC", "onCreate:lo ${event?.source?.mapCenter?.longitude}")
        //  Log.e("TAG", "onScroll   x: ${event?.x}  y: ${event?.y}", )
        return true
        }

        override fun onZoom(event: ZoomEvent?): Boolean {
        //  event?.zoomLevel?.let { controller.setZoom(it) }
        Log.e("CC", "onZoom zoom level: ${event?.zoomLevel}   source:  ${event?.source}")
        return false;
        }
        })**/

        binding.effetIndesirable.setOnClickListener {
            // open link into browser
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(LIEN_EFFETS_INDESIRABLES)
            startActivity(i)
        }


        return root
    }

    private fun updateButton() {
        val number = binding.itemMedecin.numeroMedecin
        val mail = binding.itemMedecin.mailMedecin


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocation()
            } else {
                Toast.makeText(
                    this.requireContext(),
                    getString(R.string.permission_refusee),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun requestLocation() {
        if (ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLocation = GeoPoint(location.latitude, location.longitude)
                    controller.setCenter(currentLocation)
                    controller.animateTo(currentLocation)

                    myLocationOverlay.enableMyLocation()
                    myLocationOverlay.enableFollowLocation()
                    myLocationOverlay.isDrawAccuracyEnabled = true
                    myLocationOverlay.runOnFirstFix {
                        Looper.myLooper()
                            ?.let { _ -> controller.animateTo(myLocationOverlay.myLocation) }
                    }
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }

    }

    private fun isDoctorFieldIsEmpty(textView: TextView): Boolean {
        return textView.text == getString(R.string.pas_d_informations) ||
                textView.text == getString(R.string.cliquez_sur_le_crayon) ||
                textView.text == ""
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}