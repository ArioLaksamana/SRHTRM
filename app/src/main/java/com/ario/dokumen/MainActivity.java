package com.ario.dokumen;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ario.dokumen.adapter.DokumenAdapter;
import com.ario.dokumen.model.Dokumen;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DokumenAdapter.ListItemClickListener, DokumenAdapter.OnItemLongClickListener {

    private final DokumenAdapter dokumenAdapter = new DokumenAdapter(this, this);

    public static final String TAG = "FIREBASE_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tanda Terima Surat PLNBB");

        RecyclerView rv_dokumen = findViewById(R.id.rv_dokumen);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv_dokumen.setLayoutManager(layoutManager);
        rv_dokumen.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv_dokumen.getContext(),
                ((LinearLayoutManager) layoutManager).getOrientation());
        rv_dokumen.addItemDecoration(dividerItemDecoration);
        rv_dokumen.setAdapter(dokumenAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DokumenActivity.class));
            }
        });

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    List<Dokumen> dokumenList = new ArrayList<>();

                    for (DataSnapshot mData : dataSnapshot.getChildren()) {
                        Dokumen dokumen = mData.getValue(Dokumen.class);
                        dokumenList.add(dokumen);
                    }

                    Collections.reverse(dokumenList);
                    dokumenAdapter.swapData(dokumenList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onListItemClick(Dokumen dokumen) {
        // Do nothing
    }

    @Override
    public boolean onListItemLongClick(final Dokumen dokumen) {

        new AlertDialog.Builder(this)
                .setTitle("Hapus Dokumen")
                .setMessage("Apakah Anda yakin ingin menghapus dokumen ini?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        Query applesQuery = ref.orderByChild("nomor").equalTo(dokumen.getNomor());

                        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                                    appleSnapshot.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

        return true;
    }
}
