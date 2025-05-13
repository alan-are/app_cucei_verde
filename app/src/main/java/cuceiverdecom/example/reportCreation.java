package cuceiverdecom.example;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class reportCreation extends AppCompatActivity {

    private static final String TAG = "reportCreation";
    
    private androidx.appcompat.widget.Toolbar toolbar; // Changed from Button backBtn
    private Button btnAccion;
    private EditText editTextTitle, editTextDescription;
    private ImageButton imageButton, imageButton2;
    
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_creation);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        
        // Inicializar componentes de la UI
        toolbar = findViewById(R.id.toolbar); // Changed from backBtn and ID
        setSupportActionBar(toolbar); // Set the toolbar as the action bar
        
        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        btnAccion = findViewById(R.id.btnAccion);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextTextMultiLine2);
        imageButton = findViewById(R.id.imageButton);
        imageButton2 = findViewById(R.id.imageButton2);
        
        // Configurar el botón de acción para enviar el reporte
        btnAccion.setOnClickListener(v -> {
            validateAndSubmitReport();
        });
        
        // Por ahora, estos botones mostrarán mensajes de que la funcionalidad está en desarrollo
        imageButton.setOnClickListener(v -> {
            Toast.makeText(this, "Funcionalidad de imagen en desarrollo", Toast.LENGTH_SHORT).show();
        });
        
        imageButton2.setOnClickListener(v -> {
            Toast.makeText(this, "Funcionalidad de ubicación en desarrollo", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handle the back button click on the toolbar
        onBackPressed();
        return true;
    }
    
    private void validateAndSubmitReport() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        
        // Validar campos
        if (TextUtils.isEmpty(title)) {
            editTextTitle.setError("Por favor, ingrese un título");
            return;
        }
        
        if (TextUtils.isEmpty(description)) {
            editTextDescription.setError("Por favor, ingrese una descripción");
            return;
        }
        
        // Verificar si hay un usuario autenticado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Debe iniciar sesión para enviar un reporte", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Mostrar diálogo de progreso
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Enviando reporte...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        
        // Generar ID para el reporte
        String reportId = UUID.randomUUID().toString();
        
        // Crear objeto con los datos del reporte
        Map<String, Object> reportData = new HashMap<>();
        reportData.put("id", reportId);
        reportData.put("title", title);
        reportData.put("description", description);
        reportData.put("category", "General"); // Por defecto, se podría mejorar para seleccionar categorías
        reportData.put("userId", currentUser.getUid());
        reportData.put("userName", ""); // Se completará más adelante
        reportData.put("imageUrl", "");
        reportData.put("location", "");
        reportData.put("creationDate", Timestamp.now());
        reportData.put("status", "Pendiente");
        
        // Obtener el nombre del usuario desde Firestore
        db.collection("usuarios").document(currentUser.getUid())
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String userName = documentSnapshot.getString("nombre");
                    reportData.put("userName", userName);
                }
                
                // Ahora guardar el reporte en la colección "reportes"
                saveReportToFirestore(reportId, reportData);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error al obtener datos del usuario", e);
                progressDialog.dismiss();
                Toast.makeText(reportCreation.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
    
    private void saveReportToFirestore(String reportId, Map<String, Object> reportData) {
        db.collection("reportes").document(reportId)
            .set(reportData)
            .addOnSuccessListener(aVoid -> {
                progressDialog.dismiss();
                Toast.makeText(reportCreation.this, "Reporte enviado con éxito", Toast.LENGTH_SHORT).show();
                
                // Limpiar campos
                editTextTitle.setText("");
                editTextDescription.setText("");
                
                // Regresar a la pantalla anterior
                Intent intent = new Intent(reportCreation.this, menu_principal.class);
                startActivity(intent);
                finish();
            })
            .addOnFailureListener(e -> {
                progressDialog.dismiss();
                Log.e(TAG, "Error al guardar reporte", e);
                Toast.makeText(reportCreation.this, "Error al enviar reporte: " + e.getMessage(), 
                              Toast.LENGTH_SHORT).show();
            });
    }
}
