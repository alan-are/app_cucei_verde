package cuceiverdecom.example;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class reportCreation extends AppCompatActivity {

    private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_report_creation);

        backBtn = findViewById(R.id.backButtonReportCreation);

        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(reportCreation.this, com.example.resea_profesores.menu_principal.class);
            startActivity(intent);
            finish();
        });

    }


}
