package jam.rain.com.kidrewards.ui.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import jam.rain.com.kidrewards.R;

public class ChildCardViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.name)
    TextView nameTextView;

    public ChildCardViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(itemView);
    }

    public TextView getNameTextView() {
        return nameTextView;
    }
}
