package com.example.estagioapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class VagasActivity extends AppCompatActivity {

    private LinearLayout containerVagas;
    private TextView tvSemVagas;
    private TextView tvTotalVagas;

    // Chips
    private TextView chipTodas;
    private TextView chipTech;
    private TextView chipDesign;
    private TextView chipMarketing;
    private TextView chipFinancas;

    // Vagas em memória
    private final List<VagaItem> todasVagas = new ArrayList<>();
    private String filtroAtual = "Todas";

    // Modelo simples de vaga
    static class VagaItem {
        String id, titulo, empresa, local, modalidade, carga, area;
        VagaItem(String id, String titulo, String empresa,
                 String local, String modalidade, String carga, String area) {
            this.id = id; this.titulo = titulo; this.empresa = empresa;
            this.local = local; this.modalidade = modalidade;
            this.carga = carga; this.area = area;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vagas);

        containerVagas = findViewById(R.id.container_vagas);
        tvSemVagas     = findViewById(R.id.tv_sem_vagas);
        tvTotalVagas   = findViewById(R.id.tv_total_vagas);

        chipTodas     = findViewById(R.id.chip_todas);
        chipTech      = findViewById(R.id.chip_tech);
        chipDesign    = findViewById(R.id.chip_design);
        chipMarketing = findViewById(R.id.chip_marketing);
        chipFinancas  = findViewById(R.id.chip_financas);

        configurarChips();
        carregarVagas();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavHelper.navigate(VagasActivity.this, MainActivity.class);
            }
        });

        findViewById(R.id.nav_inicio).setOnClickListener(v ->
                NavHelper.navigate(this, MainActivity.class));
        findViewById(R.id.nav_vagas).setOnClickListener(v -> {});
        findViewById(R.id.nav_atividades).setOnClickListener(v ->
                NavHelper.navigate(this, AtividadesActivity.class));
        findViewById(R.id.nav_perfil).setOnClickListener(v ->
                NavHelper.navigate(this, PerfilActivity.class));
    }

    // ───── CHIPS ─────

    private void configurarChips() {
        chipTodas.setOnClickListener(v     -> selecionarChip("Todas"));
        chipTech.setOnClickListener(v      -> selecionarChip("Tech"));
        chipDesign.setOnClickListener(v    -> selecionarChip("Design"));
        chipMarketing.setOnClickListener(v -> selecionarChip("Marketing"));
        chipFinancas.setOnClickListener(v  -> selecionarChip("Finanças"));
    }

    private void selecionarChip(String filtro) {
        filtroAtual = filtro;

        // Reseta visual de todos
        int corInativa = 0xFF444444;
        chipTodas.setBackgroundResource(R.drawable.bg_chip_inactive);
        chipTodas.setTextColor(corInativa);
        chipTech.setBackgroundResource(R.drawable.bg_chip_inactive);
        chipTech.setTextColor(corInativa);
        chipDesign.setBackgroundResource(R.drawable.bg_chip_inactive);
        chipDesign.setTextColor(corInativa);
        chipMarketing.setBackgroundResource(R.drawable.bg_chip_inactive);
        chipMarketing.setTextColor(corInativa);
        chipFinancas.setBackgroundResource(R.drawable.bg_chip_inactive);
        chipFinancas.setTextColor(corInativa);

        // Ativa o selecionado
        TextView chipAtivo = chipTodas;
        if ("Tech".equals(filtro))      chipAtivo = chipTech;
        else if ("Design".equals(filtro))    chipAtivo = chipDesign;
        else if ("Marketing".equals(filtro)) chipAtivo = chipMarketing;
        else if ("Finanças".equals(filtro))  chipAtivo = chipFinancas;

        chipAtivo.setBackgroundResource(R.drawable.bg_chip_active);
        chipAtivo.setTextColor(0xFFFFFFFF);

        // Aplica filtro
        aplicarFiltro();
    }

    private void aplicarFiltro() {
        containerVagas.removeAllViews();

        List<VagaItem> filtradas = new ArrayList<>();

        for (VagaItem vaga : todasVagas) {
            if ("Todas".equals(filtroAtual)) {
                filtradas.add(vaga);
            } else {
                // Filtra por campo "area" salvo no Firebase,
                // ou faz fallback buscando no título
                boolean bateArea = filtroAtual.equalsIgnoreCase(vaga.area);
                boolean bateTitulo = vaga.titulo != null &&
                        vaga.titulo.toLowerCase().contains(filtroAtual.toLowerCase());
                if (bateArea || bateTitulo) filtradas.add(vaga);
            }
        }

        if (filtradas.isEmpty()) {
            tvSemVagas.setVisibility(View.VISIBLE);
            tvSemVagas.setText("Nenhuma vaga em " + filtroAtual + ".");
        } else {
            tvSemVagas.setVisibility(View.GONE);
            for (VagaItem vaga : filtradas) {
                adicionarCardVaga(vaga.id, vaga.titulo, vaga.empresa,
                        vaga.local, vaga.modalidade, vaga.carga);
            }
        }

        // Atualiza contador com o total filtrado
        int total = filtradas.size();
        tvTotalVagas.setText(total + (total == 1
                ? " oportunidade disponível"
                : " oportunidades disponíveis"));
    }

    // ───── FIREBASE ─────

    private void carregarVagas() {
        FirebaseHelper.refVagas()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        todasVagas.clear();

                        if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                            tvSemVagas.setVisibility(View.VISIBLE);
                            tvTotalVagas.setText("0 oportunidades disponíveis");
                            return;
                        }

                        for (DataSnapshot vagaSnap : snapshot.getChildren()) {
                            String id         = vagaSnap.getKey();
                            String titulo     = vagaSnap.child("titulo").getValue(String.class);
                            String empresa    = vagaSnap.child("empresa").getValue(String.class);
                            String local      = vagaSnap.child("local").getValue(String.class);
                            String modalidade = vagaSnap.child("modalidade").getValue(String.class);
                            String carga      = vagaSnap.child("carga").getValue(String.class);
                            String status     = vagaSnap.child("status").getValue(String.class);
                            String area       = vagaSnap.child("area").getValue(String.class);

                            if (titulo == null || empresa == null) continue;
                            if (!"ativa".equals(status)) continue;

                            todasVagas.add(new VagaItem(
                                    id, titulo, empresa, local, modalidade, carga, area));
                        }

                        // Renderiza com filtro atual (padrão: Todas)
                        aplicarFiltro();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        tvSemVagas.setVisibility(View.VISIBLE);
                        tvSemVagas.setText("Erro ao carregar vagas.");
                        tvTotalVagas.setText("— oportunidades disponíveis");
                    }
                });
    }

    // ───── CARD (inalterado) ─────

    private void adicionarCardVaga(String id, String titulo, String empresa,
                                   String local, String modalidade, String carga) {
        LinearLayout card = new LinearLayout(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(dp(16), dp(6), dp(16), dp(6));
        card.setLayoutParams(cardParams);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_card);
        card.setElevation(dp(2));
        card.setPadding(dp(16), dp(16), dp(16), dp(16));

        TextView tvEmpresa = new TextView(this);
        tvEmpresa.setText(empresa);
        tvEmpresa.setTextColor(0xFF888888);
        tvEmpresa.setTextSize(13);
        card.addView(tvEmpresa);

        TextView tvTitulo = new TextView(this);
        tvTitulo.setText(titulo);
        tvTitulo.setTextColor(0xFF1A1A1A);
        tvTitulo.setTextSize(15);
        tvTitulo.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams tituloParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tituloParams.topMargin = dp(4);
        tvTitulo.setLayoutParams(tituloParams);
        card.addView(tvTitulo);

        TextView tvInfo = new TextView(this);
        String info = "";
        if (local != null)      info += "📍 " + local;
        if (modalidade != null) info += "     🕐 " + modalidade;
        if (carga != null)      info += " · " + carga;
        tvInfo.setText(info);
        tvInfo.setTextColor(0xFF888888);
        tvInfo.setTextSize(12);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        infoParams.topMargin = dp(6);
        tvInfo.setLayoutParams(infoParams);
        card.addView(tvInfo);

        Button btnCandidatura = new Button(this);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(46));
        btnParams.topMargin = dp(14);
        btnCandidatura.setLayoutParams(btnParams);
        btnCandidatura.setBackgroundResource(R.drawable.bg_button_orange);
        btnCandidatura.setText("Enviar currículo");
        btnCandidatura.setTextColor(0xFFFFFFFF);
        btnCandidatura.setAllCaps(false);
        btnCandidatura.setTypeface(null, android.graphics.Typeface.BOLD);
        btnCandidatura.setOnClickListener(v -> abrirCandidatura(id, titulo, empresa));
        card.addView(btnCandidatura);

        containerVagas.addView(card);
    }

    private void abrirCandidatura(String vagaId, String nomeVaga, String empresa) {
        Intent intent = new Intent(this, CurriculoActivity.class);
        intent.putExtra("vaga_id", vagaId);
        intent.putExtra("nome_vaga", nomeVaga);
        intent.putExtra("empresa_vaga", empresa);
        startActivity(intent);
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(value * density);
    }
}