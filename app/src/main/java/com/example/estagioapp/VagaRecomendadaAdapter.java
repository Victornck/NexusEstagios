package com.example.estagioapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class VagaRecomendadaAdapter
        extends RecyclerView.Adapter<VagaRecomendadaAdapter.VagaViewHolder> {

    private final List<Vaga> vagas;
    private final Context context;

    public VagaRecomendadaAdapter(List<Vaga> vagas, Context context) {
        this.vagas   = vagas;
        this.context = context;
    }

    @NonNull
    @Override
    public VagaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Card criado por código, igual ao padrão já usado em VagasActivity
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_job_card);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dp(16));
        card.setLayoutParams(params);

        return new VagaViewHolder(card);
    }

    @Override
    public void onBindViewHolder(@NonNull VagaViewHolder holder, int position) {
        Vaga vaga = vagas.get(position);

        holder.tvEmpresa.setText(vaga.getEmpresa() != null ? vaga.getEmpresa() : "");
        holder.tvTitulo.setText(vaga.getTitulo()   != null ? vaga.getTitulo()  : "");

        // Monta info de local + modalidade
        StringBuilder info = new StringBuilder();
        if (vaga.getLocal()     != null) info.append(vaga.getLocal());
        if (vaga.getModalidade() != null) {
            if (info.length() > 0) info.append("   •   ");
            info.append(vaga.getModalidade());
        }
        if (vaga.getCarga() != null) {
            if (info.length() > 0) info.append(" · ");
            info.append(vaga.getCarga());
        }
        holder.tvInfo.setText(info.toString());

        // Verifica se já se candidatou antes de mostrar o botão
        verificarCandidatura(vaga, holder.btnCandidatura);

        holder.btnCandidatura.setOnClickListener(v -> {
            // Só permite clicar se ainda não candidatou
            if (!"candidatado".equals(holder.btnCandidatura.getTag())) {
                Intent intent = new Intent(context, CurriculoActivity.class);
                intent.putExtra("vaga_id",      vaga.getId());
                intent.putExtra("nome_vaga",    vaga.getTitulo());
                intent.putExtra("empresa_vaga", vaga.getEmpresa());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() { return vagas.size(); }

    // ── Verifica candidatura no Firebase e atualiza botão ──────────────────
    private void verificarCandidatura(Vaga vaga, Button btn) {
        String uid = FirebaseHelper.getUidAtual();
        if (uid == null || vaga.getId() == null) return;

        FirebaseHelper.refCandidaturas()
                .orderByChild("candidatoUid").equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        boolean jaCandidatou = false;
                        for (DataSnapshot c : snapshot.getChildren()) {
                            String vagaId = c.child("vagaId").getValue(String.class);
                            if (vaga.getId().equals(vagaId)) {
                                jaCandidatou = true;
                                break;
                            }
                        }
                        aplicarEstadoBotao(btn, jaCandidatou);
                    }
                    @Override public void onCancelled(DatabaseError error) {}
                });
    }

    private void aplicarEstadoBotao(Button btn, boolean jaCandidatou) {
        if (jaCandidatou) {
            btn.setText("Candidatura enviada ✓");
            btn.setBackgroundResource(R.drawable.bg_stat_card);
            btn.setTextColor(0xFF7A7A7A);
            btn.setEnabled(false);
            btn.setTag("candidatado");
        } else {
            btn.setText("Enviar currículo");
            btn.setBackgroundResource(R.drawable.bg_button_orange);
            btn.setTextColor(0xFFFFFFFF);
            btn.setEnabled(true);
            btn.setTag("disponivel");
        }
    }

    // ── ViewHolder ──────────────────────────────────────────────────────────
    static class VagaViewHolder extends RecyclerView.ViewHolder {
        final TextView tvEmpresa;
        final TextView tvTitulo;
        final TextView tvInfo;
        final Button   btnCandidatura;

        VagaViewHolder(LinearLayout card) {
            super(card);

            tvEmpresa = new TextView(card.getContext());
            tvEmpresa.setTextColor(0xFF8B8B8B);
            tvEmpresa.setTextSize(12);
            card.addView(tvEmpresa);

            tvTitulo = new TextView(card.getContext());
            tvTitulo.setTextColor(0xFF111111);
            tvTitulo.setTextSize(17);
            tvTitulo.setTypeface(null, Typeface.BOLD);
            LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            p1.topMargin = dp(card, 4);
            tvTitulo.setLayoutParams(p1);
            card.addView(tvTitulo);

            tvInfo = new TextView(card.getContext());
            tvInfo.setTextColor(0xFF8A8A8A);
            tvInfo.setTextSize(12);
            LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            p2.topMargin = dp(card, 8);
            tvInfo.setLayoutParams(p2);
            card.addView(tvInfo);

            btnCandidatura = new Button(card.getContext());
            LinearLayout.LayoutParams p3 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dp(card, 48));
            p3.topMargin = dp(card, 16);
            btnCandidatura.setLayoutParams(p3);
            btnCandidatura.setAllCaps(false);
            btnCandidatura.setTypeface(null, Typeface.BOLD);
            card.addView(btnCandidatura);
        }

        private static int dp(View v, int value) {
            return Math.round(value * v.getContext()
                    .getResources().getDisplayMetrics().density);
        }
    }

    private int dp(int value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }
}