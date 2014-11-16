package net.rbcode.dbaile;

import android.graphics.Bitmap;

import org.json.JSONObject;

/**
 * Created by Borja on 03/08/2014.
 */
public class Evento {

    protected int nid;
    protected String title = "";
    protected String body = "";
    protected String imgUrl = "";
    protected String imgUri = "";
    protected String author = "";
    protected String fecha = "";
    protected String provincia = "";
    protected String ciudad = "";
    protected String calle = "";
    protected String codigoPostal = "";
    protected String precio = "";
    protected String disciplina = "";
    protected String imgName = "";
    protected Bitmap img;
    protected JSONObject disciplinaJObject;


    public Evento(){}

    public Evento setNid(int n){
        this.nid = n;
        return this;
    }

    public int getNid(){ return this.nid; }

    public Evento setTitle(String n){
        this.title = n;
        return this;
    }

    public String getTitle(){
        return this.title;
    }

    public Evento setBody(String n){
        this.body = n;
        return this;
    }

    public String getBody(){
        return this.body;
    }

    public Evento setImgUrl(String n){
        this.imgUrl = n;
        return this;
    }

    public String getImgUrl(){
        return this.imgUrl;
    }

    public Evento setAuthor(String n){
        this.author = n;
        return this;
    }

    public String getAuthor(){
        return this.author;
    }

    public Evento setImg(Bitmap n){
        this.img = n;
        return this;
    }

    public Bitmap getImg(){
        return this.img;
    }

    public Evento setImgName(String n){
        this.imgName = n;
        return this;
    }

    public String getImgName(){
        return this.imgName;
    }



    public Evento setFecha(String n){
        this.fecha = n;
        return this;
    }

    public String getFecha(){ return this.fecha; }

    public Evento setProvincia(String n){
        this.provincia = n;
        return this;
    }

    public String getProvincia(){ return this.provincia; }

    public Evento setCiudad(String n){
        this.ciudad = n;
        return this;
    }

    public String getCiudad(){ return this.ciudad; }

    public Evento setCalle(String n){
        this.calle = n;
        return this;
    }

    public String getCalle(){ return this.calle; }

    public Evento setCodigoPostal(String n){
        this.codigoPostal = n;
        return this;
    }

    public String getCodigoPostal(){ return this.codigoPostal; }

    public String getDireccion(){
        return ciudad + " (" + provincia + ") " + calle;
    }

    public Evento setPrecio(String n){
        this.precio = n;
        return this;
    }

    public String getPrecio(){ return this.precio; }



    public Evento setDisciplinaJObject(JSONObject n){
        this.disciplinaJObject = n;
        return this;
    }

    public JSONObject getDisciplinaJObject(){ return this.disciplinaJObject; }

    public Evento setDisciplina(String n){
        this.disciplina = n;
        return this;
    }

    public String getDisciplina(){ return this.disciplina; }

    public Evento setImgUri(String n){
        this.imgUri = n;
        return this;
    }

    public String getImgUri(){ return this.imgUri; }

}
