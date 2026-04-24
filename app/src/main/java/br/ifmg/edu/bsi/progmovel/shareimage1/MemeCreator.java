package br.ifmg.edu.bsi.progmovel.shareimage1;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.DisplayMetrics;

/**
 * Cria um meme com um texto e uma imagem de fundo.
 *
 * Você pode controlar o texto, a cor do texto e a imagem de fundo.
 */
public class MemeCreator {
    private String texto;
    private int corTexto;
    private Bitmap fundo;
    private DisplayMetrics displayMetrics;
    private Bitmap meme;
    private boolean dirty; // se true, significa que o meme precisa ser recriado.
    private float tamanhoTexto; // novo atributo para tamanho
    private String textoTopo;
    private int corTextoTopo;
    private float tamanhoTextoTopo;
    private float posicaoXTopo;
    private float posicaoYTopo;
    private float posicaoXBaixo;
    private float posicaoYBaixo;
    public MemeCreator(String texto, int corTexto, Bitmap fundo, DisplayMetrics displayMetrics) {
        this.texto = texto;
        this.corTexto = corTexto;
        this.fundo = fundo;
        this.displayMetrics = displayMetrics;
        this.tamanhoTexto = 64.f;
        this.textoTopo = ""; // texto vazio no início
        this.corTextoTopo = corTexto;
        this.tamanhoTextoTopo = 64.f;
        this.posicaoXTopo = 0.5f;
        this.posicaoYTopo = 0.1f;
        this.posicaoXBaixo = 0.5f;
        this.posicaoYBaixo = 0.9f;
        this.meme = criarImagem();
        this.dirty = false;
    }

    public String getTextoTopo() {
        return textoTopo;
    }

    public void setTextoTopo(String textoTopo) {
        this.textoTopo = textoTopo;
        dirty = true;
    }

    public int getCorTextoTopo() {
        return corTextoTopo;
    }

    public void setCorTextoTopo(int corTextoTopo) {
        this.corTextoTopo = corTextoTopo;
        dirty = true;
    }

    public void setTamanhoTextoTopo(int tamanho) {
        this.tamanhoTextoTopo = (float) tamanho;
        dirty = true;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
        dirty = true;
    }

    public int getCorTexto() {
        return corTexto;
    }

    public void setCorTexto(int corTexto) {
        this.corTexto = corTexto;
        dirty = true;
    }

    public Bitmap getFundo() {
        return fundo;
    }

    public void setFundo(Bitmap fundo) {
        this.fundo = fundo;
        dirty = true;
    }

    public void setTamanhoTexto(int tamanho) {
        this.tamanhoTexto = (float) tamanho;
        dirty = true;
    }

    public void setPosicaoTopo(float x, float y) {
        this.posicaoXTopo = x;
        this.posicaoYTopo = y;
        dirty = true;
    }

    public void setPosicaoBaixo(float x, float y) {
        this.posicaoXBaixo = x;
        this.posicaoYBaixo = y;
        dirty = true;
    }

    public void rotacionarFundo(float graus) {
        Matrix matrix = new Matrix();
        matrix.postRotate(graus);
        fundo = Bitmap.createBitmap(fundo, 0, 0, fundo.getWidth(), fundo.getHeight(), matrix, true);
        dirty = true;
    }

    public Bitmap getImagem() {
        if (dirty) {
            meme = criarImagem();
            dirty = false;
        }
        return meme;
    }
    protected Bitmap criarImagem() {
        float heightFactor = (float) fundo.getHeight() / fundo.getWidth();
        int width = displayMetrics.widthPixels;
        int height = (int) (width * heightFactor);
        // nao deixa a imagem ocupar mais que 60% da altura da tela.
        if (height > displayMetrics.heightPixels * 0.6) {
            height = (int) (displayMetrics.heightPixels * 0.6);
            width = (int) (height * (1 / heightFactor));
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();

        Bitmap scaledFundo = Bitmap.createScaledBitmap(fundo, width, height, true);
        canvas.drawBitmap(scaledFundo, 0, 0, new Paint());

        paint.setColor(corTexto);
        paint.setAntiAlias(true);
        paint.setTextSize(tamanhoTexto); // agora usa a variável
        paint.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        paint.setTextAlign(Paint.Align.CENTER);

        // desenhar texto de cima
        if (textoTopo != null && !textoTopo.isEmpty()) {
            paint.setColor(corTextoTopo);
            paint.setTextSize(tamanhoTextoTopo);
            canvas.drawText(textoTopo, (width * posicaoXTopo), (height * posicaoYTopo), paint);
        }

        // desenhar texto embaixo
        if (texto != null && !texto.isEmpty()) {
            paint.setColor(corTexto);
            paint.setTextSize(tamanhoTexto);
            canvas.drawText(texto, (width * posicaoXBaixo), (height * posicaoYBaixo), paint);
        }
        return bitmap;
    }
}