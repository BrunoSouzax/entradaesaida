package com.bsouza.entradaesaida01;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private DatabaseReference database;
    private EditText editTextUnit, editTextName, editTextDate, editTextTime, editTextDescription;
    private Spinner spinnerPlantonista;
    private EditText editTextOutroPlantonista;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

        editTextUnit = findViewById(R.id.editTextUnit);
        editTextName = findViewById(R.id.editTextName);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        editTextDescription = findViewById(R.id.editTextDescription);
        spinnerPlantonista = findViewById(R.id.spinnerPlantonista);
        editTextOutroPlantonista = findViewById(R.id.editTextOutroPlantonista);
        Button buttonTakePhoto = findViewById(R.id.buttonTakePhoto);
        Button buttonAddOrder = findViewById(R.id.buttonAddOrder);

        // Inicializar referência do Firebase
        database = FirebaseDatabase.getInstance().getReference();

        // Configurar o Spinner de Plantonistas
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.plantonista_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlantonista.setAdapter(adapter);
        spinnerPlantonista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                if (selectedOption.equals("Outro")) {
                    editTextOutroPlantonista.setVisibility(View.VISIBLE);
                } else {
                    editTextOutroPlantonista.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Não é necessário fazer nada aqui
            }
        });

        // Configurar o clique do botão para tirar uma foto
        buttonTakePhoto.setOnClickListener(v -> dispatchTakePictureIntent());

        // Configurar o clique do botão para adicionar uma nova encomenda
        buttonAddOrder.setOnClickListener(v -> {
            String unit = editTextUnit.getText().toString();
            String name = editTextName.getText().toString();
            String date = editTextDate.getText().toString();
            String time = editTextTime.getText().toString();
            String description = editTextDescription.getText().toString();
            String plantonista;
            if (editTextOutroPlantonista.getVisibility() == View.VISIBLE) {
                plantonista = editTextOutroPlantonista.getText().toString();
            } else {
                plantonista = spinnerPlantonista.getSelectedItem().toString();
            }

            if (!unit.isEmpty() && !name.isEmpty() && !date.isEmpty() && !time.isEmpty() && !description.isEmpty() && !plantonista.isEmpty()) {
                String orderId = database.child("orders").push().getKey(); // Gera um novo ID único
                Order order = new Order();
                order.setUnit(unit);
                order.setName(name);
                order.setDate(date);
                order.setTime(time);
                order.setDescription(description);
                order.setPlantonista(plantonista);
                order.setImageUrl(currentPhotoPath);
                order.setOrderId(orderId); // Define o ID da encomenda
                order.setStatus("-");

                // Fazer o upload da imagem para o Firebase Storage
                if (currentPhotoPath != null) {
                    uploadImageToFirebaseStorage(currentPhotoPath, order, orderId);
                } else {
                    database.child("orders").child(orderId).setValue(order);
                    Toast.makeText(MainActivity.this, "Encomenda adicionada com sucesso", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            }
        });

        // Preencher data e hora atuais automaticamente
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        String currentTime = timeFormat.format(new Date());
        editTextDate.setText(currentDate);
        editTextTime.setText(currentTime);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            // Lógica para a opção Adicionar aqui
            return true;
        } else if (id == R.id.action_consultar) {
            startActivity(new Intent(MainActivity.this, OrdersActivity.class));
            return true;
        } else if (id == R.id.action_requisicoes) {
            startActivity(new Intent(MainActivity.this, RequisitionsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("MainActivity", "Error occurred while creating the file", ex);
                Toast.makeText(this, "Erro ao criar arquivo de imagem", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.bsouza.entradaesaida01.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Log.e("MainActivity", "Photo file is null");
                Toast.makeText(this, "Erro ao criar arquivo de imagem", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("MainActivity", "No camera activity found");
            Toast.makeText(this, "Nenhuma atividade de câmera encontrada", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir("Pictures");
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Foto capturada", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToFirebaseStorage(String imagePath, Order order, String orderId) {
        // Inicializar Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Criar uma referência para o arquivo a ser enviado
        Uri file = Uri.fromFile(new File(imagePath));
        StorageReference imagesRef = storageRef.child("images/" + file.getLastPathSegment());

        // Fazer o upload do arquivo
        UploadTask uploadTask = imagesRef.putFile(file);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Obter a URL de download da imagem
            imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                Log.d("MainActivity", "Imagem enviada com sucesso. URL: " + downloadUrl);
                // Atualizar a URL da foto no Firebase Realtime Database
                order.setImageUrl(downloadUrl);
                database.child("orders").child(orderId).setValue(order);
                Toast.makeText(MainActivity.this, "Encomenda adicionada com sucesso", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Log.e("MainActivity", "Erro ao enviar imagem", e);
            Toast.makeText(MainActivity.this, "Erro ao enviar imagem", Toast.LENGTH_SHORT).show();
        });
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissão concedida", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
