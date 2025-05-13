package cuceiverdecom.example;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.WindowCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class menu_principal extends AppCompatActivity {

    private Button log_out_btn;
    private ImageButton btnCreateReport, btnViewReports;
    private GoogleSignInClient googleSignInClient; // Solo si usaste Google Sign-In
    private ImageView chatIcon;
    private CardView headerCard, optionsCard1, optionsCard2, chatCard;
    private LinearLayout animationContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.menu_principal);

        // Inicialización de las animaciones
        initializeViews();
        applyAnimations();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);        btnCreateReport = findViewById(R.id.btnCreateReport);
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
        }        log_out_btn = findViewById(R.id.log_out_btn);
        if (log_out_btn != null) {
            log_out_btn.setOnClickListener(v -> {
                animateButtonClick(v);
                new android.os.Handler().postDelayed(this::logoutUser, 300);
            });
        }
        
        chatIcon = findViewById(R.id.imageView);
        if (chatIcon != null) {
            chatIcon.setOnClickListener(v -> {
                // Aplicar animación al ícono del chat
                Animation pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse);
                v.startAnimation(pulseAnimation);
                
                Toast.makeText(this, "Iniciando chat...", Toast.LENGTH_SHORT).show();
                // Implementar la funcionalidad del chat
            });
        }
    }    private void logoutUser() {
        // 1. Cierra sesión en Firebase
        FirebaseAuth.getInstance().signOut();

        // 2. Cierra sesión en Google (si aplica)
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            // 3. Limpia datos locales (opcional)
            getSharedPreferences("MisPreferencias", MODE_PRIVATE).edit().clear().apply();

            // 4. Redirige al login
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, cuceiverdecom.example.mainActivity.class));
            finish();
        });
    }      private void initializeViews() {
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
                      
            try {
                View chatIcon = findViewById(R.id.imageView);
                if (chatIcon != null) {
                    View parent = (View) chatIcon.getParent();
                    if (parent != null && parent.getParent() instanceof CardView) {
                        chatCard = (CardView) parent.getParent();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            try {
                animationContainer = findViewById(R.id.animationContainer);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        } catch (Exception e) {
            // Log any issues but don't crash
            e.printStackTrace();
            Toast.makeText(this, "Se produjo un error al inicializar la vista", Toast.LENGTH_SHORT).show();
        }
    }      private void applyAnimations() {
        try {
            // Load animations
            Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            fadeIn.setDuration(1000);
            
            Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up_fade_in);
            Animation scaleIn = AnimationUtils.loadAnimation(this, R.anim.scale_fade_in);
            Animation slideLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
            
            // Apply animations with delays - only if views are not null
            if (headerCard != null) {
                headerCard.startAnimation(fadeIn);
            }
            
            // Use a handler for delayed animations, but check for null before applying
            if (optionsCard1 != null) {
                new android.os.Handler().postDelayed(() -> {
                    // Second null check in case the view became null during the delay
                    if (optionsCard1 != null && optionsCard1.getWindowToken() != null) {
                        optionsCard1.startAnimation(slideLeft);
                    }
                }, 300);
            }
            
            if (optionsCard2 != null) {
                new android.os.Handler().postDelayed(() -> {
                    // Second null check in case the view became null during the delay
                    if (optionsCard2 != null && optionsCard2.getWindowToken() != null) {
                        optionsCard2.startAnimation(slideLeft);
                    }
                }, 500);
            }
            
            if (chatCard != null) {
                new android.os.Handler().postDelayed(() -> {
                    // Second null check in case the view became null during the delay  
                    if (chatCard != null && chatCard.getWindowToken() != null) {
                        chatCard.startAnimation(slideUp);
                    }
                }, 700);
            }
        } catch (Exception e) {
            // Log any issues but don't crash
            e.printStackTrace();
            // Don't show toast here since this is called from onCreate
        }
    }
    
    private void animateButtonClick(View view) {
        Animation scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        view.startAnimation(scaleDown);
    }
}

