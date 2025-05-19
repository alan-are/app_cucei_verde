package cuceiverdecom.example;

import android.content.Context;
import android.graphics.Typeface; // Required for text style
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan; // Required for bold
import android.util.Log; // Added for logging
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; 
import android.widget.LinearLayout; 
import android.widget.TextView;
import android.widget.Toast; 

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth; 
import com.google.firebase.auth.FirebaseUser; 
import com.google.firebase.firestore.FirebaseFirestore; 

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap; // Added
import java.util.List;
import java.util.Locale;
import java.util.Map; // Added

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private List<Report> reportList;
    private Context context;
    private OnReportClickListener listener;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentUserId; // Changed from currentUserEmail to currentUserId for voting logic
    private String currentUserEmail; // Keep for admin check
    private static final String TAG = "ReportAdapter"; // Added for logging

    // Interfaz para manejar los clics en los items
    public interface OnReportClickListener {
        void onReportClick(Report report, int position);
    }

    public ReportAdapter(Context context, List<Report> reportList, OnReportClickListener listener) {
        this.context = context;
        this.reportList = reportList;
        this.listener = listener;
        this.db = FirebaseFirestore.getInstance(); 
        this.mAuth = FirebaseAuth.getInstance(); 
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            this.currentUserId = currentUser.getUid(); // Get UID
            this.currentUserEmail = currentUser.getEmail(); // Get Email
        }
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reportList.get(position);
        
        holder.titleTextView.setText(report.getTitle() != null ? report.getTitle() : "Sin título");
        holder.descriptionTextView.setText(report.getDescription() != null ? report.getDescription() : "Sin descripción");
        holder.categoryTextView.setText(report.getCategory() != null ? "Categoría: " + report.getCategory() : "Categoría: No especificada");

        if (report.getCreationDate() != null) {
            try {
                Timestamp timestamp = report.getCreationDate();
                Date date = timestamp.toDate();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = sdf.format(date);
                holder.dateTextView.setText("Reportado el " + formattedDate);
            } catch (Exception e) {
                holder.dateTextView.setText("Fecha no disponible");
            }
        } else {
            holder.dateTextView.setText("Fecha no disponible");
        }
        
        // Update status text based on votes and apply styling
        updateReportStatusText(holder, report);

        // Display vote counts
        holder.tvSolvedVotes.setText("Resuelto: " + report.getSolvedVotesCount());
        holder.tvUnsolvedVotes.setText("No Resuelto: " + report.getUnsolvedVotesCount());

        // Display location text
        String locationText = report.getLocation();
        if (locationText != null && !locationText.trim().isEmpty()) {
            holder.tvReportLocationLabel.setVisibility(View.VISIBLE);
            holder.tvReportLocation.setVisibility(View.VISIBLE);
            holder.tvReportLocation.setText(locationText);
        } else {
            holder.tvReportLocationLabel.setVisibility(View.GONE);
            holder.tvReportLocation.setVisibility(View.GONE);
            // Optionally, set a default text if it's gone but you still want to show the label
            // holder.tvReportLocation.setText("Ubicación no especificada"); 
        }

        if (isAdminUser()) {
            holder.userActionLayout.setVisibility(View.GONE);
            holder.adminDeleteButton.setVisibility(View.VISIBLE);
        } else {
            holder.userActionLayout.setVisibility(View.VISIBLE);
            holder.adminDeleteButton.setVisibility(View.GONE);
        }

        holder.problemSolvedButton.setOnClickListener(v -> handleVote(report, "solved", position));
        holder.problemNotSolvedButton.setOnClickListener(v -> handleVote(report, "unsolved", position));
        holder.adminDeleteButton.setOnClickListener(v -> deleteReport(report, position));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReportClick(report, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public void updateReports(List<Report> newReports) {
        this.reportList = newReports;
        notifyDataSetChanged();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView dateTextView;
        TextView descriptionTextView;
        TextView categoryTextView;
        TextView statusTextView; 
        TextView tvReportLocationLabel; // Added for location label
        TextView tvReportLocation;      // Added for location text
        TextView tvSolvedVotes;         // Added for solved votes count
        TextView tvUnsolvedVotes;       // Added for unsolved votes count
        LinearLayout userActionLayout; 
        Button problemSolvedButton; 
        Button problemNotSolvedButton; 
        Button adminDeleteButton; 

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tvReportTitle);
            dateTextView = itemView.findViewById(R.id.tvReportDate);
            descriptionTextView = itemView.findViewById(R.id.tvReportDescription);
            categoryTextView = itemView.findViewById(R.id.tvReportCategory);
            statusTextView = itemView.findViewById(R.id.tvReportStatus); 
            tvReportLocationLabel = itemView.findViewById(R.id.tvReportLocationLabel); // Initialize location label
            tvReportLocation = itemView.findViewById(R.id.tvReportLocation);          // Initialize location text
            tvSolvedVotes = itemView.findViewById(R.id.tvSolvedVotes);               // Initialize solved votes TextView
            tvUnsolvedVotes = itemView.findViewById(R.id.tvUnsolvedVotes);           // Initialize unsolved votes TextView
            userActionLayout = itemView.findViewById(R.id.userActionLayout); 
            problemSolvedButton = itemView.findViewById(R.id.btnProblemSolved); 
            problemNotSolvedButton = itemView.findViewById(R.id.btnProblemNotSolved); 
            adminDeleteButton = itemView.findViewById(R.id.btnAdminDeleteReport); 
        }
    }

    private boolean isAdminUser() {
        return currentUserEmail != null && "abraham.torres6647@alumnos.udg.mx".equals(currentUserEmail);
    }

    private void handleVote(Report report, String voteType, int position) {
        if (currentUserId == null) {
            Toast.makeText(context, "Error: Usuario no identificado.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> userVotes = report.getUserVotes(); // Ensure this is initialized in Report class
        if (userVotes == null) {
            userVotes = new HashMap<>();
            report.setUserVotes(userVotes); // Should not happen if Report constructor initializes it
        }
        String previousVote = userVotes.get(currentUserId);

        int solvedVotes = report.getSolvedVotesCount();
        int unsolvedVotes = report.getUnsolvedVotesCount();

        // If user has voted before, revert their previous vote's effect
        if (previousVote != null) {
            if (previousVote.equals("solved")) {
                solvedVotes = Math.max(0, solvedVotes - 1);
            } else if (previousVote.equals("unsolved")) {
                unsolvedVotes = Math.max(0, unsolvedVotes - 1);
            }
        }

        // Apply the new vote
        if (voteType.equals("solved")) {
            solvedVotes++;
            userVotes.put(currentUserId, "solved");
        } else if (voteType.equals("unsolved")) {
            unsolvedVotes++;
            userVotes.put(currentUserId, "unsolved");
        }

        // Check for deletion condition (combined votes >= 10 and solved > unsolved)
        // This deletion logic might need further refinement based on exact requirements.
        // For now, let's assume the old logic: if total votes (solved - unsolved, effectively) reaches a threshold.
        // The original request was: "If the counter reaches 10, the report is deleted".
        // Let's interpret this as: if (solvedVotes - unsolvedVotes) >= 10, delete.
        // Or, if it's a simple majority with a high confidence (e.g. 10 more solved than unsolved)
        // For now, we'll use the difference, and it must be positive (solved > unsolved)

        int effectiveVoteDifference = solvedVotes - unsolvedVotes;

        if (effectiveVoteDifference >= 10) { // Condition for deletion
            deleteReport(report, position);
        } else {
            report.setSolvedVotesCount(solvedVotes);
            report.setUnsolvedVotesCount(unsolvedVotes);
            report.setUserVotes(userVotes);

            Map<String, Object> updates = new HashMap<>();
            updates.put("solvedVotesCount", solvedVotes);
            updates.put("unsolvedVotesCount", unsolvedVotes);
            updates.put("userVotes", userVotes);
            
            // Update status based on new vote counts
            String newStatus;
            if (solvedVotes > unsolvedVotes) {
                newStatus = "Resuelto";
            } else if (unsolvedVotes > solvedVotes) {
                newStatus = "No resuelto";
            } else {
                newStatus = "Pendiente"; // Or original status if no majority
            }
            updates.put("status", newStatus);
            report.setStatus(newStatus);

            db.collection("reportes").document(report.getId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    notifyItemChanged(position); // This will trigger onBindViewHolder again
                    Toast.makeText(context, "Voto registrado", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al registrar voto", e);
                    Toast.makeText(context, "Error al registrar voto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Revert local changes if Firestore update fails? (Consider this for robustness)
                });
        }
    }

    private void updateReportStatusText(ReportViewHolder holder, Report report) {
        String statusText;
        int statusColor;
        String baseStatus = "Estado: ";

        if (report.getSolvedVotesCount() > report.getUnsolvedVotesCount()) {
            statusText = baseStatus + "Resuelto";
            statusColor = ContextCompat.getColor(context, R.color.green_primary);
        } else if (report.getUnsolvedVotesCount() > report.getSolvedVotesCount()) {
            statusText = baseStatus + "No resuelto";
            statusColor = ContextCompat.getColor(context, R.color.accent_orange);
        } else {
            String currentStatus = report.getStatus();
            if (currentStatus != null && !currentStatus.isEmpty() && !currentStatus.equalsIgnoreCase("Resuelto") && !currentStatus.equalsIgnoreCase("No resuelto")) {
                statusText = baseStatus + currentStatus; // e.g., "Pendiente", "En progreso"
            } else {
                statusText = baseStatus + "Pendiente"; // Default
            }
            statusColor = ContextCompat.getColor(context, R.color.gray_dark); // Default color for Pendiente or other statuses
            // Specific color for "Pendiente"
            if (statusText.endsWith("Pendiente")) {
                 statusColor = ContextCompat.getColor(context, R.color.accent_blue); // Assuming you add accent_blue to colors.xml
            }
        }

        SpannableStringBuilder ssb = new SpannableStringBuilder(statusText);
        // Apply bold to the whole string
        ssb.setSpan(new StyleSpan(Typeface.BOLD), 0, statusText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // Apply color to the whole string
        ssb.setSpan(new ForegroundColorSpan(statusColor), 0, statusText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        holder.statusTextView.setText(ssb);
    }

    private void deleteReport(Report report, int position) {
        db.collection("reportes").document(report.getId())
            .delete()
            .addOnSuccessListener(aVoid -> {
                // Ensure the position is still valid before removing
                if (position < reportList.size() && reportList.get(position).getId().equals(report.getId())) {
                    reportList.remove(position);
                    notifyItemRemoved(position);
                    // notifyItemRangeChanged(position, reportList.size()); // This might be problematic if list becomes empty
                    if (reportList.isEmpty()) {
                        notifyDataSetChanged(); // Refresh if list is empty
                    } else {
                        notifyItemRangeChanged(position, reportList.size() - position);
                    }
                }
                Toast.makeText(context, "Reporte eliminado", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error al eliminar reporte", e);
                Toast.makeText(context, "Error al eliminar reporte: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
}
