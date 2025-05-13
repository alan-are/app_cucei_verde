package cuceiverdecom.example;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private List<Report> reportList;
    private Context context;
    private OnReportClickListener listener;

    // Interfaz para manejar los clics en los items
    public interface OnReportClickListener {
        void onReportClick(Report report, int position);
    }

    public ReportAdapter(Context context, List<Report> reportList, OnReportClickListener listener) {
        this.context = context;
        this.reportList = reportList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reportList.get(position);
        
        // Set title with null check
        holder.titleTextView.setText(report.getTitle() != null ? report.getTitle() : "Sin título");
        
        // Set description with null check
        holder.descriptionTextView.setText(report.getDescription() != null ? report.getDescription() : "Sin descripción");
        
        // Formatear la fecha
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
        
        // Configurar el evento de clic
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

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tvReportTitle);
            dateTextView = itemView.findViewById(R.id.tvReportDate);
            descriptionTextView = itemView.findViewById(R.id.tvReportDescription);
        }
    }
}
