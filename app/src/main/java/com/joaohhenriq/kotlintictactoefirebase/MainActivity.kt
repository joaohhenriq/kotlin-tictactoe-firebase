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

class MainActivity : AppCompatActivity() {

    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    var activePlayer = 1
    var player1 = ArrayList<Int>()
    var player2 = ArrayList<Int>()

    var player1WinsCounts = 0
    var player2WinsCounts = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
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

        playGame(cellId, btnSelected)
    }

    fun playGame(cellId: Int, btnSelected: Button) {
        if (activePlayer == 1) {
            btnSelected.text = "X"
            btnSelected.setBackgroundResource(R.color.red)
            player1.add(cellId)
            activePlayer = 2
            autoplay()
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

    fun autoplay() {
        var emptyCells = ArrayList<Int>()

        for (cellId in 1..9) {
            if (!player1.contains(cellId) || !player2.contains(cellId)) {
                emptyCells.add(cellId)
            }
        }

        if(emptyCells.size == 0) {
            restartGame()
        }

        var r = Random()
        val randomIndex = r.nextInt(emptyCells.size)
        val cellId = emptyCells[randomIndex]

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
}