package com.example.fotoappjava;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fotoappjava.databinding.RecyclerRowBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.PostHolder> {
    ArrayList<Post> arrayList;

    public RecyclerAdapter(ArrayList<Post> postArrayList){
        this.arrayList=postArrayList;

    }

    class PostHolder extends RecyclerView.ViewHolder{
        RecyclerRowBinding row;
        public PostHolder(RecyclerRowBinding roww) {
            super(roww.getRoot());
            this.row=roww;
        }
    }
    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding rowBinding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PostHolder(rowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.row.recyclerEmailText.setText(arrayList.get(position).email);
        holder.row.recyclerCommentText.setText(arrayList.get(position).comment);
        Picasso.get().load(arrayList.get(position).downloadUrl).into(holder.row.recyclerImageView);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
