package jam.rain.com.kidrewards.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import jam.rain.com.kidrewards.R;
import jam.rain.com.kidrewards.dagger.Injector;
import jam.rain.com.kidrewards.domain.Child;
import jam.rain.com.kidrewards.ui.adapter.viewholder.ChildCardViewHolder;

public class ChildListAdapter extends RecyclerView.Adapter<ChildCardViewHolder> {

    private List<Child> childList;

    public ChildListAdapter() {
        Injector.get().inject(this);
        childList = new ArrayList<>();
    }

    @Override
    public ChildCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child, parent, false);
        ChildCardViewHolder viewHolder = new ChildCardViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChildCardViewHolder holder, int position) {
        Child child = childList.get(position);
        holder.getNameTextView().setText(child.getName());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void setChildList(List<Child> childList) {
        this.childList = childList;
        notifyDataSetChanged();
    }

    public void addChild(Child child) {
        childList.add(child);
    }
}
