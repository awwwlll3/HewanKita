package com.capstone.hewankita.ui.myPet

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.capstone.hewankita.R
import com.capstone.hewankita.databinding.ActivityMyPetBinding
import com.capstone.hewankita.ui.bottom.BottomActivity
import com.capstone.hewankita.ui.bottom.ui.profile.ProfileFragment
import com.capstone.hewankita.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class MyPetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyPetBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.apply {
            imgPet.setOnClickListener {
                startGallery()
            }

            fabSave.setOnClickListener {
                val etNamePet = etNamePet.text.toString().trim()
                val etSpecies = etSpesies.text.toString().trim()
                val etGender = etJenisKelamin.text.toString().trim()
                val etDatOB = etTgl.text.toString().trim()
                val etWeight = etBerat.text.toString().trim()
                val etFeatherColour = etWarna.text.toString().trim()
                val etOther = etOther.text.toString().trim()
                if (etNamePet.isNotEmpty() && etSpecies.isNotEmpty() && etGender.isNotEmpty() && etDatOB.isNotEmpty() && etWeight.isNotEmpty() && etFeatherColour.isNotEmpty() && etOther.isNotEmpty()) {
                    addPet(etNamePet, etSpecies, etGender, etDatOB, etWeight, etFeatherColour, etOther)
                }
                else{
                    Toast.makeText(this@MyPetActivity, getString(R.string.error_field), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addPet(
        petName: String,
        species: String,
        gender: String,
        dateOB: String,
        weight: String,
        featherColour: String,
        other: String
    ) {

        val user: FirebaseUser? = auth.currentUser
        val userUid: String = user!!.uid
        val userEmail: String = user.email.toString()

        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(Constants.TABLE_DATA_PET)

        val hashMap = mapOf<String, Any>(
            Constants.CONST_PET_NAME to petName,
            Constants.CONST_PET_SPECIES to species,
            Constants.CONST_PET_GENDER to gender,
            Constants.CONST_PET_DATE_OF_BIRTH to dateOB,
            Constants.CONST_PET_WEIGHT to weight,
            Constants.CONST_PET_FEATHER_COLOUR to featherColour,
            Constants.CONST_PET_OTHER to other,
            Constants.CONST_USER_EMAIL to userEmail
        )

        databaseReference.child(userUid).updateChildren(hashMap).addOnCompleteListener {
            val intent = Intent(this@MyPetActivity, BottomActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.picture))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImage: Uri = it.data?.data as Uri

            val storageRef = FirebaseStorage.getInstance()
                .getReference("image/pet/${selectedImage.lastPathSegment}")
            storageRef.putFile(selectedImage).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener {

                    val user: FirebaseUser = auth.currentUser!!
                    val userUid: String = user.uid
                    val database = Firebase.database
                    val databaseReference = database.getReference(Constants.TABLE_DATA_PET)
                    val hashMap = mapOf(
                        Constants.CONST_PET_IMG to it.toString()
                    )
                    databaseReference.child(userUid).updateChildren(hashMap)
                }
            }.addOnFailureListener {
                Toast.makeText(this@MyPetActivity, "Request Time Out", Toast.LENGTH_SHORT).show()
            }
            binding.imgPet.setImageURI(selectedImage)
        }
    }
}