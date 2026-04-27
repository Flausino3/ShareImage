package br.ifmg.edu.bsi.progmovel.shareimage1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia;
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Activity que cria uma imagem com um texto e imagem de fundo.
 */
public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private MemeCreator memeCreator;
    private String textoSelecionado = null;
    private final ActivityResultLauncher<Intent> startNovoTexto = registerForActivityResult(new StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent != null) {
                            String novoTexto = intent.getStringExtra(NovoTextoActivity.EXTRA_NOVO_TEXTO);
                            String novaCor = intent.getStringExtra(NovoTextoActivity.EXTRA_NOVA_COR);
                            String novoTamanho = intent.getStringExtra(NovoTextoActivity.EXTRA_NOVO_TAMANHO);

                            String novoTextoTopo = intent.getStringExtra(NovoTextoActivity.EXTRA_NOVO_TEXTO_TOPO);
                            String novaCorTopo = intent.getStringExtra(NovoTextoActivity.EXTRA_NOVA_COR_TOPO);
                            String novoTamanhoTopo = intent.getStringExtra(NovoTextoActivity.EXTRA_NOVO_TAMANHO_TOPO);

                            // Aplicar texto de cima
                            if (novoTextoTopo != null && novaCorTopo != null && novoTamanhoTopo != null) {
                                memeCreator.setTextoTopo(novoTextoTopo);
                                memeCreator.setCorTextoTopo(converterCor(novaCorTopo));
                                memeCreator.setTamanhoTextoTopo(Integer.parseInt(novoTamanhoTopo));
                            }

                            if (novaCor == null) {
                                novaCor = "Preto";
                            }

                            if (novoTamanho == null) {
                                novoTamanho = "15";
                            }

                            memeCreator.setTexto(novoTexto);
                            memeCreator.setCorTexto(converterCor(novaCor));
                            memeCreator.setTamanhoTexto(Integer.parseInt(novoTamanho));
                            mostrarImagem();
                            textoSelecionado = intent.getStringExtra(NovoTextoActivity.EXTRA_POSICAO_ESCOLHIDA);
                            if (textoSelecionado != null) {
                                exibirMensagem("Toque na tela para reposicionar o texto!");
                            }

                        }
                    }
                }
            });

    private void exibirMensagem(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    // Após o usuário escolher uma imagem, abre a FiltrosActivity para aplicar um filtro antes de usar como fundo.
    private final ActivityResultLauncher<PickVisualMediaRequest> startImagemFundo = registerForActivityResult(new PickVisualMedia(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result == null) {
                        return;
                    }
                    try (ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(result, "r")) {
                        Bitmap imagemFundo = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), result);

                        // descobrir se é preciso rotacionar a imagem
                        FileDescriptor fd = pfd.getFileDescriptor();
                        ExifInterface exif = new ExifInterface(fd);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                            Matrix matrix = new Matrix();
                            matrix.postRotate(90);
                            imagemFundo = Bitmap.createBitmap(imagemFundo, 0, 0, imagemFundo.getWidth(), imagemFundo.getHeight(), matrix, true);
                        }

                        // Passa a imagem para a FiltrosActivity via campo estático para evitar limite do Bundle
                        FiltrosActivity.imagemEntrada = imagemFundo;
                        startFiltros.launch(new Intent(MainActivity.this, FiltrosActivity.class));
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });

    // Recebe a imagem de volta da FiltrosActivity (com ou sem filtro aplicado) e usa como fundo do meme.
    private final ActivityResultLauncher<Intent> startFiltros = registerForActivityResult(new StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && FiltrosActivity.imagemResultado != null) {
                        memeCreator.setFundo(FiltrosActivity.imagemResultado);
                        mostrarImagem();
                    }
                    // Limpa os campos estáticos para liberar memória
                    FiltrosActivity.imagemEntrada = null;
                    FiltrosActivity.imagemResultado = null;
                }
            });

    private ActivityResultLauncher<String> startWriteStoragePermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (!result) {
                        Toast.makeText(MainActivity.this, "Sem permissão de acesso a armazenamento do celular.", Toast.LENGTH_SHORT).show();
                    } else {
                        compartilhar(null);
                    }
                }
            });

    private class TemplateAdapter extends BaseAdapter {
        private int[] templates = {
                R.drawable.template1,
                R.drawable.template2,
                R.drawable.template3,
                R.drawable.template4,
                R.drawable.template5,
                R.drawable.template6,
                R.drawable.template7,
                R.drawable.template8
        };

        @Override
        public int getCount() {
            return templates.length;
        }

        @Override
        public Object getItem(int position) {
            return templates[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(MainActivity.this);
                imageView.setLayoutParams(new GridView.LayoutParams(250, 250));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setImageResource(templates[position]);
            return imageView;
        }
    }

    private void abrirSeletorTemplates() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolha um Template");

        GridView gridView = new GridView(this);
        gridView.setNumColumns(2);
        gridView.setAdapter(new TemplateAdapter());

        builder.setView(gridView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            int templateId = ((Integer) parent.getAdapter().getItem(position));
            Bitmap templateBitmap = BitmapFactory.decodeResource(getResources(), templateId);
            memeCreator.setFundo(templateBitmap);
            mostrarImagem();
            alertDialog.dismiss();
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void configurarTouchListener() {
        imageView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.performClick();
                }

                if (textoSelecionado != null) {
                    float posX = event.getX() / imageView.getWidth();
                    float posY = event.getY() / imageView.getHeight();

                    if (textoSelecionado.equals("topo")) {
                        memeCreator.setPosicaoTopo(posX, posY);
                    } else {
                        memeCreator.setPosicaoBaixo(posX, posY);
                    }

                    mostrarImagem();
                    textoSelecionado = null;
                    exibirMensagem("Posição atualizada!");
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnTemplates = findViewById(R.id.btnTemplates);
        btnTemplates.setOnClickListener(v -> abrirSeletorTemplates());
        imageView = findViewById(R.id.imageView);

        configurarTouchListener();

        imageView.setOnClickListener(v -> {
            // vazio, só pra satisfazer o aviso
        });

        Bitmap imagemFundo = BitmapFactory.decodeResource(getResources(), R.drawable.fry_meme);

        memeCreator = new MemeCreator("Olá Android!", Color.WHITE, imagemFundo, getResources().getDisplayMetrics());
        mostrarImagem();
    }

    public void iniciarMudarTexto(View v) {
        Intent intent = new Intent(this, NovoTextoActivity.class);
        intent.putExtra(NovoTextoActivity.EXTRA_TEXTO_ATUAL, memeCreator.getTexto());
        intent.putExtra(NovoTextoActivity.EXTRA_COR_ATUAL, converterCorParaString(memeCreator.getCorTexto()));

        intent.putExtra(NovoTextoActivity.EXTRA_TEXTO_TOPO_ATUAL, memeCreator.getTextoTopo());
        intent.putExtra(NovoTextoActivity.EXTRA_COR_TOPO_ATUAL, converterCorParaString(memeCreator.getCorTextoTopo()));

        startNovoTexto.launch(intent);
    }

    // converte nome em português para Color
    public int converterCor(String cor) {
        switch (cor) {
            case "Preto": return Color.BLACK;
            case "Branco": return Color.WHITE;
            case "Azul": return Color.BLUE;
            case "Verde": return Color.GREEN;
            case "Vermelho": return Color.RED;
            case "Amarelo": return Color.YELLOW;
        }
        return Color.BLACK;
    }

    // converte Color para nome em português
    public String converterCorParaString(int cor) {
        switch (cor) {
            case Color.BLACK: return "Preto";
            case Color.WHITE: return "Branco";
            case Color.BLUE: return "Azul";
            case Color.GREEN: return "Verde";
            case Color.RED: return "Vermelho";
            case Color.YELLOW: return "Amarelo";
        }
        return "Preto";
    }

    public void iniciarMudarFundo(View v) {
        startImagemFundo.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ImageOnly.INSTANCE)
                .build());
    }

    public void compartilhar(View v) {
        compartilharImagem(memeCreator.getImagem());
    }

    public void mostrarImagem() {
        imageView.setImageBitmap(memeCreator.getImagem());
    }

    public void compartilharImagem(Bitmap bitmap) {

        // pegar a uri da mediastore
        // pego o volume externo pq normalmente ele é maior que o volume interno.
        Uri contentUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            /*
            Em versões <= 28, é preciso solicitar a permissão WRITE_EXTERNAL_STORAGE.
            Mais detalhes em https://developer.android.com/training/data-storage/shared/media#java.
             */
            int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (PackageManager.PERMISSION_GRANTED != write) {
                startWriteStoragePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                return;
            }
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        // montar a nova imagem a ser inserida na mediastore
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "shareimage1file");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        Uri imageUri = getContentResolver().insert(contentUri, values);

        // criar a nova imagem na pasta da mediastore
        try (
                ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(imageUri, "w");
                FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor())
        ) {
            BufferedOutputStream bytes = new BufferedOutputStream(fos);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao gravar imagem:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        // compartilhar a imagem com intent implícito
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_TITLE, "Seu meme fabuloso");
        share.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(share, "Compartilhar Imagem"));
    }
}