package cuceiverdecom.example;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cuceiverdecom.example.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class mainActivity extends AppCompatActivity  {

    private Button btnLogin;
    private TextView textViewSignUp;
    private Button google_sign_in;
    private EditText editEmailLogin;
    private EditText editPasswordLogin;

    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    private FirebaseDatabase database;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private GoogleSignInClient googleSignInClient;
    private int RC_SIGN_IN = 20;

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, menu_principal.class));
            finish();
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBarLogin);

        btnLogin = findViewById(R.id.btnLogin);
        editEmailLogin = findViewById(R.id.editEmailLogin);
        editPasswordLogin = findViewById(R.id.editPasswordLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email, password;
                email = editEmailLogin.getText().toString();
                password = editPasswordLogin.getText().toString();
                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    Toast.makeText(mainActivity.this,"Por favor, ingrese todos los campos",Toast.LENGTH_SHORT).show();
                } else{
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(mainActivity.this, "Correo electrónico inválido", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        hideKeyboard(mainActivity.this);
                        progressBar.setVisibility(View.VISIBLE);
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    db.collection("usuarios").document(user.getUid()).get().addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            String nombre = documentSnapshot.getString("nombre");
                                            Toast.makeText(mainActivity.this, "Bienvenido: " + nombre, Toast.LENGTH_SHORT).show();
                                            updateUI(user);
                                        } else {
                                            Toast.makeText(mainActivity.this,
                                                    "Error: " + Objects.requireNonNull(task.getException()).getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                                else {
                                    Toast.makeText(mainActivity.this,"Error: Usuario o contraseña incorrectos",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });

        textViewSignUp = findViewById(R.id.textViewSignUp);
        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mainActivity.this, RegisterActivity.class));
            }
        });        // Configuración de Firebase
        database = FirebaseDatabase.getInstance();

        // Configuración de Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        google_sign_in = findViewById(R.id.button2);
        google_sign_in.setOnClickListener(v -> {
            // Mostrar mensajes de verificación en modo desarrollo
            if (BuildConfig.DEBUG) {
                checkGoogleSignInConfiguration();
            }
            signInWithGoogle();
        });
    }

    // Método para ayudar a diagnosticar problemas de configuración de Google Sign-In
    private void checkGoogleSignInConfiguration() {
        try {
            String webClientId = getString(R.string.default_web_client_id);
            Log.d("GoogleSignIn", "Web Client ID: " + webClientId);
            
            // Verificar si el ID de cliente web coincide con el de google-services.json
            if (webClientId.isEmpty() || webClientId.equals("default_web_client_id")) {
                Toast.makeText(this, "Error: default_web_client_id no encontrado", Toast.LENGTH_LONG).show();
                Log.e("GoogleSignIn", "Web Client ID no encontrado o es el valor por defecto");
            }

            // Mostrar el SHA-1 de la aplicación (solo para depuración)
            try {
                PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
                for (android.content.pm.Signature signature : info.signatures) {
                    MessageDigest md = MessageDigest.getInstance("SHA-1");
                    md.update(signature.toByteArray());
                    String shaKey = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                    Log.d("GoogleSignIn", "SHA-1 Key: " + shaKey);
                }
            } catch (Exception e) {
                Log.e("GoogleSignIn", "Error al obtener SHA-1", e);
            }
        } catch (Exception e) {
            Log.e("GoogleSignIn", "Error en verificación de configuración", e);
        }
    }
    private void signInWithGoogle() {
        progressBar.setVisibility(View.VISIBLE);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            progressBar.setVisibility(View.VISIBLE);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("GoogleSignIn", "GoogleSignInAccount recuperado correctamente");
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                progressBar.setVisibility(View.GONE);
                Log.e("GoogleSignIn", "Error en inicio de sesión con Google: " + e.getMessage() + ", código: " + e.getStatusCode(), e);
                Toast.makeText(this, "Error al iniciar sesión con Google: " + getGoogleSignInErrorMessage(e.getStatusCode()), Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getGoogleSignInErrorMessage(int statusCode) {
        switch (statusCode) {
            case 12500: // SIGN_IN_CANCELLED
                return "Inicio de sesión cancelado";
            case 12501: // SIGN_IN_CURRENTLY_IN_PROGRESS
                return "Inicio de sesión en progreso";
            case 12502: // SIGN_IN_FAILED
                return "Fallo en el inicio de sesión";
            case 15: // NETWORK_ERROR
                return "Error de red, verifica tu conexión";
            case 8: // INTERNAL_ERROR
                return "Error interno del servidor";
            case 10: // DEVELOPER_ERROR
                return "Error de configuración. Verifica la SHA-1 en Firebase";
            default:
                return "Error desconocido: " + statusCode;
        }
    }    private void firebaseAuthWithGoogle(String idToken) {
        progressBar.setVisibility(View.VISIBLE);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        Map<String, Object> userData = new HashMap<>();
                        userData.put("uid", user.getUid());
                        userData.put("nombre", user.getDisplayName());
                        userData.put("correo", user.getEmail());
                        userData.put("fotoURL", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);

                        db.collection("usuarios").document(user.getUid())
                                .set(userData)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Usuario guardado correctamente con correo"))
                                .addOnFailureListener(e -> Log.e("Firestore", "Error al guardar usuario", e));

                        Toast.makeText(mainActivity.this, "Bienvenido: " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                        updateUI(user);
                    } else {
                        Log.e("FirebaseAuth", "Error de autenticación con Firebase", task.getException());
                        Toast.makeText(mainActivity.this, "Error de autenticación: " + 
                                (task.getException() != null ? task.getException().getMessage() : "Desconocido"), 
                                Toast.LENGTH_LONG).show();
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(mainActivity.this, menu_principal.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(mainActivity.this, "Autenticacion fallo",
                    Toast.LENGTH_SHORT).show();
        }
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}