package com.example.oneredaction

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var playBtn: Button
    private lateinit var pauseBtn: Button
    private lateinit var stopBtn: Button
    private lateinit var seekBar: SeekBar
    private lateinit var tvPass: TextView
    private lateinit var tvDue: TextView
    private var videoUri: Uri? = null
    private val videoPickCode = 1000
    private var isPaused = false
    private val handler = Handler()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnUpload = findViewById<Button>(R.id.btn_upload)
        videoView = findViewById(R.id.videoView)
        playBtn = findViewById(R.id.btn_play)
        pauseBtn = findViewById(R.id.btn_pause)
        stopBtn = findViewById(R.id.btn_stop)
        seekBar = findViewById(R.id.seek_bar)
        tvPass = findViewById(R.id.tv_pass)
        tvDue = findViewById(R.id.tv_due)

        btnUpload.setOnClickListener {
            pickVideo()
        }

        playBtn.setOnClickListener {
            if (videoUri != null) {
                if (!videoView.isPlaying) {
                    videoView.start()
                    isPaused = false
                    updateSeekBar()
                    Toast.makeText(this, "Playing Video", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Select a video first", Toast.LENGTH_SHORT).show()
            }
        }

        pauseBtn.setOnClickListener {
            if (videoView.isPlaying) {
                videoView.pause()
                isPaused = true
                Toast.makeText(this, "Video Paused", Toast.LENGTH_SHORT).show()
            }
        }

        stopBtn.setOnClickListener {
            if (videoView.isPlaying || isPaused) {
                videoView.stopPlayback()  // Completely stop the video
                videoView.setVideoURI(videoUri) // Reset video
                isPaused = false
                seekBar.progress = 0
                tvPass.text = "0 sec"
                tvDue.text = "${seekBar.max} sec"
                Toast.makeText(this, "Video Stopped", Toast.LENGTH_SHORT).show()
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    videoView.seekTo(progress * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        videoView.setOnPreparedListener { mediaPlayer ->
            seekBar.max = mediaPlayer.duration / 1000
            tvDue.text = "${mediaPlayer.duration / 1000} sec"
            updateSeekBar()
        }
    }

    private fun pickVideo() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "video/*"
        startActivityForResult(intent, videoPickCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == videoPickCode && resultCode == Activity.RESULT_OK) {
            videoUri = data?.data
            videoView.setVideoURI(videoUri)
            videoView.requestFocus()
            Toast.makeText(this, "Video Selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateSeekBar() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (videoView.isPlaying) {
                    val currentPos = videoView.currentPosition / 1000
                    seekBar.progress = currentPos
                    tvPass.text = "$currentPos sec"
                    tvDue.text = "${seekBar.max - currentPos} sec"
                    handler.postDelayed(this, 1000)
                }
            }
        }, 1000)
    }
}
