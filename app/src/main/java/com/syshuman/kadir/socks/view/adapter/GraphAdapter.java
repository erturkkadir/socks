package com.syshuman.kadir.socks.view.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.syshuman.kadir.socks.R;


public class GraphAdapter extends RecyclerView.Adapter<GraphAdapter.ViewHolder> {

    private static int ITEM = 0;
    private static int TITLE = 1;
    private LineDataSet dataset;


    private Context context;

    public GraphAdapter(Context context, LineDataSet dataset) {
        this.context = context;
        this.dataset = dataset;
    }

    @Override
    public GraphAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.graph_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.staDate.setText("Start");
        holder.endDate.setText("End");
        LineData data = new LineData(dataset);
        holder.chart.setData(data);
        holder.chart.invalidate();


    }


    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return 2;
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView staDate, endDate;
        LineChart chart;

        private ViewHolder(View itemView) {
            super(itemView);
            staDate = (TextView) itemView.findViewById(R.id.eStartDate);
            endDate = (TextView) itemView.findViewById(R.id.eEndDate);
            chart = (LineChart) itemView.findViewById(R.id.chart);
            chart.notifyDataSetChanged();
            chart.invalidate();

        }
    }

}

