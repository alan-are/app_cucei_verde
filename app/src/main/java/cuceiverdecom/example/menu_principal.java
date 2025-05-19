package cuceiverdecom.example;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class menu_principal extends AppCompatActivity {

    private static final String TAG = "menu_principal"; // Added for logging
    private Button log_out_btn;
    private ImageButton btnCreateReport, btnViewReports;
    private GoogleSignInClient googleSignInClient;
    private CardView headerCard, optionsCard1, optionsCard2, cardTopCollaborators; // Added cardTopCollaborators
    private LinearLayout animationContainer;

    private RecyclerView rvCollaborators;
    private CollaboratorAdapter collaboratorAdapter;
    private List<Collaborator> collaboratorList;
    private TextView tvNoCollaborators;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.menu_principal);

        db = FirebaseFirestore.getInstance(); // Initialize Firestore

        initializeViews();
        applyAnimations();
        setupCollaboratorsSection(); // New method to setup collaborators

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Ensure this string resource exists
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        // ... existing listener setup ...
        btnCreateReport = findViewById(R.id.btnCreateReport);
        if (btnCreateReport != null) {
            btnCreateReport.setOnClickListener(v -> {
                animateButtonClick(v);
                new android.os.Handler().postDelayed(() -> {
                    Intent intent = new Intent(this, reportCreation.class);
                    startActivity(intent);
                }, 300);
            });
        }
        
        btnViewReports = findViewById(R.id.imageButton3);
        if (btnViewReports != null) {
            btnViewReports.setOnClickListener(v -> {
                animateButtonClick(v);
                new android.os.Handler().postDelayed(() -> {
                    Intent intent = new Intent(this, ReportListActivity.class);
                    startActivity(intent);
                }, 300);
            });
        }
        log_out_btn = findViewById(R.id.log_out_btn);
        if (log_out_btn != null) {
            log_out_btn.setOnClickListener(v -> {
                animateButtonClick(v);
                new android.os.Handler().postDelayed(this::logoutUser, 300);
            });
        }
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            getSharedPreferences("MisPreferencias", MODE_PRIVATE).edit().clear().apply();
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, cuceiverdecom.example.mainActivity.class));
            finish();
        });
    }

    private void initializeViews() {
        try {
            // Find our various UI elements safely - with null checks at each step
            // Try to find the toolbar - if not found, don't crash
            try {
                View titleView = findViewById(R.id.toolbar); 
                if (titleView != null) {
                    View parent = (View) titleView.getParent();
                    if (parent != null && parent.getParent() instanceof CardView) {
                        headerCard = (CardView) parent.getParent();
                    }
                }
            } catch (Exception e) {
                // Just log and continue if we can't find this view
                e.printStackTrace();
            }
        
            // Get views for options cards - safely
            try {
                View reportButton = findViewById(R.id.imageButton3);
                if (reportButton != null) {
                    View parent = (View) reportButton.getParent();
                    if (parent != null && parent.getParent() instanceof CardView) {
                        optionsCard1 = (CardView) parent.getParent();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            try {
                View createReportButton = findViewById(R.id.btnCreateReport);
                if (createReportButton != null) {
                    View parent = (View) createReportButton.getParent();
                    if (parent != null && parent.getParent() instanceof CardView) {
                        optionsCard2 = (CardView) parent.getParent();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
                      
            // try { // Commented out
            //     View chatIconView = findViewById(R.id.imageView); // Renamed from chatIcon to avoid confusion with the field // Commented out
            //     if (chatIconView != null) { // Commented out
            //         View parent = (View) chatIconView.getParent(); // Commented out
            //         if (parent != null && parent.getParent() instanceof CardView) { // Commented out
            //             chatCard = (CardView) parent.getParent(); // Commented out
            //         } // Commented out
            //     } // Commented out
            // } catch (Exception e) { // Commented out
            //     e.printStackTrace(); // Commented out
            // } // Commented out
            
            try {
                animationContainer = findViewById(R.id.animationContainer);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Initialize collaborators section views
            rvCollaborators = findViewById(R.id.rvCollaborators);
            tvNoCollaborators = findViewById(R.id.tvNoCollaborators);
            cardTopCollaborators = findViewById(R.id.cardTopCollaborators); // Initialize cardTopCollaborators

        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
            Toast.makeText(this, "Se produjo un error al inicializar la vista", Toast.LENGTH_SHORT).show();
        }
    }

    private void applyAnimations() {
        try {
            Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            fadeIn.setDuration(1000);
            
            Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up_fade_in);
            Animation scaleIn = AnimationUtils.loadAnimation(this, R.anim.scale_fade_in);
            Animation slideLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
            
            if (headerCard != null) headerCard.startAnimation(fadeIn);
            if (cardTopCollaborators != null) cardTopCollaborators.startAnimation(fadeIn); // Animate collaborators card

            if (optionsCard1 != null) {
                new android.os.Handler().postDelayed(() -> {
                    if (optionsCard1 != null && optionsCard1.getWindowToken() != null) {
                        optionsCard1.startAnimation(slideLeft);
                    }
                }, 300);
            }
            
            if (optionsCard2 != null) {
                new android.os.Handler().postDelayed(() -> {
                    if (optionsCard2 != null && optionsCard2.getWindowToken() != null) {
                        optionsCard2.startAnimation(slideLeft);
                    }
                }, 500);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error applying animations", e);
        }
    }
    
    private void animateButtonClick(View view) {
        Animation scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        view.startAnimation(scaleDown);
    }

    private void setupCollaboratorsSection() {
        collaboratorList = new ArrayList<>();
        collaboratorAdapter = new CollaboratorAdapter(this, collaboratorList);
        if (rvCollaborators != null) {
            rvCollaborators.setLayoutManager(new LinearLayoutManager(this));
            rvCollaborators.setAdapter(collaboratorAdapter);
            rvCollaborators.setNestedScrollingEnabled(false); // Important for NestedScrollView
        }
        fetchCollaboratorData();
    }

    private void fetchCollaboratorData() {
        db.collection("reportes")
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Map<String, Integer> userPostCounts = new HashMap<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String userId = document.getString("userId");
                        if (userId != null) {
                            userPostCounts.put(userId, userPostCounts.getOrDefault(userId, 0) + 1);
                        }
                    }
                    fetchUserNamesAndDisplay(userPostCounts);
                } else {
                    Log.e(TAG, "Error getting reports: ", task.getException());
                    if (tvNoCollaborators != null) tvNoCollaborators.setVisibility(View.VISIBLE);
                    if (rvCollaborators != null) rvCollaborators.setVisibility(View.GONE);
                }
            });
    }

    private void fetchUserNamesAndDisplay(Map<String, Integer> userPostCounts) {
        if (userPostCounts.isEmpty()) {
            if (tvNoCollaborators != null) tvNoCollaborators.setVisibility(View.VISIBLE);
            if (rvCollaborators != null) rvCollaborators.setVisibility(View.GONE);
            return;
        }

        List<Collaborator> tempCollaborators = new ArrayList<>();
        final int[] usersToFetch = {userPostCounts.size()};

        for (Map.Entry<String, Integer> entry : userPostCounts.entrySet()) {
            String userId = entry.getKey();
            int postCount = entry.getValue();

            db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener(userDocument -> {
                    String userName = "Usuario Desconocido"; // Default name
                    if (userDocument.exists()) {
                        userName = userDocument.getString("nombre");
                        if (userName == null || userName.isEmpty()) {
                            userName = "Usuario Anónimo"; // Fallback if name field is empty
                        }
                    }
                    tempCollaborators.add(new Collaborator(userId, userName, postCount));
                    usersToFetch[0]--;
                    if (usersToFetch[0] == 0) {
                        // All users fetched, sort and update adapter
                        Collections.sort(tempCollaborators, (c1, c2) -> Integer.compare(c2.getPostCount(), c1.getPostCount())); 
                        
                        collaboratorList.clear();
                        // Display top 3 or all, depending on your preference. Here, displaying all sorted.
                        collaboratorList.addAll(tempCollaborators);

                        if (collaboratorList.isEmpty()) {
                            if (tvNoCollaborators != null) tvNoCollaborators.setVisibility(View.VISIBLE);
                            if (rvCollaborators != null) rvCollaborators.setVisibility(View.GONE);
                        } else {
                            if (tvNoCollaborators != null) tvNoCollaborators.setVisibility(View.GONE);
                            if (rvCollaborators != null) rvCollaborators.setVisibility(View.VISIBLE);
                            collaboratorAdapter.updateCollaborators(collaboratorList);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user name for " + userId, e);
                    // Add with default name even if fetch fails to maintain count
                    tempCollaborators.add(new Collaborator(userId, "Usuario Desconocido", postCount));
                    usersToFetch[0]--;
                    if (usersToFetch[0] == 0) {
                        // All users fetched (some might have failed), sort and update adapter
                        Collections.sort(tempCollaborators, (c1, c2) -> Integer.compare(c2.getPostCount(), c1.getPostCount()));
                        collaboratorList.clear();
                        collaboratorList.addAll(tempCollaborators);
                        if (collaboratorList.isEmpty()) {
                            if (tvNoCollaborators != null) tvNoCollaborators.setVisibility(View.VISIBLE);
                            if (rvCollaborators != null) rvCollaborators.setVisibility(View.GONE);
                        } else {
                            if (tvNoCollaborators != null) tvNoCollaborators.setVisibility(View.GONE);
                            if (rvCollaborators != null) rvCollaborators.setVisibility(View.VISIBLE);
                            collaboratorAdapter.updateCollaborators(collaboratorList);
                        }
                    }
                });
        }
    }
}

