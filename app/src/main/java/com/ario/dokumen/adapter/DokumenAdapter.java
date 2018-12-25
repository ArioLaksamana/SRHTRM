package com.ario.dokumen.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ario.dokumen.R;
import com.ario.dokumen.model.Dokumen;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DokumenAdapter extends RecyclerView.Adapter<DokumenAdapter.ViewHolder> {

    private final List<Dokumen> dokumenList = new ArrayList<>();

    final private ListItemClickListener listItemClickListener;
    final private OnItemLongClickListener listItemLongClickListener;

    public interface ListItemClickListener {
        void onListItemClick(Dokumen dokumen);
    }

    public interface OnItemLongClickListener {
        boolean onListItemLongClick(Dokumen dokumen);
    }

    public DokumenAdapter(ListItemClickListener listItemClickListener, OnItemLongClickListener listItemLongClickListener) {
        this.listItemClickListener = listItemClickListener;
        this.listItemLongClickListener = listItemLongClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView tv_penerima, tv_tanggal, tv_nomer, tv_perihal;
        ImageView iv_signature;

        ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            tv_penerima = view.findViewById(R.id.tv_penerima);
            iv_signature = view.findViewById(R.id.iv_signature);
            tv_tanggal = view.findViewById(R.id.tv_tanggal);
            tv_nomer = view.findViewById(R.id.tv_nomer);
            tv_perihal = view.findViewById(R.id.tv_perihal);
        }


        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            listItemClickListener.onListItemClick(dokumenList.get(clickedPosition));
        }

        @Override
        public boolean onLongClick(View v) {
            int clickedPosition = getAdapterPosition();
            listItemLongClickListener.onListItemLongClick(dokumenList.get(clickedPosition));
            return true;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_dokumen, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Dokumen dokumen = dokumenList.get(position);

        if (!dokumen.getSignatureUrl().trim().equals("")) {
            Picasso.get()
                    .load(dokumen.getSignatureUrl())
                    .into(holder.iv_signature);
        }

        holder.tv_penerima.setText(dokumen.getPenerima());
        holder.tv_tanggal.setText(dokumen.getTanggal());
        holder.tv_nomer.setText(dokumen.getNomor());
        holder.tv_perihal.setText(dokumen.getPerihal());
    }

    @Override
    public int getItemCount() {
        return dokumenList.size();
    }

    public void swapData(List<Dokumen> dokumenList) {
        this.dokumenList.clear();
        if (dokumenList != null && !dokumenList.isEmpty()) {
            this.dokumenList.addAll(dokumenList);
        }
        notifyDataSetChanged();
    }

}
