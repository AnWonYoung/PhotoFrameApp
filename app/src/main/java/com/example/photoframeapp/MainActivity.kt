package com.example.photoframeapp

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private val startPhotoFrame: AppCompatButton by lazy {
        findViewById(R.id.startPhotoFrame)
    }
    private val addPhoto: AppCompatButton by lazy {
        findViewById(R.id.addPhoto)
    }

    private val imageView: List<ImageView> by lazy {
        mutableListOf<ImageView>().apply {
            add(findViewById(R.id.photo1))
            add(findViewById(R.id.photo2))
            add(findViewById(R.id.photo3))
            add(findViewById(R.id.photo4))
            add(findViewById(R.id.photo5))
            add(findViewById(R.id.photo6))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAddPhotoButton()
        initstartPhotoFrameButton()

        startPhotoFrame.setOnClickListener() {

        }
    }
    // 따로 빼서 메소드로 만들어주기 (추상화)

    private fun initAddPhotoButton() { // 여기서는 권한을 받아와야 함
        addPhoto.setOnClickListener() {
            when {
                ContextCompat.checkSelfPermission( // 권한 유무
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // 권한이 잘 부여되었을 때 사진을 선택하능 구현

                } // 권한이 없을 때
                shouldShowRequestPermissionRationale(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) -> {
                    // 교육용 팝업 확인 후 권한 팝업을 요청하는 팝업 띄우기
                    permissionPopup()
                }
                else -> { // 그 외일 때 처리
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1000
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 위 조건이 만족되었다면 권한이 부여된 것
                } else {
                    Toast.makeText(this, "권한을 거부하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                // requestCode를 1000이라 명시해두었으며, 1000이 요청되지 않았다면 아무런 기능을 하지 않음
            }

        }
    }


        // 권한 기능 구현 후 maniFest에서도 별도의 추가가 필요함
        private fun permissionPopup() {
            AlertDialog.Builder(this)
                .setTitle("권한이 필요합니다.")
                .setMessage("사진 업로드 및 앱 사용을 위한 권한이 필요합니다.")
                .setPositiveButton("동의하기", { dialog, which -> })
                .setNegativeButton("취소하기", { dialog, which -> })
                .create().show()
        }

        private fun initstartPhotoFrameButton() {

        }
    }