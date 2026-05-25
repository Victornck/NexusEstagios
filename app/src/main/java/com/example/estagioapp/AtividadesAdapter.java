package com.example.estagioapp;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class AtividadesAdapter
        extends RecyclerView.Adapter<AtividadesAdapter.ViewHolder> {

    private final List<Atividade> lista;
    private final DatabaseReference atividadesRef;

    public AtividadesAdapter(
            List<Atividade> lista,
            DatabaseReference atividadesRef
    ) {

        this.lista = lista;
        this.atividadesRef = atividadesRef;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_atividade, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        Atividade atividade = lista.get(position);

        holder.tvTitulo.setText(
                atividade.getTitulo()
        );

        holder.tvData.setText(
                atividade.getData()
        );

        holder.checkConcluida.setChecked(
                atividade.isConcluida()
        );

        if (atividade.isConcluida()) {

            holder.tvTitulo.setPaintFlags(
                    holder.tvTitulo.getPaintFlags()
                            | Paint.STRIKE_THRU_TEXT_FLAG
            );

        } else {

            holder.tvTitulo.setPaintFlags(0);
        }

        holder.checkConcluida.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    atividadesRef
                            .child(atividade.getId())
                            .child("concluida")
                            .setValue(isChecked);
                });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkConcluida;
        TextView tvTitulo;
        TextView tvData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            checkConcluida =
                    itemView.findViewById(R.id.check_concluida);

            tvTitulo =
                    itemView.findViewById(R.id.tv_titulo);

            tvData =
                    itemView.findViewById(R.id.tv_data);
        }
    }
}