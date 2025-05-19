package cuceiverdecom.example;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class CollaboratorAdapter extends RecyclerView.Adapter<CollaboratorAdapter.CollaboratorViewHolder> {

    private List<Collaborator> collaboratorList;
    private Context context;

    public CollaboratorAdapter(Context context, List<Collaborator> collaboratorList) {
        this.context = context;
        this.collaboratorList = collaboratorList;
    }

    @NonNull
    @Override
    public CollaboratorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_collaborator, parent, false);
        return new CollaboratorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollaboratorViewHolder holder, int position) {
        Collaborator collaborator = collaboratorList.get(position);
        holder.tvCollaboratorName.setText(collaborator.getUserName());
        holder.tvPostCount.setText(String.format(Locale.getDefault(), "%d Posts", collaborator.getPostCount()));
    }

    @Override
    public int getItemCount() {
        return collaboratorList.size();
    }

    public void updateCollaborators(List<Collaborator> newCollaborators) {
        this.collaboratorList = newCollaborators;
        notifyDataSetChanged();
    }

    static class CollaboratorViewHolder extends RecyclerView.ViewHolder {
        TextView tvCollaboratorName;
        TextView tvPostCount;

        public CollaboratorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCollaboratorName = itemView.findViewById(R.id.tvCollaboratorName);
            tvPostCount = itemView.findViewById(R.id.tvPostCount);
        }
    }
}
