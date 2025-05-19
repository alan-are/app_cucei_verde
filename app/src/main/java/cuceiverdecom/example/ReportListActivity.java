package cuceiverdecom.example;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView; // Added for search functionality

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
    private List<Report> filteredReportList; // Added for search
    private ProgressBar progressBar;
    private TextView tvNoReports;
    private androidx.appcompat.widget.Toolbar toolbar; 
    private SearchView searchViewReports; // Added for search

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
        toolbar = findViewById(R.id.toolbar); 
        searchViewReports = findViewById(R.id.searchViewReports); // Initialize SearchView
        setSupportActionBar(toolbar); 

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        // Configurar RecyclerView
        recyclerViewReports.setLayoutManager(new LinearLayoutManager(this));
        reportList = new ArrayList<>();
        filteredReportList = new ArrayList<>(); // Initialize filtered list
        reportAdapter = new ReportAdapter(this, filteredReportList, this); // Use filtered list for adapter
        recyclerViewReports.setAdapter(reportAdapter);
        
        // Cargar reportes
        loadReports();
        setupSearch(); // Setup search functionality
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
                            // Set the ID in case it wasn\'t populated automatically
                            if (report.getId() == null) {
                                report.setId(document.getId());
                            }
                            reportList.add(report);
                        } catch (Exception e) {
                            Log.e(TAG, "Error converting document to Report: " + e.getMessage());
                        }
                    }
                    
                    filterReports(""); // Initially display all reports
                    
                    // Mostrar mensaje si no hay reportes
                    if (reportList.isEmpty()) { // Check original list for initial "no reports" message
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

    private void setupSearch() {
        searchViewReports.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterReports(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterReports(newText);
                return true;
            }
        });
    }

    private void filterReports(String query) {
        filteredReportList.clear();
        if (query.isEmpty()) {
            filteredReportList.addAll(reportList);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (Report report : reportList) {
                if ((report.getTitle() != null && report.getTitle().toLowerCase().contains(lowerCaseQuery)) ||
                    (report.getDescription() != null && report.getDescription().toLowerCase().contains(lowerCaseQuery)) ||
                    (report.getCategory() != null && report.getCategory().toLowerCase().contains(lowerCaseQuery)) ||
                    (report.getLocation() != null && report.getLocation().toLowerCase().contains(lowerCaseQuery))) {
                    filteredReportList.add(report);
                }
            }
        }
        reportAdapter.notifyDataSetChanged();

        // Update "no reports" message based on filtered list
        if (filteredReportList.isEmpty()) {
            tvNoReports.setVisibility(View.VISIBLE);
            recyclerViewReports.setVisibility(View.GONE);
        } else {
            tvNoReports.setVisibility(View.GONE);
            recyclerViewReports.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handle the back button click on the toolbar
        onBackPressed(); // This will call finish() on the activity
        return true;
    }
}
