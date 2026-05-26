package com.example.estagioapp;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

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

        holder.tvDescricao.setText(
                atividade.getDescricao()
        );

        holder.tvData.setText(
                atividade.getData()
        );

        holder.tvHorasItem.setText(
                atividade.getHoras() + "h"
        );

        holder.checkConcluida.setOnCheckedChangeListener(null);

        holder.checkConcluida.setChecked(
                atividade.isConcluida()
        );

        if (atividade.isConcluida()) {

            holder.tvTitulo.setPaintFlags(
                    holder.tvTitulo.getPaintFlags()
                            | Paint.STRIKE_THRU_TEXT_FLAG
            );

            holder.tvStatus.setText(
                    "Concluída"
            );

            holder.tvStatus.setBackgroundColor(
                    Color.parseColor("#4CAF50")
            );

        } else {

            holder.tvTitulo.setPaintFlags(
                    holder.tvTitulo.getPaintFlags()
                            & (~Paint.STRIKE_THRU_TEXT_FLAG)
            );

            holder.tvStatus.setText(
                    "Pendente"
            );

            holder.tvStatus.setBackgroundColor(
                    Color.parseColor("#FF6B00")
            );
        }

        holder.checkConcluida.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    int horasAtual =
                            Integer.parseInt(
                                    AppData.getHorasConcluidas(
                                            holder.itemView.getContext()
                                    )
                            );

                    int horasTotal =
                            Integer.parseInt(
                                    AppData.getHorasTotal(
                                            holder.itemView.getContext()
                                    )
                            );

                    // VERIFICA LIMITE

                    if (isChecked) {

                        int novaCarga =
                                horasAtual
                                        + atividade.getHoras();

                        if (novaCarga > horasTotal) {

                            Toast.makeText(
                                    holder.itemView.getContext(),
                                    "Carga horária máxima atingida",
                                    Toast.LENGTH_SHORT
                            ).show();

                            holder.checkConcluida.setChecked(false);

                            return;
                        }
                    }

                    atividadesRef
                            .child(atividade.getId())
                            .child("concluida")
                            .setValue(isChecked);
                });

        // DELETAR AO TOCAR

        holder.itemView.setOnClickListener(v -> {

            new android.app.AlertDialog.Builder(
                    holder.itemView.getContext()
            )
                    .setTitle("Excluir atividade")
                    .setMessage(
                            "Deseja apagar esta atividade?"
                    )
                    .setPositiveButton(
                            "Excluir",
                            (dialog, which) -> {

                                atividadesRef
                                        .child(atividade.getId())
                                        .removeValue();

                                Toast.makeText(
                                        holder.itemView.getContext(),
                                        "Atividade apagada",
                                        Toast.LENGTH_SHORT
                                ).show();
                            })
                    .setNegativeButton(
                            "Cancelar",
                            null
                    )
                    .show();
        });
    }

    @Override
    public int getItemCount() {

        return lista.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        CheckBox checkConcluida;

        TextView tvTitulo;
        TextView tvDescricao;
        TextView tvData;
        TextView tvHorasItem;
        TextView tvStatus;

        public ViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            checkConcluida =
                    itemView.findViewById(
                            R.id.check_concluida
                    );

            tvTitulo =
                    itemView.findViewById(
                            R.id.tv_titulo
                    );

            tvDescricao =
                    itemView.findViewById(
                            R.id.tv_descricao
                    );

            tvData =
                    itemView.findViewById(
                            R.id.tv_data
                    );

            tvHorasItem =
                    itemView.findViewById(
                            R.id.tv_horas_item
                    );

            tvStatus =
                    itemView.findViewById(
                            R.id.tv_status
                    );
        }
    }
}