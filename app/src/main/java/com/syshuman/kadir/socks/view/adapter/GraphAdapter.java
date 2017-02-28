package com.syshuman.kadir.socks.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.syshuman.kadir.socks.R;


public class GraphAdapter extends RecyclerView.Adapter<GraphAdapter.ViewHolder> {

    private static int ITEM = 0;
    private static int TITLE = 1;


    private Context context;

    public GraphAdapter(Context context) {
        this.context = context;
    }

    @Override
    public GraphAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.graph_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (holder.getItemViewType() == ITEM) {
            holder.staDate.setText("");
            holder.endDate.setText("");
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView staDate, endDate;

        private ViewHolder(View itemView) {
            super(itemView);
            staDate = (TextView) itemView.findViewById(R.id.eStartDate);
            endDate = (TextView) itemView.findViewById(R.id.eEndDate);
        }
    }

}

