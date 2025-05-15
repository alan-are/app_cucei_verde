package cuceiverdecom.example;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReportListActivity extends AppCompatActivity implements ReportAdapter.OnReportClickListener {

    private static final String TAG = "ReportListActivity";
    private RecyclerView recyclerViewReports;
    private ReportAdapter reportAdapter;
    private List<Report> reportList;
    private ProgressBar progressBar;
    private TextView tvNoReports;
    private androidx.appcompat.widget.Toolbar toolbar; // Changed from Button backButton
    
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Inicializar vistas
        progressBar = findViewById(R.id.progressBarReportList);
        tvNoReports = findViewById(R.id.tvNoReports);
        recyclerViewReports = findViewById(R.id.recyclerViewReports);
        toolbar = findViewById(R.id.toolbar); // Changed from backButton and ID
        setSupportActionBar(toolbar); // Set the toolbar as the action bar

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        // Configurar RecyclerView
        recyclerViewReports.setLayoutManager(new LinearLayoutManager(this));
        reportList = new ArrayList<>();
        reportAdapter = new ReportAdapter(this, reportList, this);
        recyclerViewReports.setAdapter(reportAdapter);
        
        // Cargar reportes
        loadReports();
    }    private void loadReports() {
        progressBar.setVisibility(View.VISIBLE);
        
        db.collection("reportes")
            .orderBy("creationDate", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                
                if (task.isSuccessful()) {
                    reportList.clear();
                    
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        try {
                            Report report = document.toObject(Report.class);
                            // Set the ID in case it wasn't populated automatically
                            if (report.getId() == null) {
                                report.setId(document.getId());
                            }
                            reportList.add(report);
                        } catch (Exception e) {
                            Log.e(TAG, "Error converting document to Report: " + e.getMessage());
                        }
                    }
                    
                    reportAdapter.notifyDataSetChanged();
                    
                    // Mostrar mensaje si no hay reportes
                    if (reportList.isEmpty()) {
                        tvNoReports.setVisibility(View.VISIBLE);
                        recyclerViewReports.setVisibility(View.GONE);
                    } else {
                        tvNoReports.setVisibility(View.GONE);
                        recyclerViewReports.setVisibility(View.VISIBLE);
                    }
                    
                } else {
                    Log.w(TAG, "Error obteniendo reportes.", task.getException());
                    Toast.makeText(ReportListActivity.this, "Error al cargar reportes: " + 
                                  (task.getException() != null ? task.getException().getMessage() : "Error desconocido"), 
                                  Toast.LENGTH_SHORT).show();
                    
                    // Show empty state
                    tvNoReports.setVisibility(View.VISIBLE);
                    recyclerViewReports.setVisibility(View.GONE);
                }
            })
            .addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Complete failure to fetch reports", e);
                Toast.makeText(ReportListActivity.this, "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show();
                
                // Show empty state
                tvNoReports.setVisibility(View.VISIBLE);                recyclerViewReports.setVisibility(View.GONE);
            });
    }

    @Override
    public void onReportClick(Report report, int position) {
        // Aquí se manejaría el clic en un reporte para mostrar detalles
        Toast.makeText(this, "Seleccionado: " + report.getTitle(), Toast.LENGTH_SHORT).show();
        
        // Para la siguiente fase, aquí se abriría una actividad de detalle
        // Intent intent = new Intent(ReportListActivity.this, ReportDetailActivity.class);
        // intent.putExtra("REPORT_ID", report.getId());
        // startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handle the back button click on the toolbar
        onBackPressed(); // This will call finish() on the activity
        return true;
    }
}
