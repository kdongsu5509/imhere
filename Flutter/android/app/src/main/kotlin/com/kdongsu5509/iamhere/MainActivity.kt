package com.kdongsu5509.iamhere

import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.util.Log

class MainActivity : FlutterActivity() {
    private val CHANNEL_NAME = "com.iamhere.app/contacts"
    private val METHOD_NAME = "selectContact"
    private val PICK_CONTACT_REQUEST_ID = 1 // 안드로이드에서 사용하는 구분자.

    private var methodResult: MethodChannel.Result? = null // 결과를 저장할 임시 변수

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL_NAME).setMethodCallHandler {
                call, result ->
            //call은 플러터에서 호출한 함수의 이름 정보, result는 안드로이드에서 반환하는 정보.

            if(call.method == METHOD_NAME) {
                methodResult = result

                //안드로이드 Native 설정
                val intent =
                    Intent(
                        Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                    )

                //안드로이드에서 작업 시행
                startActivityForResult(intent, PICK_CONTACT_REQUEST_ID)
            } else {
                result.notImplemented()
            }
        }
    }

    // startActivityForResult의 결과를 담아 안드로이드가 자동으로 호출해서 결과를 다룬다.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // selectContact 호출에서 온 결과인지 확인
        if (requestCode == PICK_CONTACT_REQUEST_ID) {
            if (resultCode == Activity.RESULT_OK) {
                // 사용자가 연락처를 선택했음
                val contactUri = data?.data
                if (contactUri != null) {
                    val contactMap = getContactFromUri(contactUri)
                    methodResult?.success(contactMap)
                } else {
                    methodResult?.error("CONTACT_PICK_FAILED", "선택된 연락처 URI가 없습니다.", null)
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // 사용자가 취소했음
                methodResult?.success(null) // Flutter에 null을 전달
            }
            methodResult = null // 결과 전달 후 Result 초기화
        }
    }

    // 선택된 URI에서 이름과 전화번호를 추출
    private fun getContactFromUri(contactUri: android.net.Uri): Map<String, String?> {
        val contactMap = mutableMapOf<String, String?>()
        val contentResolver = contentResolver

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val cursor = contentResolver.query(
            contactUri,
            projection,
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY)
                val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                if (nameIndex >= 0 && numberIndex >= 0) {
                    val name = it.getString(nameIndex)
                    val number = it.getString(numberIndex)?.replace("[^0-9]".toRegex(), "")

                    contactMap["name"] = name
                    contactMap["number"] = number
                }
            }
        }
        return contactMap
    }
}