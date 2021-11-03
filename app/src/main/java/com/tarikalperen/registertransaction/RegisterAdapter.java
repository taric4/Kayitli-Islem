package com.tarikalperen.registertransaction;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tarikalperen.registertransaction.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class RegisterAdapter extends RecyclerView.Adapter<RegisterAdapter.RegisterHolder> {

    ArrayList<CorporateClass> corporateClassArrayList;

    public RegisterAdapter(ArrayList<CorporateClass> corporateClassArrayList) {
        this.corporateClassArrayList = corporateClassArrayList;
    }

    @NonNull
    @Override
    public RegisterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new RegisterHolder(recyclerRowBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull RegisterAdapter.RegisterHolder holder, int position) {
        holder.binding.recylerViewTextView.setText(corporateClassArrayList.get(position).corporate);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(),DetailsActivity.class);
                intent.putExtra("RegisterId",corporateClassArrayList.get(position).id);
                intent.putExtra("info","old");
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return corporateClassArrayList.size();
    }

    public class RegisterHolder extends RecyclerView.ViewHolder{

        private RecyclerRowBinding binding;

        public RegisterHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }
}
