package com.example.a41562119.juegococos;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import org.cocos2d.opengl.CCGLSurfaceView;

public class MainActivity extends Activity {

    CCGLSurfaceView VistaPrincipal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        VistaPrincipal = new CCGLSurfaceView(this);
        setContentView(VistaPrincipal);
    }

    protected void onStart(){
        super.onStart();
        clsJuego MiJuego;
        MiJuego=new clsJuego(VistaPrincipal);
        MiJuego.ComenzarJuego(VistaPrincipal, this);

    }

}
