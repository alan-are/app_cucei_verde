package cuceiverdecom.example;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class reportCreation extends AppCompatActivity {

    private Button backBtn, btnAccion;
    private EditText editTextTitle, editTextDescription;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_creation);

        // Inicializar Firebase (solo Firestore y Auth)
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Vincular vistas
        backBtn = findViewById(R.id.backButtonReportCreation);
        btnAccion = findViewById(R.id.btnAccion);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextTextMultiLine2);

        // Configurar botón de regreso
        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(reportCreation.this, menu_principal.class));
            finish();
        });

        // Configurar botón para guardar reporte
        btnAccion.setOnClickListener(v -> saveReport());
    }

    private void saveReport() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        // Validación básica
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Debes iniciar sesión para crear un reporte", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear mapa con datos del reporte (sin imagen)
        Map<String, Object> report = new HashMap<>();
        report.put("titulo", title);
        report.put("descripcion", description);
        report.put("usuarioId", user.getUid());
        report.put("usuarioEmail", user.getEmail());
        report.put("fecha", System.currentTimeMillis());
        report.put("estado", "pendiente");
        report.put("etiqueta", "general");

        // Guardar directamente en Firestore
        saveReportToFirestore(report);
    }

    private void saveReportToFirestore(Map<String, Object> report) {
        db.collection("reportes")
                .add(report)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Reporte creado con éxito", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // <-- Esto activará la actualización
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al crear reporte: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}