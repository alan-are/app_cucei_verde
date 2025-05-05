package cuceiverdecom.example;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportAdapter extends ArrayAdapter<Map<String, Object>> {

    public ReportAdapter(@NonNull Context context, List<Map<String, Object>> reports) {
        super(context, R.layout.item_report, reports);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_report, parent, false);
        }

        Map<String, Object> report = getItem(position);
        if (report == null) {
            return convertView;
        }

        // Obtener referencias a las vistas
        TextView tvTitle = convertView.findViewById(R.id.tvReportTitle);
        TextView tvDescription = convertView.findViewById(R.id.tvReportDescription);
        TextView tvDate = convertView.findViewById(R.id.tvReportDate);
        TextView tvStatus = convertView.findViewById(R.id.tvReportStatus);
        ImageView ivImage = convertView.findViewById(R.id.ivReportImage);

        // Configurar los datos
        tvTitle.setText(report.get("titulo").toString());
        tvDescription.setText(report.get("descripcion").toString());

        // Formatear fecha
        long timestamp = (long) report.get("fecha");
        String dateString = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date(timestamp));
        tvDate.setText(dateString);

        // Configurar estado con color
        String status = report.get("estado").toString();
        tvStatus.setText(status);
        switch (status) {
            case "pendiente":
                tvStatus.setTextColor(getContext().getResources().getColor(android.R.color.holo_red_light));
                break;
            case "en_proceso":
                tvStatus.setTextColor(getContext().getResources().getColor(android.R.color.holo_orange_light));
                break;
            case "resuelto":
                tvStatus.setTextColor(getContext().getResources().getColor(android.R.color.holo_green_light));
                break;
        }

        // Cargar imagen si existe
        if (report.containsKey("imagenUrl")) {
            Glide.with(getContext())
                    .load(report.get("imagenUrl").toString())
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(ivImage);
            ivImage.setVisibility(View.VISIBLE);
        } else {
            ivImage.setVisibility(View.GONE);
        }

        return convertView;
    }
}