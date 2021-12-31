package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.adapter.CallAdapter;
import com.example.myapplication.data.Call;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.Map;

public class PhoneActivity extends AppCompatActivity {

    // Call
    ListView list;
    ProgressDialog pd;
    public static ArrayList<Call> callList;
    public static ArrayList<Call> callList2;
    CallAdapter callAdapter;

    // Gallery
    final int PICTURE_REQUEST_CODE = 100;
    LinearLayout imgLayout;
    LinearLayout.LayoutParams imgParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phonebook);

        EditText callText = (EditText) findViewById(R.id.editSearch);
        callText.setVisibility(View.VISIBLE);

        // 연락처 권한 추가
        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("연락처 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(Manifest.permission.READ_CONTACTS)
                .check();

        // 연락처 동기화
        // ImageButton callBtn = (ImageButton) findViewById(R.id.callBtn);
        //callBtn.setOnClickListener(new View.OnClickListener() {
        //@Override
        //public void onClick(View v) {
        // TODO Auto-generated method stub
        LoadContactsAyscn lca = new LoadContactsAyscn();
        lca.execute();
        //callBtn.setVisibility(View.GONE);
        callText.setVisibility(View.VISIBLE);
        //}
        //});

        // call search
        callText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                String text = callText.getText().toString();
                search(text);
            }
        });

        // Call List Click event 리스트뷰 클릭시 상세화면 보기 이벤트
        list = (ListView) findViewById(R.id.listView1);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PhoneActivity.this, CallActivity.class);
                intent.putExtra("POSITION", position);
                startActivity(intent);
            }
        });



    }

    public void search(String charText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        callList.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length() == 0) {
            callList.addAll(callList2);
        }
        // 문자 입력을 할때..
        else
        {
            // 리스트의 모든 데이터를 검색한다.
            for(int i = 0;i < callList2.size(); i++)
            {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (callList2.get(i).getName().toLowerCase().contains(charText))
                {
                    // 검색된 데이터를 리스트에 추가한다.
                    callList.add(callList2.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        callAdapter.notifyDataSetChanged();
    }

    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(getApplicationContext(), "권한이 허용됨",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(), "권한이 거부됨",Toast.LENGTH_SHORT).show();
        }
    };







    // make ImageView
    void makeImg(Uri uri) {
        imgParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 600);
        imgParams.setMargins(50,50,50,50);
        ImageView iv = new ImageView(this);  // 새로 추가할 imageView 생성
        iv.setImageURI(uri);  // imageView에 내용 추가
        iv.setLayoutParams(imgParams);  // imageView layout 설정
        imgLayout.addView(iv); // 기존 linearLayout에 imageView 추가
    }

    // Get phone gallery data.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //ClipData 또는 Uri를 가져온다
//                Uri uri = data.getData();
                ClipData clipData = data.getClipData();
                //이미지 URI 를 이용하여 이미지뷰에 순서대로 세팅한다.
                if (clipData != null) {
                    for (int j = 0; j < clipData.getItemCount(); j++) {
                        Uri urione = clipData.getItemAt(j).getUri();
                        makeImg(urione);
                    }
                }
            }
        }
    }

    class LoadContactsAyscn extends AsyncTask<Void, Void, ArrayList<Call>> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            pd = ProgressDialog.show(PhoneActivity.this, "Loading Contacts",
                    "Please Wait");
        }

        @SuppressLint("Range")
        @Override
        protected ArrayList<Call> doInBackground(Void... params) {
            // TODO Auto-generated method stubco
            ArrayList<Map<String, String>> contacts = new ArrayList<Map<String, String>>();

            callList = new ArrayList<Call>();

            Cursor c = getContentResolver().query(
                    ContactsContract.Contacts.CONTENT_URI, null,
                    null, null, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY+" asc");

            int caseNum = 0;
            while (c.moveToNext()) {
                //연락처 id값
                String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));

                //연락처 대표이름
                String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));

                //id로 전화 정보 조회
                Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                String number="";
                //데이터가 있는 경우
                if(phoneCursor.moveToFirst()){
                    number = phoneCursor.getString(phoneCursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                }

                caseNum = caseNum % 5;
                switch (caseNum) {
                    case 0:
                        callList.add(new Call(R.drawable.call, name, number, R.drawable.hi));
                        break;
                    case 1:
                        callList.add(new Call(R.drawable.call, name, number, R.drawable.hi));
                        break;
                    case 2:
                        callList.add(new Call(R.drawable.call, name, number, R.drawable.hi));
                        break;
                    case 3:
                        callList.add(new Call(R.drawable.call, name, number, R.drawable.hi));
                        break;
                    case 4:
                        callList.add(new Call(R.drawable.call, name, number, R.drawable.hi));
                        break;

                }
                phoneCursor.close();
                caseNum++;
            } //end while

            c.close();
            return callList;
        }

        @Override
        protected void onPostExecute(ArrayList<Call> callList) {
            // TODO Auto-generated method stub
            super.onPostExecute(callList);
            pd.cancel();
            callList2 = new ArrayList<Call>();
            callList2.addAll(callList);
            System.out.println("CallList2: " + callList2);
            callAdapter = new CallAdapter(getApplicationContext(), callList);
            list.setAdapter(callAdapter);
        }

    }
}
