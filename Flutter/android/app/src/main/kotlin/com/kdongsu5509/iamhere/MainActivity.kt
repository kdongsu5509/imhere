// MainActivity.kt 파일의 가장 상단
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
    private val PICK_CONTACT_REQUEST = 1 // requestCode
    private var methodResult: MethodChannel.Result? = null // 결과를 저장할 임시 변수

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        // 1. MethodChannel 생성 및 핸들러 설정
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL_NAME).setMethodCallHandler {
                call, result -> //call은 플러터에서 호출한 함수의 이름 정보

            // 2. Flutter에서 호출한 메서드 이름 확인 ('importContact'는 Dart에서 호출한 이름)
            if(call.method == "selectContact") {
                // 1. MethodChannel.Result를 저장
                methodResult = result

                // 2. 연락처 선택 인텐트 생성 및 실행
                val intent = Intent(
                    Intent.ACTION_PICK,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI // 전화번호만 있는 연락처를 선택하도록 URI 설정
                )
                startActivityForResult(intent, PICK_CONTACT_REQUEST) // 결과 받을 준비
            }
            else if (call.method == "importContact") {
                // 3. 오타 수정: imoprtContactFromDevice -> importContactFromDevice
                val contacts = importContactFromDevice(this)
                result.success(contacts)
            } else {
                result.notImplemented()
            }
        }
    }

    // 3. startActivityForResult의 결과를 처리하는 콜백 메서드
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // selectContact 호출에서 온 결과인지 확인
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                // 사용자가 연락처를 선택했음
                val contactUri = data?.data
                if (contactUri != null) {
                    val contactMap = getContactFromUri(contactUri)
                    // 4. Flutter에 결과 전달
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

    // 5. 선택된 URI에서 이름과 전화번호를 추출하는 헬퍼 함수
    private fun getContactFromUri(contactUri: android.net.Uri): Map<String, String?> {
        val contactMap = mutableMapOf<String, String?>()
        val contentResolver = contentResolver

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val cursor = contentResolver.query(
            contactUri, // 선택된 하나의 연락처 URI를 쿼리
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
                    // 숫자 외 문자 제거
                    val number = it.getString(numberIndex)?.replace("[^0-9]".toRegex(), "")

                    contactMap["name"] = name
                    contactMap["number"] = number
                }
            }
        }
        return contactMap
    }

    // 4. Android ContentResolver를 사용하여 전체 연락처 정보를 가져오는 함수
    private fun importContactFromDevice(context: Context): List<Map<String, String?>> {
        val contactsList = mutableListOf<Map<String, String?>>()
        val contentResolver = context.contentResolver

        // Android 연락처 데이터베이스에서 원하는 필드(이름, 번호)만 선택
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        // 전화번호가 있는 연락처만 쿼리
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " ASC" // 이름순 정렬
        )

        cursor?.use {
            // 커서가 널이 아니고 데이터가 있는 경우
            while (it.moveToNext()) {
                val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY)
                val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                // 인덱스가 유효한 경우에만 데이터를 가져옵니다.
                if (nameIndex >= 0 && numberIndex >= 0) {
                    val name = it.getString(nameIndex)
                    // 숫자 외 문자 제거 및 널 체크
                    val number = it.getString(numberIndex)?.replace("[^0-9]".toRegex(), "")

                    contactsList.add(mapOf("name" to name, "number" to number))
                }
            }
        }
        Log.d("Contacts", "Contacts loaded: ${contactsList.size}")
        return contactsList
    }
}