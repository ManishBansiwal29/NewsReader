package com.destr0yer29.newsreader;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private ArrayList<NewsItem> newsItem = new ArrayList<>();
    private Context context;

    public NewsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent , int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder , final int position) {
        holder.textTitle.setText(newsItem.get(position).getTitle());
        holder.txtContent.setText(newsItem.get(position).getDescription());
        holder.txtDate.setText(newsItem.get(position).getDate());
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,WebsiteActivity.class);
                intent.putExtra("url",newsItem.get(position).getLink());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsItem.size();
    }

    public void setNewsItem(ArrayList<NewsItem> newsItem) {
        this.newsItem = newsItem;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textTitle,txtContent,txtDate;
        private CardView parent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle=itemView.findViewById(R.id.textTitle);
            txtContent=itemView.findViewById(R.id.txtContent);
            txtDate=itemView.findViewById(R.id.textDate);
            parent=itemView.findViewById(R.id.parent);
        }
    }
}
