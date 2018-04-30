package com.mobile.pid.pid.home.adapters;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile.pid.pid.R;
import com.mobile.pid.pid.home.feed.FeedFragment;
import com.mobile.pid.pid.home.feed.Post;
import com.mobile.pid.pid.home.perfil.PerfilFragment;
import com.mobile.pid.pid.home.perfil.UsuarioPerfilActivity;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by jonasramos on 13/03/18.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.RecyclerViewHolder>{

    private Context context;
    private List<Post> posts;

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_layout, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position)
    {
        final Post p = posts.get(position);
        final String usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        holder.usuario.setText(p.getUser());

        try
        {
            holder.postTime.setText(calcularTempo(p.getPostDataFormatado()));
        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }

        holder.texto.setText(p.getTexto());

        Glide.with(holder.foto.getContext())
                .load(p.getPhotoUrl())
                .into(holder.foto);

        //TODO SE CLICAR NA FOTO DO USUARIO REDIRECIONA PRO PERFIL DA PESSOA
        holder.foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPerfilUsuario(p);
            }
        });


        holder.like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(holder.like.isChecked()){
                    holder.like.setButtonTintList(context.getResources().getColorStateList(R.color.red));

                    FirebaseDatabase.getInstance().getReference().child("posts").child(p.getId())
                            .child("likes").child(usuario).setValue(true);

                    // SALVAR NOS POSTS CURTIDOS DO USUARIO
                    FirebaseDatabase.getInstance().getReference("usuarios").child(usuario).child("posts_like")
                            .child(p.getId()).setValue(p);
                } else {
                    holder.like.setButtonTintList(context.getResources().getColorStateList(R.color.gray_font));

                    FirebaseDatabase.getInstance().getReference("posts").child(p.getId())
                            .child("likes").child(usuario).removeValue();

                    FirebaseDatabase.getInstance().getReference("usuarios").child(usuario).child("posts_like")
                            .child(p.getId()).removeValue();
                }
            }
        });

        // CARREGAR OS POSTS CURTIDOS COM O NUMERO DE CURTIDAS
        FirebaseDatabase.getInstance().getReference().child("posts").child(p.getId())
                .child("likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // CASO O CONTADOR DE CURTIDAS DO POST SEJA ZERO, ELE NAO MOSTRA O CONTADOR
                if(dataSnapshot.getChildrenCount() == 0)
                    holder.countLike.setVisibility(View.INVISIBLE);
                else {
                    holder.countLike.setVisibility(View.VISIBLE);
                    holder.countLike.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }

                // MOSTRA NO FEED O POST CURTIDO CASO O USUARIO TENHA CURTIDO ANTERIORMENTE
                if(dataSnapshot.hasChild(usuario)) {
                    holder.like.setButtonTintList(context.getResources().getColorStateList(R.color.red));
                    holder.like.setChecked(true);
                } else {
                    holder.like.setButtonTintList(context.getResources().getColorStateList(R.color.gray_font));
                    holder.like.setChecked(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public String calcularTempo(String data) throws ParseException
    {
        Calendar         c   = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        Date dataAtual = c.getTime();
        Date dataPost  = sdf.parse(data);

        long duracao     = dataAtual.getTime() - dataPost.getTime();
        long horas       = duracao / (60 * 60 * 1000);
        long minutos     = duracao / (60 * 1000) % 60;
        long segundos    = duracao / 1000 % 60;

        if(horas < 24)
            if(horas == 0)
                if(minutos == 0)
                    if (segundos <= 0)
                        return "Agora";
                    else
                        return String.valueOf(segundos) + "s";
                else
                    return String.valueOf(minutos) + "m";
            else
                return String.valueOf(horas) + "h";
        else
        {
            c.setTime(dataPost);
            return  String.format("%02d", c.get(Calendar.DAY_OF_MONTH)) + "/" +
                    String.format("%02d", (c.get(Calendar.MONTH) + 1)) + "/" +
                    c.get(Calendar.YEAR);
        }

    }

    public void dialogPerfilUsuario(final Post p) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_perfil_usuario, null);

        final AlertDialog.Builder mBuilderUsuario = new AlertDialog.Builder(context);
        mBuilderUsuario.setView(view);

        final Button seguir = view.findViewById(R.id.seguir);
        final Button irPerfil = view.findViewById(R.id.irPerfil);
        final TextView nome = view.findViewById(R.id.nome);
        final ImageView foto = view.findViewById(R.id.foto);
        final ImageView capa = view.findViewById(R.id.capa);

        final AlertDialog dialogUsuario = mBuilderUsuario.create();
        dialogUsuario.show();

        nome.setText(p.getUser());
        Glide.with(context)
                .load(p.getPhotoUrl())
                .into(foto);
        Glide.with(context)
                .load(p.getPhotoUrl())
                .apply(RequestOptions.overrideOf(20,20).error(android.R.drawable.dark_header))
                .into(capa);

        seguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogUsuario.dismiss();

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                mBuilder.setTitle(R.string.confirmacao)
                        .setMessage(context.getText(R.string.unfollow_message) + " " + p.getUser() + "?")
                        .setPositiveButton(R.string.confirmar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogUsuario.show();
                                seguir.setText(R.string.follow);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogUsuario.show();
                            }
                        });

                AlertDialog dialog = mBuilder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.gray_font));
            }
        });

        irPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, UsuarioPerfilActivity.class);
                i.putExtra("usuario", p.getUserId());
                context.startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void add(Post post) {
        this.posts.add(post);
        notifyDataSetChanged();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private ImageView foto;
        private TextView usuario;
        private TextView texto;
        private TextView postTime;
        private TextView countLike;
        private CheckBox like;

        public RecyclerViewHolder(final View itemView) {
            super(itemView);

            foto       = itemView.findViewById(R.id.icon_user_feed);
            usuario    = itemView.findViewById(R.id.tv_user_feed);
            texto      = itemView.findViewById(R.id.tv_message_feed);
            postTime   = itemView.findViewById(R.id.postTime);
            countLike  = itemView.findViewById(R.id.count_like);
            like = itemView.findViewById(R.id.cb_like);
            
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final Post p = posts.get(getPosition());

                    if(p.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setTitle(R.string.confirmacao)
                                .setMessage("Deseja excluir o post selecionado?")
                                .setPositiveButton(R.string.confirmar, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        /*FirebaseDatabase.getInstance().getReference("usuarios").child(p.getUserId())
                                               .child("posts").child(p.getId()).removeValue();
                                        FirebaseDatabase.getInstance().getReference("posts").child(p.getId()).removeValue();*/
                                    }
                                })
                                .setNegativeButton(R.string.cancel, null)
                                .create();

                        dialog.show();

                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.gray_font));

                        return true;
                    }

                    return false;
                }
            });
        }
    }

    public void removePost(Post p) {
        for (Post post: posts) {
            if(post.getId().equals(p.getId())) {
                posts.remove(post);
                notifyDataSetChanged();
            }
        }
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }
}
