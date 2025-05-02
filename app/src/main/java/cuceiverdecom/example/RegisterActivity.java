package cuceiverdecom.example;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private Button btnCreateAcc, backButton;
    private ProgressBar progressBar;

    private EditText editEmailSignup, editPasswordSignUp, editPasswordSignUp2, editNameSignUp;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        progressBar = findViewById(R.id.progressBarRegister);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, cuceiverdecom.example.mainActivity.class));
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        editNameSignUp = findViewById(R.id.editNameSignup);
        editEmailSignup = findViewById(R.id.editEmailSignup);
        editPasswordSignUp = findViewById(R.id.editPasswordSignup);
        editPasswordSignUp2 = findViewById(R.id.editPasswordSignup2);
        btnCreateAcc = findViewById(R.id.btnCreateAcc);

        btnCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name ,email, password, repeatPassword;
                name = editNameSignUp.getText().toString();
                email = editEmailSignup.getText().toString();
                password = editPasswordSignUp.getText().toString();
                repeatPassword = editPasswordSignUp2.getText().toString();


                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(repeatPassword)){
                    Toast.makeText(RegisterActivity.this, "Por favor, ingrese todos los campos", Toast.LENGTH_SHORT).show();
                }
                else{
                    if (name.length() < 3 || name.length() > 10) {
                        Toast.makeText(RegisterActivity.this, "El nombre debe tener entre 3 y 10 caracteres", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(RegisterActivity.this, "Ingrese un email válido", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (password.length() < 6) {
                        Toast.makeText(RegisterActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!password.equals(repeatPassword)) {
                        Toast.makeText(RegisterActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    hideKeyboard(RegisterActivity.this);

                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if(task.isSuccessful()){

                                FirebaseUser user = mAuth.getCurrentUser();

                                Map<String, Object> userData = new HashMap<>();
                                userData.put("uid", user.getUid());
                                userData.put("nombre", name);
                                userData.put("correo", user.getEmail());
                                userData.put("fotoURL", "");

                                db.collection("usuarios").document(user.getUid())
                                        .set(userData)
                                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "Usuario guardado correctamente con correo"))
                                        .addOnFailureListener(e -> Log.e("Firestore", "Error al guardar usuario", e));

                                Toast.makeText(RegisterActivity.this, "Usuario " + name + " creado correctamente", Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                                startActivity(new Intent(RegisterActivity.this, cuceiverdecom.example.mainActivity.class));
                                finish();
                            }
                            else{
                                Toast.makeText(RegisterActivity.this,
                                        "Error: " + Objects.requireNonNull(task.getException()).getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }

        });
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
