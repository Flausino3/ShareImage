package br.ifmg.edu.bsi.progmovel.shareimage1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class NovoTextoActivity extends AppCompatActivity {

    public static String EXTRA_TEXTO_ATUAL = "br.ifmg.edu.bsi.progmovel.shareimage1.texto_atual";
    public static String EXTRA_COR_ATUAL = "br.ifmg.edu.bsi.progmovel.shareimage1.cor_atual";
    public static String EXTRA_TAMANHO_ATUAL = "br.ifmg.edu.bsi.progmovel.shareimage1.tamanho_atual";
    public static String EXTRA_NOVO_TEXTO = "br.ifmg.edu.bsi.progmovel.shareimage1.novo_texto";
    public static String EXTRA_NOVA_COR = "br.ifmg.edu.bsi.progmovel.shareimage1.nova_cor";
    public static String EXTRA_NOVO_TAMANHO = "br.ifmg.edu.bsi.progmovel.shareimage1.novo_tamanho";

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


        // spinners com opções de tamanho e cores

        String[] tamanhos = {"10", "15", "20", "50", "80", "95", "120", "140"};
        ArrayAdapter adapterTamanho = new ArrayAdapter(this, android.R.layout.simple_spinner_item, tamanhos);
        spinnerTamanho.setAdapter(adapterTamanho);

        String[] cores = {"Preto", "Branco", "Vermelho", "Azul", "Verde", "Amarelo"};
        ArrayAdapter adapterCor = new ArrayAdapter(this, android.R.layout.simple_spinner_item, cores);
        spinnerCor.setAdapter(adapterCor);

        Intent intent = getIntent();
        String textoAtual = intent.getStringExtra(EXTRA_TEXTO_ATUAL);
        String corAtual = intent.getStringExtra(EXTRA_COR_ATUAL);

        etTexto.setText(textoAtual);

        if (corAtual != null) {
            int posicao = ((ArrayAdapter) spinnerCor.getAdapter()).getPosition(corAtual);
            spinnerCor.setSelection(posicao);
        }
    }

    // metodo agora envia o tamanho do texto e a cor nova
    public void enviarNovoTexto(View v) {
        String novoTexto = etTexto.getText().toString();
        String novaCor = spinnerCor.getSelectedItem().toString();
        String novoTamanho = spinnerTamanho.getSelectedItem().toString();

        Intent intent = new Intent();
        intent.putExtra(EXTRA_NOVO_TEXTO, novoTexto);
        intent.putExtra(EXTRA_NOVA_COR, novaCor);
        intent.putExtra(EXTRA_NOVO_TAMANHO, novoTamanho);
        setResult(RESULT_OK, intent);
        finish();
    }
}