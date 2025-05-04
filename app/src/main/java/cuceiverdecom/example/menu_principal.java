package cuceiverdecom.example;

import static cuceiverdecom.example.R.*;

import cuceiverdecom.example.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class menu_principal extends AppCompatActivity {

    private Button log_out_btn;

    private ImageButton btnCreateReport;
    private GoogleSignInClient googleSignInClient; // Solo si usaste Google Sign-In

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.menu_principal);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        btnCreateReport = findViewById(R.id.btnCreateReport);
        btnCreateReport.setOnClickListener(v -> {
            Intent intent = new Intent(this, reportCreation.class);
            startActivity(intent);
        });

        log_out_btn = findViewById(R.id.log_out_btn);
        log_out_btn.setOnClickListener(v -> logoutUser());
    }

    private void logoutUser() {
        // 1. Cierra sesión en Firebase
        FirebaseAuth.getInstance().signOut();

        // 2. Cierra sesión en Google (si aplica)
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            // 3. Limpia datos locales (opcional)
            getSharedPreferences("MisPreferencias", MODE_PRIVATE).edit().clear().apply();

            // 4. Redirige al login
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, mainActivity.class));
            finish();
        });
    }

}

