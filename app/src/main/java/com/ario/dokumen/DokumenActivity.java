package com.ario.dokumen;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ario.dokumen.model.Dokumen;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class DokumenActivity extends AppCompatActivity {

    private EditText input_no_surat, input_perihal_surat, input_penerima_surat;

    private SignaturePad mSignaturePad;
    private Button btn_clear_signature;

    private boolean signatureCreated = false;

    private DatabaseReference mDatabase;

    private StorageReference firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dokumen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tanda Terima Dokumen");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance().getReference();

        input_no_surat = findViewById(R.id.input_no_surat);
        input_perihal_surat = findViewById(R.id.input_perihal_surat);
        input_penerima_surat = findViewById(R.id.input_penerima_surat);

        mSignaturePad = findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {
                btn_clear_signature.setEnabled(true);
                signatureCreated = true;
            }

            @Override
            public void onClear() {
                btn_clear_signature.setEnabled(false);
                signatureCreated = false;
            }
        });

        btn_clear_signature = findViewById(R.id.btn_clear_signature);
        btn_clear_signature.setEnabled(false);
        btn_clear_signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        Button btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToFirebase();
            }
        });
    }

    private void saveToFirebase() {
        String noSurat = input_no_surat.getText().toString().trim();
        if (noSurat.equals("")) {
            showToast("No surat tidak boleh kosong");
            return;
        }

        String perihalSurat = input_perihal_surat.getText().toString().trim();
        if (perihalSurat.equals("")) {
            showToast("Perihal surat tidak boleh kosong");
            return;
        }

        Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
        if (!signatureCreated) {
            showToast("Tanda tangan tidak boleh kosong");
            return;
        }

        final String penerimaSurat = input_penerima_surat.getText().toString().trim();
        if (penerimaSurat.equals("")) {
            showToast("Penerima surat tidak boleh kosong");
            return;
        }

        final String[] nomerSuratList = noSurat.split("\n");
        final String[] perihalSuratList = perihalSurat.split("\n");

        if (nomerSuratList.length != perihalSuratList.length) {
            showToast("Banyaknya nomer surat harus sama dengan perihal surat");
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        signatureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        final StorageReference mTTTDref = firebaseStorage.child("tandatangan/" + UUID.randomUUID().toString() + ".jpg");
        UploadTask uploadTask = mTTTDref.putBytes(data);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return mTTTDref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    Log.d("FIREBASE_STORAGE", downloadUri.toString());

                    for (int i = 0; i < nomerSuratList.length; i++) {
                        final String nomer = nomerSuratList[i];
                        final String perihal = perihalSuratList[i];

                        Dokumen dokumen = new Dokumen(
                                nomer,
                                perihal,
                                getCurrentDateTime(),
                                downloadUri.toString(),
                                penerimaSurat
                        );

                        uploadData(dokumen);
                    }
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    private void showToast(String pesan) {
        Toast.makeText(DokumenActivity.this, pesan, Toast.LENGTH_LONG).show();
    }

    private String getCurrentDateTime() {
        Locale id = new Locale("in", "ID");
        Long tsLong = System.currentTimeMillis();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy HH:mm", id);
            Date netDate = (new Date(tsLong));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }

    private void uploadData(Dokumen dokumen) {
        mDatabase.push().setValue(dokumen)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast("Berhasil menambah dokumen");
                        DokumenActivity.this.finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Gagal menambah dokumen");
                    }
                });
    }
}
