package cuceiverdecom.example;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class reportCreation extends AppCompatActivity {

    private static final String TAG = "reportCreation";

    private androidx.appcompat.widget.Toolbar toolbar;
    private Button btnAccion;
    private EditText editTextTitle, editTextDescription, editTextLocation; // Added editTextLocation
    private RadioGroup radioGroupCategory;
    private Button btnSelectReportImage;
    private ImageView ivReportImagePreview;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private ProgressDialog progressDialog;

    private Uri selectedImageUri;
    private String manualLocationText; // To store text from editTextLocation

    // ActivityResultLaunchers
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<String> requestStoragePermissionLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_creation);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();


        // Initialize components de la UI
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        btnAccion = findViewById(R.id.btnAccion);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextTextMultiLine2);
        editTextLocation = findViewById(R.id.editTextLocation); // Initialize new EditText
        radioGroupCategory = findViewById(R.id.radioGroupCategory);
        btnSelectReportImage = findViewById(R.id.btnSelectReportImage);
        ivReportImagePreview = findViewById(R.id.ivReportImagePreview);

        initializeActivityLaunchers();

        btnAccion.setOnClickListener(v -> validateAndSubmitReport());
        btnSelectReportImage.setOnClickListener(v -> openImageChooser());
    }

    private void initializeActivityLaunchers() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        selectedImageUri = result.getData().getData();
                        Glide.with(this)
                             .load(selectedImageUri)
                             .into(ivReportImagePreview);
                        ivReportImagePreview.setVisibility(View.VISIBLE);
                    }
                });

        requestStoragePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        launchImagePicker();
                    } else {
                        Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openImageChooser() {
        String permissionToRequest;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissionToRequest = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permissionToRequest = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permissionToRequest) == PackageManager.PERMISSION_GRANTED) {
            launchImagePicker();
        } else {
            requestStoragePermissionLauncher.launch(permissionToRequest);
        }
    }

    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void validateAndSubmitReport() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        manualLocationText = editTextLocation.getText().toString().trim(); // Get text from new EditText
        int selectedCategoryId = radioGroupCategory.getCheckedRadioButtonId();
        String category = "";
        if (selectedCategoryId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedCategoryId);
            category = selectedRadioButton.getText().toString();
        }

        if (TextUtils.isEmpty(title)) {
            editTextTitle.setError("Por favor, ingrese un título");
            return;
        }
        if (TextUtils.isEmpty(description)) {
            editTextDescription.setError("Por favor, ingrese una descripción");
            return;
        }
        if (TextUtils.isEmpty(category)) {
            Toast.makeText(this, "Por favor, seleccione una categoría", Toast.LENGTH_SHORT).show();
            return;
        }
        // Location text is now optional, but if you want to make it mandatory:
        // if (TextUtils.isEmpty(manualLocationText)) {
        //     editTextLocation.setError("Por favor, ingrese una ubicación");
        //     return;
        // }


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Debe iniciar sesión para enviar un reporte", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Enviando reporte...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String reportId = UUID.randomUUID().toString();
        Map<String, Object> reportData = new HashMap<>();
        reportData.put("id", reportId);
        reportData.put("title", title);
        reportData.put("description", description);
        reportData.put("category", category);
        reportData.put("userId", currentUser.getUid());
        reportData.put("userName", ""); // Will be fetched
        reportData.put("imageUrl", "");
        
        // Store manual location text instead of GeoPoint and placeName
        reportData.put("location", manualLocationText); // Storing the text directly
        
        reportData.put("creationDate", Timestamp.now());
        reportData.put("status", "Pendiente");
        reportData.put("solvedVotesCount", 0);
        reportData.put("unsolvedVotesCount", 0);
        reportData.put("userVotes", new HashMap<String, String>());

        db.collection("usuarios").document(currentUser.getUid())
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String userName = documentSnapshot.getString("nombre");
                    reportData.put("userName", userName != null ? userName : "Usuario Anónimo");
                } else {
                    reportData.put("userName", "Usuario Anónimo");
                }
                if (selectedImageUri != null) {
                    uploadImageAndSaveReport(reportId, reportData);
                } else {
                    saveReportToFirestore(reportId, reportData);
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error al obtener datos del usuario", e);
                reportData.put("userName", "Usuario Anónimo");
                if (selectedImageUri != null) {
                    uploadImageAndSaveReport(reportId, reportData);
                } else {
                    saveReportToFirestore(reportId, reportData);
                }
            });
    }

    private void uploadImageAndSaveReport(String reportId, Map<String, Object> reportData) {
        if (selectedImageUri == null) {
            saveReportToFirestore(reportId, reportData);
            return;
        }
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Subiendo imagen y enviando reporte...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        } else {
            progressDialog.setMessage("Subiendo imagen...");
        }

        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("report_images/" + reportId + "/" + UUID.randomUUID().toString() + ".jpg");

        imageRef.putFile(selectedImageUri)
            .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                reportData.put("imageUrl", imageUrl);
                saveReportToFirestore(reportId, reportData);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error al obtener URL de descarga", e);
                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(reportCreation.this, "Error al obtener URL de imagen: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }))
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error al subir imagen", e);
                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(reportCreation.this, "Error al subir imagen: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    private void saveReportToFirestore(String reportId, Map<String, Object> reportData) {
        if (progressDialog != null && progressDialog.isShowing()) {
             progressDialog.setMessage(selectedImageUri == null ? "Enviando reporte..." : "Finalizando envío...");
        }

        db.collection("reportes").document(reportId)
            .set(reportData)
            .addOnSuccessListener(aVoid -> {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(reportCreation.this, "Reporte enviado con éxito", Toast.LENGTH_SHORT).show();
                clearForm();
                finish();
            })
            .addOnFailureListener(e -> {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Log.e(TAG, "Error al guardar reporte", e);
                Toast.makeText(reportCreation.this, "Error al enviar reporte: " + e.getMessage(),
                              Toast.LENGTH_LONG).show();
            });
    }

    private void clearForm() {
        editTextTitle.setText("");
        editTextDescription.setText("");
        editTextLocation.setText(""); // Clear the new location EditText
        radioGroupCategory.clearCheck();
        if (ivReportImagePreview != null) {
            ivReportImagePreview.setImageDrawable(null);
            ivReportImagePreview.setVisibility(View.GONE);
        }
        selectedImageUri = null;
        manualLocationText = null;
    }
}
