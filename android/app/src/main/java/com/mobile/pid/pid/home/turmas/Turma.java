package com.mobile.pid.pid.home.turmas;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by junio on 07/03/2018.
 */

// TODO: Completar classe Turma

public class Turma implements Parcelable // Parcelable Necessário pra passar ele pra outra activity https://youtu.be/ROQ4T47nMhI?t=619
{
    @Exclude
    private String uid;

    private String capaUrl;
    private String nome;
    private String pin;

    private Map<String, Integer> diasDaSemana;

    private InfoUsuario professor;
    private List<InfoUsuario> alunos;

    public Turma() {}

    public Turma(String nome, String pin, String capaUrl, InfoUsuario professor, Map<String, Integer> diasDaSemana)
    {
        this.capaUrl = capaUrl;
        this.nome = nome;
        this.pin  = pin;
        this.diasDaSemana = diasDaSemana;

        this.professor = professor;
    }

    public Turma(String nome, String pin, String capaUrl, String imagemProf, String nomeProf, String uidProf, Map<String, Integer> diasDaSemana)
    {
        this.capaUrl = capaUrl;
        this.nome = nome;
        this.pin  = pin;
        this.diasDaSemana = diasDaSemana;

        this.professor = new InfoUsuario(nomeProf, imagemProf, uidProf);
    }


    public String getUid(){
        return uid;
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPin(){
        return this.pin;
    }

    public void setPin(String pin){
        this.pin = pin;
    }

    public String getCapaUrl() {
        return this.capaUrl;
    }

    public InfoUsuario getProfessor() {
        return professor;
    }

    public void setProfessor(InfoUsuario professor)
    {
        this.professor = professor;
    }

    public List<InfoUsuario> getAlunos() {
        return this.alunos;
    }

    public Map<String, Integer> getDiasDaSemana() {
        return this.diasDaSemana;
    }

    /*@Exclude
    public String getQtdProfessores(){
        if (professor == null)
            return "0";
        else
            return Integer.toString(professor.size());
    }*/

    @Exclude
    public String getQtdAlunos(){
        if (alunos == null)
            return "0";
        else
            return Integer.toString(alunos.size());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.uid);
        dest.writeString(this.capaUrl);
        dest.writeString(this.nome);
        dest.writeString(this.pin);
        dest.writeInt(this.diasDaSemana.size());
        for (Map.Entry<String, Integer> entry : this.diasDaSemana.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeList(this.alunos);
        dest.writeParcelable(this.professor, flags);
    }

    protected Turma(Parcel in)
    {
        this.uid = in.readString();
        this.capaUrl = in.readString();
        this.nome = in.readString();
        this.pin = in.readString();
        int diasDaSemanaSize = in.readInt();
        this.diasDaSemana = new HashMap<String, Integer>(diasDaSemanaSize);
        for (int i = 0; i < diasDaSemanaSize; i++)
        {
            String key = in.readString();
            Integer value = (Integer) in.readValue(Integer.class.getClassLoader());
            this.diasDaSemana.put(key, value);
        }

        this.professor = new InfoUsuario();
        this.professor = in.readParcelable(InfoUsuario.class.getClassLoader());
        this.alunos = new ArrayList<InfoUsuario>();
        in.readList(this.alunos, InfoUsuario.class.getClassLoader());
    }

    public static final Creator<Turma> CREATOR = new Creator<Turma>()
    {
        @Override
        public Turma createFromParcel(Parcel source) {
            return new Turma(source);
        }

        @Override
        public Turma[] newArray(int size) {
            return new Turma[size];
        }
    };
}