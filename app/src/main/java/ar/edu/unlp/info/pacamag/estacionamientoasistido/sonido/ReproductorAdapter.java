package ar.edu.unlp.info.pacamag.estacionamientoasistido.sonido;

import android.content.Context;
import android.media.MediaPlayer;

import ar.edu.unlp.info.pacamag.estacionamientoasistido.R;

public class ReproductorAdapter {
     private MediaPlayer reproductor;

    public ReproductorAdapter(Context context) {
    }

    void ReproductorAdapter(Context context){
        reproductor = MediaPlayer.create(context, R.raw.freq1);
        reproductor.setLooping(true);
    }

     public void ReproducirSonido(int tono){
        reproductor.setAudioSessionId(tono);
        reproductor.start();
     }

     public void DetenerSonido(){
        reproductor.stop();
     }

     public void LiberarReproductor(){
        reproductor.release();
     }

     public boolean EstaSonando(){
        return reproductor.isPlaying();
     }

}
