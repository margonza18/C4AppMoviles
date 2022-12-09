package com.example.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.R;
import com.example.myapplication.models.Pokemon;

import java.util.ArrayList;

public class ListaPokemonAdapters  extends RecyclerView.Adapter<ListaPokemonAdapters.ViewHolder>{

    private ArrayList<Pokemon> dataset;
    private Context context;

    public ListaPokemonAdapters(Context context) {
        this.context=context;
        dataset= new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pokemon,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pokemon poke=dataset.get(position);
        holder.nombreTextView.setText(poke.getName());
        Glide.with(context)
                .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"+poke.getNumber()+ ".png")
                .centerCrop()//permite que la imagen quede centrada
                .diskCacheStrategy(DiskCacheStrategy.ALL)//limpia el cache, permitiendo traer las demas imgenes
                .into(holder.fotoImageView);//holder parte logica donde vamos adaptar la img, se acomoda en el fotoimageview. "la inserta"

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void AdicionarListaPokemon(ArrayList<Pokemon> listaPokemon) {
        dataset.addAll(listaPokemon);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView fotoImageView;
        private TextView nombreTextView;

     public ViewHolder(View itemView){
         super (itemView);

         fotoImageView=itemView.findViewById(R.id.fotoImageView);
         nombreTextView= itemView.findViewById(R.id.nombreTextView);


     }
 }


}
