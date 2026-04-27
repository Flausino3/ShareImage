package br.ifmg.edu.bsi.progmovel.shareimage1;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

// Activity responsável por exibir a imagem selecionada e permitir que o usuário
// escolha um filtro antes de confirmar o uso como fundo do meme.
public class FiltrosActivity extends AppCompatActivity {

    // Campos estáticos usados para passar o Bitmap entre activities sem passar pelo Intent,
    // já que Bitmaps grandes demais causam erro ao ser colocados em extras.
    public static Bitmap imagemEntrada = null;
    public static Bitmap imagemResultado = null;

    private ImageView imageViewPreview;
    private Bitmap imagemAtual; // guarda o bitmap com o filtro que está sendo visualizado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros);

        imageViewPreview = findViewById(R.id.imageViewPreview);

        // Começa mostrando a imagem sem nenhum filtro
        imagemAtual = imagemEntrada;
        imageViewPreview.setImageBitmap(imagemAtual);

        configurarBotoesFiltro();

        Button btnAplicar = findViewById(R.id.btnAplicar);
        Button btnCancelar = findViewById(R.id.btnCancelar);

        // Confirma o filtro escolhido e devolve para a MainActivity
        btnAplicar.setOnClickListener(v -> {
            imagemResultado = imagemAtual;
            setResult(Activity.RESULT_OK);
            finish();
        });

        // Descarta as alterações e volta sem mudar nada
        btnCancelar.setOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED);
            finish();
        });
    }

    // Configura cada botão de filtro para atualizar o preview em tempo real.
    private void configurarBotoesFiltro() {
        Button btnCinza = findViewById(R.id.btnCinza);
        Button btnSepia = findViewById(R.id.btnSepia);
        Button btnContraste = findViewById(R.id.btnContraste);
        Button btnBrilho = findViewById(R.id.btnBrilho);
        Button btnBorda = findViewById(R.id.btnBorda);

        btnCinza.setOnClickListener(v -> aplicarFiltro("cinza"));
        btnSepia.setOnClickListener(v -> aplicarFiltro("sepia"));
        btnContraste.setOnClickListener(v -> aplicarFiltro("contraste"));
        btnBrilho.setOnClickListener(v -> aplicarFiltro("brilho"));
        btnBorda.setOnClickListener(v -> aplicarFiltro("borda"));
    }

    // Aplica o filtro escolhido sobre a imagem original (imagemEntrada) e atualiza o preview.
    // Sempre parte da imagem original para evitar degradação ao trocar de filtro.
    private void aplicarFiltro(String filtro) {
        switch (filtro) {
            case "cinza":
                imagemAtual = FiltrosImagemUtil.aplicarCinza(imagemEntrada);
                break;
            case "sepia":
                imagemAtual = FiltrosImagemUtil.aplicarSepia(imagemEntrada);
                break;
            case "contraste":
                imagemAtual = FiltrosImagemUtil.aplicarContraste(imagemEntrada);
                break;
            case "brilho":
                imagemAtual = FiltrosImagemUtil.aplicarBrilho(imagemEntrada);
                break;
            case "borda":
                imagemAtual = FiltrosImagemUtil.aplicarBorda(imagemEntrada);
                break;
        }
        imageViewPreview.setImageBitmap(imagemAtual);
    }
}
