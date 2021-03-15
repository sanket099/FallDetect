package com.sanket.falldetect

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sanket.falldetect.databinding.ActivityContactsBinding
import com.sanket.falldetect.R.layout.activity_contacts


class ContactsActivity : AppCompatActivity() {

    lateinit var myViewModel: MyViewModel
    private val sharedPrefFile: String = "shared"
    private  lateinit var sharedPreferences: SharedPreferences
    private lateinit var _binding : ActivityContactsBinding

    lateinit var context: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        sharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)

        context = this@ContactsActivity


        myViewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        myViewModel.getLoginDetails(context)!!.observe(this, Observer {

            if (it == null) {
                Toast.makeText(context, "No Contacts Found", Toast.LENGTH_SHORT).show()
            }
            else {
                if(it.isEmpty()){
                    Toast.makeText(context, "No Contacts Found", Toast.LENGTH_SHORT).show()
                }
                else{
                    /*_binding.name.setText(it[0].name)
                    _binding.email.setText(it[0].email)
                    _binding.address.setText(it[0].address)
                    _binding.phn.setText(it[0].phone)

                    _binding.name2.setText(it[1].name)
                    _binding.email2.setText(it[1].email)
                    _binding.address2.setText(it[1].address)
                    _binding.phn2.setText(it[1].phone)

                    _binding.name.setText(it[2].name)
                    _binding.email3.setText(it[2].email)
                    _binding.address3.setText(it[2].address)
                    _binding.phn3.setText(it[2].phone)
*/
                }

            }
        })

        _binding.saveEc.setOnClickListener {

            val editor:SharedPreferences.Editor =  sharedPreferences.edit()
            editor.putBoolean("ec_saved",true)

            editor.apply()
            editor.commit()


            insertData(_binding.name.text.toString(), _binding.email.text.toString(),
                    _binding.address.text.toString(), _binding.phn.text.toString())

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

          /*  insertData(_binding.name2.text.toString(), _binding.email2.text.toString(),
                _binding.address2.text.toString(), _binding.phn2.text.toString())

            insertData(_binding.name3.text.toString(), _binding.email3.text.toString(),
                _binding.address3.text.toString(), _binding.phn3.text.toString())*/

        }

    }

    private fun insertData(name: String, email: String, address: String, phone: String){
        myViewModel.insertData(context, name, email, address, phone)
    }

    override fun onStart() {
        super.onStart()

        if(sharedPreferences.getBoolean("ec_saved",false)){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}