package br.ifmg.edu.bsi.progmovel.shareimage1;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

// Classe utilitária com métodos estáticos para aplicar filtros em imagens Bitmap.
// Cada método recebe um Bitmap original e retorna um novo Bitmap com o filtro aplicado.
public class FiltrosImagemUtil {

    // Remove as informações de cor da imagem, mantendo apenas a luminosidade.
    public static Bitmap aplicarCinza(Bitmap imagem) {
        Bitmap resultado = Bitmap.createBitmap(imagem.getWidth(), imagem.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultado);
        Paint paint = new Paint();

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0); // saturação 0 = sem cor = tons de cinza
        paint.setColorFilter(new ColorMatrixColorFilter(matrix));

        canvas.drawBitmap(imagem, 0, 0, paint);
        return resultado;
    }

    // Aplica tom sépia: deixa a imagem com aparência de foto antiga, com tons marrons/avermelhados.
    public static Bitmap aplicarSepia(Bitmap imagem) {
        Bitmap resultado = Bitmap.createBitmap(imagem.getWidth(), imagem.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultado);
        Paint paint = new Paint();

        // Matriz de cor que mistura os canais RGB para produzir o efeito sépia
        ColorMatrix matrix = new ColorMatrix(new float[]{
                0.393f, 0.769f, 0.189f, 0, 0,
                0.349f, 0.686f, 0.168f, 0, 0,
                0.272f, 0.534f, 0.131f, 0, 0,
                0,      0,      0,      1, 0
        });
        paint.setColorFilter(new ColorMatrixColorFilter(matrix));

        canvas.drawBitmap(imagem, 0, 0, paint);
        return resultado;
    }

    // Aumenta o contraste multiplicando os canais de cor, tornando os tons mais intensos.
    public static Bitmap aplicarContraste(Bitmap imagem) {
        Bitmap resultado = Bitmap.createBitmap(imagem.getWidth(), imagem.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultado);
        Paint paint = new Paint();

        float contraste = 1.8f; // valores acima de 1 aumentam o contraste
        float offset = 128 * (1 - contraste); // compensa o deslocamento para manter o meio tom estável
        ColorMatrix matrix = new ColorMatrix(new float[]{
                contraste, 0,         0,         0, offset,
                0,         contraste, 0,         0, offset,
                0,         0,         contraste, 0, offset,
                0,         0,         0,         1, 0
        });
        paint.setColorFilter(new ColorMatrixColorFilter(matrix));

        canvas.drawBitmap(imagem, 0, 0, paint);
        return resultado;
    }

    // Aumenta o brilho adicionando um valor constante a cada canal RGB.
    public static Bitmap aplicarBrilho(Bitmap imagem) {
        Bitmap resultado = Bitmap.createBitmap(imagem.getWidth(), imagem.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultado);
        Paint paint = new Paint();

        float brilho = 60; // valor positivo clareia, negativo escurece
        ColorMatrix matrix = new ColorMatrix(new float[]{
                1, 0, 0, 0, brilho,
                0, 1, 0, 0, brilho,
                0, 0, 1, 0, brilho,
                0, 0, 0, 1, 0
        });
        paint.setColorFilter(new ColorMatrixColorFilter(matrix));

        canvas.drawBitmap(imagem, 0, 0, paint);
        return resultado;
    }

    // Adiciona uma borda preta simples ao redor da imagem.
    public static Bitmap aplicarBorda(Bitmap imagem) {
        int espessura = 20; // espessura da borda em pixels
        Bitmap resultado = Bitmap.createBitmap(imagem.getWidth(), imagem.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultado);

        // Desenha a imagem original
        canvas.drawBitmap(imagem, 0, 0, null);

        // Desenha um retângulo vazado por cima, simulando a borda
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(espessura * 2); // dobro porque a metade fica fora do bitmap
        canvas.drawRect(0, 0, imagem.getWidth(), imagem.getHeight(), paint);

        return resultado;
    }
}
