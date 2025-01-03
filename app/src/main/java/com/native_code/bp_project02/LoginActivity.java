package com.native_code.bp_project02;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;

import android.widget.Button;

import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginActivity extends AppCompatActivity  {
    private static final String TAG = "LoginActivity";
    private Context mContext;
    private FirebaseAuth mAuth; //파이어베이스 인증객체
    private CallbackManager mCallbackManager;
    private GoogleSignInClient mGoogleSignInClient; //구글 API 클라이언트 객체
    private static final int REO_SIGN_GOOGLE = 100; //구글 로그인 결과 코드
    private EditText mEtPW;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = LoginActivity.this;
        mAuth = FirebaseAuth.getInstance(); // 파이어베이스 인증 객체 초기화
        mCallbackManager = CallbackManager.Factory.create();

        mEtPW = (EditText) findViewById(R.id.passwordEditText);
        mEtPW.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mEtPW.setSingleLine();

        mEtPW.setTransformationMethod(PasswordTransformationMethod.getInstance());





        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("737568200127-fpc92gko7a732cp31jb7db4fu5qcoeni.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        revokeAccess();


        //페북로그인버튼
        Button facebookcustomButton = findViewById(R.id.activity_login_customfacebooklogin);

        LoginButton facebookloginButton = findViewById(R.id.activity_login_facebookloginbutton);
        facebookloginButton.setReadPermissions("email");
        facebookloginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);

            }
        });


        findViewById(R.id.CheckButton).setOnClickListener(onClickListener);
        findViewById(R.id.gotoPasswordResetbutton).setOnClickListener(onClickListener);
        findViewById(R.id.signUp_Activity_Button).setOnClickListener(onClickListener);
        //findViewById(R.id.activity_login_TemporaryNativeButton).setOnClickListener(onClickListener);
        //findViewById(R.id.activity_login_TemporaryTravelerButton).setOnClickListener(onClickListener);
        findViewById(R.id.activity_login_customGoogleLogin).setOnClickListener(onClickListener);
        facebookcustomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookloginButton.performClick();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        LoginManager.getInstance().logOut();
        // 어플이 정상적으로 종료되지 않았을 때 페이스북 로그아웃 시키기. 나중에 어플 로그인 유지시키고 싶으면 수정 가능
    }




    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) { //페이스북 로그인 실패했을 때
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getApplicationContext(), "페이스북 로그인 실패",
                                    Toast.LENGTH_SHORT).show();
                            LoginManager.getInstance().logOut();

                            if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                                //유저 충돌로 인한 로그인 실패일 경우(이미 같은 이메일로 다른 계정이 존재하는 경우)
                                Toast.makeText(getApplicationContext(), "이 이메일로 다른 계정이 이미 존재합니다.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            LoginManager.getInstance().logOut();
                        } else { //페이스북 로그인 성공
                            Log.d(TAG, "signInWithCredential:success");
                            userLogin();
                        }
                    }
                });
    }


    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REO_SIGN_GOOGLE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { //구글로그인인증을요청했을때 결과값을 되돌려받는곳임
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REO_SIGN_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                if(e.getStatusCode() != 12501){
                    Log.w(TAG, "Google sign in failed", e);
                    Toast.makeText(LoginActivity.this,"로그인 실패",Toast.LENGTH_SHORT).show();
                } else {
                    Log.w(TAG, "Google sign in failed", e);
                }

            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {//구글로그인이 성공했으면
                                    userLogin();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                                }
                            }
                        }
                );
    }



    private void userLogin(){
        //로그인이 성공했으면
            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
            FirebaseUser user = mAuth.getCurrentUser();
            String uid = user.getUid();

            // 파이어스토어 객체선언
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            //파이어스토어에서 해당 유저의 uid를 이용하여 정보 가져오기
            final DocumentReference docRef = db.collection("users").document(uid);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                //정보 가져오는 것이 성공적일 때
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        //DocumentSnapshot에 정보를 담아둠
                        DocumentSnapshot document = task.getResult();

                        //document가 null이 아닐 때
                        if (document != null) {

                            //재차 확인
                            if (document.exists()) {

                                MystartActivity(MainActivity.class);
                            } else {

//                                                    //로그인은 됐는데, 상세정보가 등록되어 있지 않으면 MemberActivity클래스로 이동
                                MystartActivity(MemberActivity.class);
                            }
                        }
                    } else { //로그인 실패했으면
                        Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            });



    }


    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.CheckButton:
                    login();
                    break;

                case R.id.gotoPasswordResetbutton:
                    MystartActivity(PasswordResetActivity.class);
                    break;

                case R.id.signUp_Activity_Button:
                    MystartActivity(Sign_UpActivity.class);
                    break;

               /* case R.id.activity_login_TemporaryNativeButton:
                    //Temporary_native_login();
                    break;

                case R.id.activity_login_TemporaryTravelerButton:
                    //Temporary_traveler_login();
                    break;*/

                case R.id.activity_login_customGoogleLogin:
                    signIn();
                    break;

            }



        }





    };

    private void login () {

        String email = ((EditText) findViewById(R.id.NameEditText)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();






        if (email.length() > 0 && password.length() > 0) {

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                userLogin();
                            } else {

                                if (task.getException() != null) {
                                    startToast("이메일 또는 비밀번호를 확인해주세요.");
                                }
                            }

                        }
                    });
        } else {

            startToast("이메일 또는 비밀번호를 입력해주세요.");
        }


    }

    private void Temporary_native_login () {

        String email = "native@naver.com";
        String password = "123456";

        if (email.length() > 0 && password.length() > 0) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                userLogin();
                            } else {
                                if (task.getException() != null) {
                                    startToast(task.getException().toString());
                                }
                            }
                            // ...
                        }
                    });
        } else {

            startToast("이메일 또는 비밀번호를 입력해주세요.");
        }

    }

    private void Temporary_traveler_login () {

        String email = "traveler@naver.com";
        String password = "123456";

        if (email.length() > 0 && password.length() > 0) {

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                userLogin();
                            } else {

                                if (task.getException() != null) {
                                    startToast(task.getException().toString());
                                }
                            }
                            // ...
                        }
                    });

        } else {

            startToast("이메일 또는 비밀번호를 입력해주세요.");
        }

    }

    private void startToast (String msg){

        Toast.makeText(LoginActivity.this, msg,
                Toast.LENGTH_SHORT).show();
    }

    private void MystartActivity (Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void revokeAccess() {
        //구글로그인 초기화 시키기(Access 해제시키기)
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }


}

