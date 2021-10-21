package com.example.photoframeapp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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

    private val imageViewList  : List<ImageView> by lazy {
        mutableListOf<ImageView>().apply {
            add(findViewById(R.id.photo1))
            add(findViewById(R.id.photo2))
            add(findViewById(R.id.photo3))
            add(findViewById(R.id.photo4))
            add(findViewById(R.id.photo5))
            add(findViewById(R.id.photo6))
        }
    }
    // 리스트로 uri를 저장할 수 있도록 구현
    private val imgUriList : MutableList<Uri> = mutableListOf()

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
                    uploadPhoto()
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
                    uploadPhoto()
                } else {
                    Toast.makeText(this, "권한을 거부하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                // requestCode를 1000이라 명시해두었으며, 1000이 요청되지 않았다면 아무런 기능을 하지 않음
            }

        }
    }

    // 스토리지 엑세스 프레임워크를 사용하여 사진을 가져올 것
    // SEF의 구현은 코드가 간결할 뿐만 아니라 사용자들에게도 친근함
    private fun uploadPhoto() {
        val intent = Intent(Intent.ACTION_GET_CONTENT) //ACTION_GET_CONTENT란,
                                                       // saf 기능을 실행하여 컨텐츠를 가져오는 액티비티를 실행하게 됨
        intent.type = "image/*" // 이미지만 불러올 수 있도록 경로를 설정
        startActivityForResult(intent, 2000) // startActivityForResult를 통해서 콜백으로 activityResult가 떨어짐.
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
// 중간 예외처리 (정상적으로 데이터를 내리지 않고 취소 등
// 사진이 올바르게 올라가지 않았을 때의 예외처리를 해주기
        if(resultCode != Activity.RESULT_OK)  {
            return // OK가 되지 않았을 때, return 해주기
        }
        when(requestCode) { // resultCode가 일치할 때
            2000 -> { // nullalbe이 가능함을 알려주지 않으면 이전에 데이터가 null일 때 오류가 생길 수 있음
                val selectImgUri : Uri? = data?.data // data가 null이면 뒤를 실행하지 않고 null을 반환하도록

                if (selectImgUri != null) {

                    if(imgUriList.size == 6) {
                        Log.d("MainActivity","${imgUriList.size}")
                        Toast.makeText(this, "더는 못해~~~!", Toast.LENGTH_SHORT).show()
                        return
                    }

                    imgUriList.add(selectImgUri) // 절대 null일 수 없음
                    // imgUriList.size가 하나 있다면, imageViewList에는 [0]번 째에 들어가야 하기 때문에 -1을 해줌
                    imageViewList[imgUriList.size -1].setImageURI(selectImgUri)
                } else {
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> { // 예외처리
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
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