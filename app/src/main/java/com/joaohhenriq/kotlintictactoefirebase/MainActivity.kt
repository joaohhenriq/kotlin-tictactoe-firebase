package com.joaohhenriq.kotlintictactoefirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var dataBase = FirebaseDatabase.getInstance()
    private var myRef = dataBase.reference

    private var myEmail: String? = null

    var activePlayer = 1
    var player1 = ArrayList<Int>()
    var player2 = ArrayList<Int>()

    var player1WinsCounts = 0
    var player2WinsCounts = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        var b: Bundle = intent.extras!!
        myEmail = b.getString("email")
        incomingCalls()
    }

    fun btnClick(view: View) {

        val btnSelected = view as Button

        var cellId = 0
        when (btnSelected.id) {
            R.id.btn1 -> cellId = 1
            R.id.btn2 -> cellId = 2
            R.id.btn3 -> cellId = 3
            R.id.btn4 -> cellId = 4
            R.id.btn5 -> cellId = 5
            R.id.btn6 -> cellId = 6
            R.id.btn7 -> cellId = 7
            R.id.btn8 -> cellId = 8
            R.id.btn9 -> cellId = 9
        }

        myRef.child("playerOnline").child(sessionId!!).child(cellId.toString()).setValue(myEmail)
    }

    fun playGame(cellId: Int, btnSelected: Button) {
        if (activePlayer == 1) {
            btnSelected.text = "X"
            btnSelected.setBackgroundResource(R.color.red)
            player1.add(cellId)
            activePlayer = 2
        } else {
            btnSelected.text = "O"
            btnSelected.setBackgroundResource(R.color.yellow)
            player2.add(cellId)
            activePlayer = 1
        }

        btnSelected.isEnabled = false

        checkWinner()
    }

    fun checkWinner() {
        var winner = -1

        //rows
        if (player1.contains(1) && player1.contains(2) && player1.contains(3)) {
            winner = 1
        }
        if (player2.contains(1) && player2.contains(2) && player2.contains(3)) {
            winner = 2
        }

        if (player1.contains(4) && player1.contains(5) && player1.contains(6)) {
            winner = 1
        }
        if (player2.contains(4) && player2.contains(5) && player2.contains(6)) {
            winner = 2
        }

        if (player1.contains(7) && player1.contains(8) && player1.contains(9)) {
            winner = 1
        }
        if (player2.contains(7) && player2.contains(8) && player2.contains(9)) {
            winner = 2
        }

        //columns
        if (player1.contains(1) && player1.contains(4) && player1.contains(7)) {
            winner = 1
        }
        if (player2.contains(1) && player2.contains(4) && player2.contains(7)) {
            winner = 2
        }

        if (player1.contains(2) && player1.contains(5) && player1.contains(8)) {
            winner = 1
        }
        if (player2.contains(2) && player2.contains(5) && player2.contains(8)) {
            winner = 2
        }

        if (player1.contains(3) && player1.contains(6) && player1.contains(9)) {
            winner = 1
        }
        if (player2.contains(3) && player2.contains(6) && player2.contains(9)) {
            winner = 2
        }

        //diagonal
        if (player1.contains(1) && player1.contains(5) && player1.contains(9)) {
            winner = 1
        }
        if (player2.contains(1) && player2.contains(5) && player2.contains(9)) {
            winner = 2
        }

        if (player1.contains(3) && player1.contains(5) && player1.contains(7)) {
            winner = 1
        }
        if (player2.contains(3) && player2.contains(5) && player2.contains(7)) {
            winner = 2
        }

        //winner
        if (winner == 1) {
            player1WinsCounts++
            Toast.makeText(this, "player one won the game", Toast.LENGTH_LONG).show()
            restartGame()
        } else if (winner == 2) {
            player2WinsCounts++
            Toast.makeText(this, "player two won the game", Toast.LENGTH_LONG).show()
            restartGame()
        }
    }

    fun autoplay(cellId: Int) {


        var btnSelected:Button
        btnSelected = when(cellId) {
            1 -> btn1
            2 -> btn2
            3 -> btn3
            4 -> btn4
            5 -> btn5
            6 -> btn6
            7 -> btn7
            8 -> btn8
            9 -> btn9
            else -> {btn1}
        }

        playGame(cellId, btnSelected)
    }

    fun restartGame(){
        activePlayer = 1
        player1.clear()
        player2.clear()

        for (i in 1..9) {
            var btnSelected:Button? = when(i) {
                1 -> btn1
                2 -> btn2
                3 -> btn3
                4 -> btn4
                5 -> btn5
                6 -> btn6
                7 -> btn7
                8 -> btn8
                9 -> btn9
                else -> {btn1}
            }

            btnSelected!!.text = ""
            btnSelected!!.setBackgroundResource(R.color.buttonColor)
            btnSelected.isEnabled = true
        }

        Toast.makeText(this, "Player1: $player1WinsCounts, Player2: $player2WinsCounts", Toast.LENGTH_LONG).show()
    }

    fun btnRequestEvent(view: View){
        var userEmail = edtFriendEmail.text.toString()
        myRef.child("users").child(userEmail.split("@")[0]).child("request").push().setValue(myEmail)

        playerOnline(myEmail!!.split("@")[0] + userEmail.split("@")[0])

    }

    fun btnAcceptEvent(view: View) {
        var userEmail = edtFriendEmail.text.toString()
        myRef.child("users").child(userEmail.split("@")[0]).child("request").push().setValue(myEmail)

        playerOnline(userEmail.split("@")[0] + myEmail!!.split("@")[0])
    }

    private fun incomingCalls() {
        myRef.child("users").child(myEmail!!.split("@")[0]).child("request").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val td = snapshot.value as HashMap<String, Any>
                    if(td != null) {
                        var value: String
                        for(key in td.keys) {
                            value = td[key] as String
                            edtFriendEmail.setText(value)

                            myRef.child("users").child(myEmail!!).child("request").setValue(true)
                            break
                        }
                    }
                } catch (e: Exception){}
            }

        })
    }

    var playerSymbol: String? = null
    var sessionId: String? = null

    fun playerOnline(sessionId: String) {
        this.sessionId = sessionId

        myRef.child("playerOnline").removeValue()

        myRef.child("playerOnline").child(sessionId!!).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    player1.clear()
                    player2.clear()

                    val td = snapshot.value as HashMap<String, Any>
                    if(td != null) {
                        var value: String
                        for(key in td.keys) {
                            value = td[key] as String

                            activePlayer = if(value != myEmail){
                                if(playerSymbol === "X") 1 else 2
                            } else {
                                if(playerSymbol === "X") 2 else 1
                            }

                            autoplay(key.toInt())
                        }
                    }
                } catch (e: Exception){}
            }

        })
    }
}