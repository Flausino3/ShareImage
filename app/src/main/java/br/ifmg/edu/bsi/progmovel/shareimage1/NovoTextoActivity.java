package br.ifmg.edu.bsi.progmovel.shareimage1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class NovoTextoActivity extends AppCompatActivity {

    public static String EXTRA_TEXTO_ATUAL = "br.ifmg.edu.bsi.progmovel.shareimage1.texto_atual";
    public static String EXTRA_COR_ATUAL = "br.ifmg.edu.bsi.progmovel.shareimage1.cor_atual";
    public static String EXTRA_TAMANHO_ATUAL = "br.ifmg.edu.bsi.progmovel.shareimage1.tamanho_atual";
    public static String EXTRA_NOVO_TEXTO = "br.ifmg.edu.bsi.progmovel.shareimage1.novo_texto";
    public static String EXTRA_NOVA_COR = "br.ifmg.edu.bsi.progmovel.shareimage1.nova_cor";
    public static String EXTRA_NOVO_TAMANHO = "br.ifmg.edu.bsi.progmovel.shareimage1.novo_tamanho";
    public static final String EXTRA_TEXTO_TOPO_ATUAL = "textoTopoAtual";
    public static final String EXTRA_COR_TOPO_ATUAL = "corTopoAtual";
    public static final String EXTRA_TAMANHO_TOPO_ATUAL = "tamanhoTopoAtual";
    public static final String EXTRA_NOVO_TEXTO_TOPO = "novoTextoTopo";
    public static final String EXTRA_NOVA_COR_TOPO = "novaCorTopo";
    public static final String EXTRA_NOVO_TAMANHO_TOPO = "novoTamanhoTopo";
    public static final String EXTRA_POSICAO_ESCOLHIDA = "posicaoEscolhida";

    private EditText etTexto;
    private Spinner spinnerTamanho;
    private Spinner spinnerCor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_texto);

        etTexto = findViewById(R.id.etTexto);
        spinnerTamanho = findViewById(R.id.spinnerTamanho);
        spinnerCor = findViewById(R.id.spinnerCor);
        RadioGroup radioGroupPosicao = findViewById(R.id.radioGroupPosicao);

        // Spinners com opções de tamanho e cores
        String[] tamanhos = {"25", "30", "45", "55", "80", "95", "120", "140"};
        ArrayAdapter adapterTamanho = new ArrayAdapter(this, android.R.layout.simple_spinner_item, tamanhos);
        spinnerTamanho.setAdapter(adapterTamanho);

        String[] cores = {"Preto", "Branco", "Vermelho", "Azul", "Verde", "Amarelo"};
        ArrayAdapter adapterCor = new ArrayAdapter(this, android.R.layout.simple_spinner_item, cores);
        spinnerCor.setAdapter(adapterCor);

        // Carregar dados do intent
        Intent intent = getIntent();
        String textoAtual = intent.getStringExtra(EXTRA_TEXTO_ATUAL);
        String corAtual = intent.getStringExtra(EXTRA_COR_ATUAL);
        String textoTopoAtual = intent.getStringExtra(EXTRA_TEXTO_TOPO_ATUAL);

        // Verificar qual texto está sendo editado
        boolean editandoTopo = textoTopoAtual != null && !textoTopoAtual.isEmpty();

        if (editandoTopo) {
            radioGroupPosicao.check(R.id.radioTopo);
            etTexto.setText(textoTopoAtual);
        } else {
            radioGroupPosicao.check(R.id.radioBaixo);
            etTexto.setText(textoAtual);
        }

        if (corAtual != null) {
            int posicao = ((ArrayAdapter) spinnerCor.getAdapter()).getPosition(corAtual);
            spinnerCor.setSelection(posicao);
        }
    }

    public void enviarNovoTexto(View v) {
        Intent intent = new Intent();

        String novoTexto = etTexto.getText().toString();
        String novaCor = spinnerCor.getSelectedItem().toString();
        String novoTamanho = spinnerTamanho.getSelectedItem().toString();

        RadioGroup radioGroupPosicao = findViewById(R.id.radioGroupPosicao);
        boolean editandoTopo = radioGroupPosicao.getCheckedRadioButtonId() == R.id.radioTopo;

        if (editandoTopo) {
            intent.putExtra(EXTRA_NOVO_TEXTO_TOPO, novoTexto);
            intent.putExtra(EXTRA_NOVA_COR_TOPO, novaCor);
            intent.putExtra(EXTRA_NOVO_TAMANHO_TOPO, novoTamanho);
        } else {
            intent.putExtra(EXTRA_NOVO_TEXTO, novoTexto);
            intent.putExtra(EXTRA_NOVA_COR, novaCor);
            intent.putExtra(EXTRA_NOVO_TAMANHO, novoTamanho);
        }

        setResult(RESULT_OK, intent);
        intent.putExtra(EXTRA_POSICAO_ESCOLHIDA, editandoTopo ? "topo" : "baixo");
        finish();
    }
}