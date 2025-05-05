package cuceiverdecom.example;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class menu_principal extends AppCompatActivity {

    private static final int CREATE_REPORT_REQUEST = 1;

    private ListView listViewReports;
    private FirebaseFirestore db;
    private Button log_out_btn;
    private ImageButton btnCreateReport;
    private GoogleSignInClient googleSignInClient;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.menu_principal);

        listViewReports = findViewById(R.id.listViewReports);
        db = FirebaseFirestore.getInstance();

        loadReports();

        // Configuración de Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Botón para crear reporte
        btnCreateReport = findViewById(R.id.btnCreateReport);
        btnCreateReport.setOnClickListener(v -> {
            Intent intent = new Intent(this, reportCreation.class);
            startActivityForResult(intent, CREATE_REPORT_REQUEST);
        });

        // Botón para cerrar sesión
        log_out_btn = findViewById(R.id.log_out_btn);
        log_out_btn.setOnClickListener(v -> logoutUser());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_REPORT_REQUEST && resultCode == RESULT_OK) {
            loadReports(); // Recargar los reportes cuando se crea uno nuevo
        }
    }

    private void loadReports() {
        db.collection("reportes")
                .orderBy("fecha", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> reports = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> report = document.getData();
                            report.put("id", document.getId());
                            reports.add(report);
                        }

                        if (reports.isEmpty()) {
                            listViewReports.setEmptyView(findViewById(R.id.emptyReportsView));
                        } else {
                            ReportAdapter adapter = new ReportAdapter(this, reports);
                            listViewReports.setAdapter(adapter);
                        }
                    } else {
                        Toast.makeText(this, "Error al cargar reportes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            getSharedPreferences("MisPreferencias", MODE_PRIVATE).edit().clear().apply();
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, mainActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Opcional: Recargar los reportes cuando la actividad vuelva a primer plano
        // loadReports();
    }
}