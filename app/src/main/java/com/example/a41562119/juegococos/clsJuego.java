package com.example.a41562119.juegococos;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;

import org.cocos2d.actions.base.Action;
import org.cocos2d.actions.instant.CallFuncN;
import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.actions.interval.MoveBy;
import org.cocos2d.actions.interval.MoveTo;
import org.cocos2d.actions.interval.ScaleBy;
import org.cocos2d.actions.interval.Sequence;
import org.cocos2d.layers.Layer;
import org.cocos2d.menus.Menu;
import org.cocos2d.menus.MenuItemImage;
import org.cocos2d.nodes.CocosNode;
import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Label;
import org.cocos2d.nodes.Scene;
import org.cocos2d.nodes.Sprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CCPoint;
import org.cocos2d.types.CCSize;

import java.net.PortUnreachableException;
import java.nio.channels.GatheringByteChannel;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 41562119 on 13/9/2016.
 */
public class
clsJuego {
    CCGLSurfaceView _VistaDelJuego;
    CCSize PantallaDelDispositivo;
    Context _ContextoDelJuego;
    boolean activo = false;
    float temp;
    int puntos;
    int Vidas = 3;
    Sprite naveJugador,naveEnemiga, ImagenFondo, BordeInferior, TuboPuntos;
    Label lblTitulo;
    ArrayList<Sprite> arrEnemigos;
    ArrayList<Sprite> arrTubos;
    public clsJuego(CCGLSurfaceView _VistaDelJuego){
        _VistaDelJuego=_VistaDelJuego;
    }

    public void ComenzarJuego(CCGLSurfaceView _VistaDelJuego, Context ContextoDelJuego){

        _ContextoDelJuego = ContextoDelJuego;
        Director.sharedDirector().attachInView(_VistaDelJuego);

        PantallaDelDispositivo=Director.sharedDirector().displaySize();


        //Ejecuto al escnea
        Director.sharedDirector().runWithScene(EscenaDelJuego());
    }

    private Scene EscenaDelJuego(){

        //Declaro la escena
        Scene EscenaADevolver;
        EscenaADevolver=Scene.node();

        //Declaro la capa de fondo
        CapaDeFondo MiCapaFondo;
        MiCapaFondo=new CapaDeFondo();

        //Declaro la capa contenedora
        CapaDeFrente MiCapaFrente;
        MiCapaFrente=new CapaDeFrente();

        CapaArriba MiCapaArriba;
        MiCapaArriba = new CapaArriba();

        //La capa del fondo mas atras y la del frente mas adelante
        EscenaADevolver.addChild(MiCapaFondo,-10);
        EscenaADevolver.addChild(MiCapaFrente,10);
        EscenaADevolver.addChild(MiCapaArriba,15);

        return EscenaADevolver;
    }
    class CapaArriba extends Layer{
        public CapaArriba(){
            PonerImagenArriba();
        }
        private void PonerImagenArriba(){
            BordeInferior=Sprite.sprite("BordeInferior.png");
            BordeInferior.setPosition(PantallaDelDispositivo.width/2,40);
            BordeInferior.runAction(ScaleBy.action(0.01f,2.5f,2.5f));
            super.addChild(BordeInferior);
        }
    }

    class CapaDeFondo extends Layer{
        public  CapaDeFondo()
        {
            PonerImagenFondo();

        }
        private void PonerImagenFondo(){
            ImagenFondo=Sprite.sprite("Fondo.png");
            ImagenFondo.setPosition(PantallaDelDispositivo.width/2,PantallaDelDispositivo.height/2);
            ImagenFondo.runAction(ScaleBy.action(0.01f,2.8f,2.5f));
            super.addChild(ImagenFondo);
        }


    }

    class CapaDeFrente extends Layer {

        public CapaDeFrente() {

            IniciarNave();
            ColocarLabel();
            //PonerBoton();

            arrEnemigos = new ArrayList<Sprite>();
            arrTubos = new ArrayList<Sprite>();

            Random random;
            random = new Random();
            int SpawnTimer = random.nextInt(10000);

            this.setIsTouchEnabled(true);

            MediaPlayer mpMusicaDeFondo;
            mpMusicaDeFondo=MediaPlayer.create(_ContextoDelJuego, R.raw.musica);
            mpMusicaDeFondo.start();
            mpMusicaDeFondo.setVolume(0.5f,0.5f);
            mpMusicaDeFondo.setLooping(true);

            TimerTask TareaPonerEnemigos;
            TareaPonerEnemigos = new TimerTask() {
                @Override
                public void run() {

                    PonerUnTuboArriba();
                    PonerUnTuboAbajo();
                    PonerCorrector();
                    ColocarLabel();
                }
            };
            // PonerUnTuboArriba();
            Timer RelojEnemigos;
            RelojEnemigos = new Timer();
            RelojEnemigos.schedule(TareaPonerEnemigos, 0, 1500);

            TimerTask TareaVerificarImpactos;
            TareaVerificarImpactos=new TimerTask() {
                @Override
                public void run() {
                    DetectarColisiones();
                    DetectarPuntos();
                    if (activo ==false)
                    {
                       // SecuenciaAbajo();
                        Gravedad();
                    }

                }
            };
            Timer RelojVerificarImpactos;
            RelojVerificarImpactos=new Timer();
            RelojVerificarImpactos.schedule(TareaVerificarImpactos,0,100);

        }


        private void IniciarNave() {

            naveJugador = Sprite.sprite("Bird.png");

            float PosicionIniciarX, PosicionIniciarY;
            PosicionIniciarX = PantallaDelDispositivo.width / 2;
            PosicionIniciarY = naveJugador.getHeight() / 2;
            naveJugador.runAction(ScaleBy.action(0.01f,1.8f,1.8f));
            naveJugador.setPosition(PantallaDelDispositivo.width/2 -150, PantallaDelDispositivo.height/2);
           // naveJugador.runAction(RotateTo.action(0.01f, 315f));
            super.addChild(naveJugador);
        }

        private void ColocarLabel() {
            lblTitulo = Label.label("Flappy Bird: " + puntos + " Vidas" + Vidas, "Verdana", 30);
            float AlturaTitulo;
            AlturaTitulo = lblTitulo.getHeight();
            lblTitulo.setPosition(PantallaDelDispositivo.width / 2, PantallaDelDispositivo.height - AlturaTitulo / 2);
            super.addChild(lblTitulo);
        }

        void PonerUnTuboArriba() {
            naveEnemiga = Sprite.sprite("TuboArriba.png");
            Random random;
            random = new Random();
            int num = (random.nextInt(400));

            CCPoint PosicionInicial, PosicionFinal;
            PosicionInicial = new CCPoint();
            float AlturaEnemigo, AnchoEnemigo;
            AlturaEnemigo = naveEnemiga.getHeight();
            AnchoEnemigo = naveEnemiga.getWidth();

            PosicionInicial.y = PantallaDelDispositivo.height - AlturaEnemigo/2 + num - 150;
            PosicionInicial.x = PantallaDelDispositivo.width + AnchoEnemigo / 2;
            temp = PosicionInicial.y;
            // PosicionInicial.x = random.nextInt((int) PantallaDelDispositivo.width - (int) AnchoEnemigo) + AnchoEnemigo / 2;
            //naveEnemiga.runAction(RotateTo.action(0.01f,0f));

            PosicionFinal = new CCPoint();
            PosicionFinal.x = -PosicionInicial.x;
            PosicionFinal.y = PantallaDelDispositivo.height - AlturaEnemigo/2 + num - 150;

            arrEnemigos.add(naveEnemiga);

            naveEnemiga.runAction(ScaleBy.action(0.01f,1.8f,1.8f));
            naveEnemiga.setPosition(PosicionInicial.x, PosicionInicial.y);

            naveEnemiga.runAction(MoveTo.action(6, PosicionFinal.x, PosicionFinal.y));
           // Secuencia();

            super.addChild(naveEnemiga);

            //PONER DOS TUBOS DIEFERENTES (ARRIBA Y ABAJO)
        }
        void PonerUnTuboAbajo() {
            naveEnemiga = Sprite.sprite("TuboAbajo.png");
            Random random;
            random = new Random();
            int num = (random.nextInt(400));

            CCPoint PosicionInicial, PosicionFinal;
            PosicionInicial = new CCPoint();
            float AlturaEnemigo, AnchoEnemigo;
            AlturaEnemigo = naveEnemiga.getHeight();
            AnchoEnemigo = naveEnemiga.getWidth();

            //PosicionInicial.y = PantallaDelDispositivo.height - AlturaEnemigo/2  - 150 -700;
            PosicionInicial.y = temp -900;
            PosicionInicial.x = PantallaDelDispositivo.width + AnchoEnemigo / 2;
            // PosicionInicial.x = random.nextInt((int) PantallaDelDispositivo.width - (int) AnchoEnemigo) + AnchoEnemigo / 2;
            //naveEnemiga.runAction(RotateTo.action(0.01f,0f));

            PosicionFinal = new CCPoint();
            PosicionFinal.x = -PosicionInicial.x;
            PosicionFinal.y = PosicionInicial.y;

            arrEnemigos.add(naveEnemiga);

            naveEnemiga.runAction(ScaleBy.action(0.01f,1.8f,1.8f));
            naveEnemiga.setPosition(PosicionInicial.x, PosicionInicial.y);

            naveEnemiga.runAction(MoveTo.action(6, PosicionFinal.x, PosicionFinal.y));
            // Secuencia();

            super.addChild(naveEnemiga);

            //PONER DOS TUBOS DIEFERENTES (ARRIBA Y ABAJO)
        }
        void PonerCorrector() {
            TuboPuntos = Sprite.sprite("Tubo.png");
            Random random;
            random = new Random();
            int num = (random.nextInt(400));

            CCPoint PosicionInicial, PosicionFinal;
            PosicionInicial = new CCPoint();
            float AlturaEnemigo, AnchoEnemigo;
            AlturaEnemigo = TuboPuntos.getHeight();
            AnchoEnemigo = TuboPuntos.getWidth();

            //PosicionInicial.y = PantallaDelDispositivo.height - AlturaEnemigo/2  - 150 -700;
            PosicionInicial.y = temp - 100 ;
            PosicionInicial.x = PantallaDelDispositivo.width + AnchoEnemigo / 2 + 50;
            // PosicionInicial.x = random.nextInt((int) PantallaDelDispositivo.width - (int) AnchoEnemigo) + AnchoEnemigo / 2;
            //naveEnemiga.runAction(RotateTo.action(0.01f,0f));

            PosicionFinal = new CCPoint();
            PosicionFinal.x = -PosicionInicial.x ;
            PosicionFinal.y = PosicionInicial.y;

            arrTubos.add(TuboPuntos);

            //TuboPuntos.runAction(ScaleBy.action(0.01f,1.8f,1.8f));

            TuboPuntos.setPosition(PosicionInicial.x, PosicionInicial.y);

            TuboPuntos.runAction(MoveTo.action(6, PosicionFinal.x, PosicionFinal.y));
            // Secuencia();

            super.addChild(TuboPuntos);

            //PONER DOS TUBOS DIEFERENTES (ARRIBA Y ABAJO)
        }

        boolean InterseccionEntreSprites(Sprite Sprite1, Sprite Sprite2) {
            boolean Devolver;
            Devolver = false;

            int Sprite1Izquierda, Sprite1Derecha, Sprite1Abajo, Sprite1Arriba;
            int Sprite2Izquierda, Sprite2Derecha, Sprite2Abajo, Sprite2Arriba;

            Sprite1Izquierda = (int) (Sprite1.getPositionX() - Sprite1.getWidth() / 2);
            Sprite1Derecha = (int) (Sprite1.getPositionX() + Sprite1.getWidth() / 2);
            Sprite1Abajo = (int) (Sprite1.getPositionY() - Sprite1.getHeight() / 2);
            Sprite1Arriba = (int) (Sprite1.getPositionY() + Sprite1.getHeight() / 2);

            Sprite2Izquierda = (int) (Sprite2.getPositionX() - Sprite2.getWidth() / 2);
            Sprite2Derecha = (int) (Sprite2.getPositionX() + Sprite2.getWidth() / 2);
            Sprite2Abajo = (int) (Sprite2.getPositionY() - Sprite2.getHeight() / 2);
            Sprite2Arriba = (int) (Sprite2.getPositionY() + Sprite2.getHeight() / 2);


            //Borde izq y borde inf de Sprite 1 está dentro de Sprite 2
            if (EstaEntre(Sprite1Izquierda, Sprite2Izquierda, Sprite2Derecha) &&
                    EstaEntre(Sprite1Abajo, Sprite2Abajo, Sprite2Arriba)) {

                Devolver = true;
            }

            //Borde izq y borde sup de Sprite 1 está dentro de Sprite 2
            if (EstaEntre(Sprite1Izquierda, Sprite2Izquierda, Sprite2Derecha) &&
                    EstaEntre(Sprite1Arriba, Sprite2Abajo, Sprite2Arriba)) {

                Devolver = true;
            }

            //Borde der y borde sup de Sprite 1 está dentro de Sprite 2
            if (EstaEntre(Sprite1Derecha, Sprite2Izquierda, Sprite2Derecha) &&
                    EstaEntre(Sprite1Arriba, Sprite2Abajo, Sprite2Arriba)) {

                Devolver = true;
            }

            //Borde der y borde inf de Sprite 1 está dentro de Sprite 2
            if (EstaEntre(Sprite1Derecha, Sprite2Izquierda, Sprite2Derecha) &&
                    EstaEntre(Sprite1Abajo, Sprite2Abajo, Sprite2Arriba)) {

                Devolver = true;
            }

            //Borde izq y borde inf de Sprite 2 está dentro de Sprite 1
            if (EstaEntre(Sprite2Izquierda, Sprite1Izquierda, Sprite1Derecha) &&
                    EstaEntre(Sprite2Abajo, Sprite1Abajo, Sprite1Arriba)) {

                Devolver = true;
            }

            //Borde izq y borde sup de Sprite 1 está dentro de Sprite 1
            if (EstaEntre(Sprite2Izquierda, Sprite1Izquierda, Sprite1Derecha) &&
                    EstaEntre(Sprite2Arriba, Sprite1Abajo, Sprite1Arriba)) {

                Devolver = true;
            }

            //Borde der y borde sup de Sprite 2 está dentro de Sprite 1
            if (EstaEntre(Sprite2Derecha, Sprite1Izquierda, Sprite1Derecha) &&
                    EstaEntre(Sprite2Arriba, Sprite1Abajo, Sprite1Arriba)) {

                Devolver = true;
            }

            //Borde der y borde inf de Sprite 2 está dentro de Sprite 1
            if (EstaEntre(Sprite2Derecha, Sprite1Izquierda, Sprite1Derecha) &&
                    EstaEntre(Sprite2Abajo, Sprite1Abajo, Sprite1Arriba)) {

                Devolver = true;
            }

            return Devolver;

        }
        boolean EstaEntre(int NumeroAComparar, int NumeroMenor, int NumeroMayor){
            boolean Devolver;

            if (NumeroMenor>NumeroMayor){
                int auxiliar;
                auxiliar=NumeroMayor;
                NumeroMayor=NumeroMenor;
                NumeroMenor=auxiliar;
            }
            if(NumeroAComparar>=NumeroMenor && NumeroAComparar <= NumeroMayor){
                Devolver= true;
            }else{
                Devolver=false;
            }
            return  Devolver;
        }

        void DetectarColisiones(){
            boolean HuboColision;
            HuboColision=false;
            for(Sprite UnEnemigoAVerificar: arrEnemigos){
                if(InterseccionEntreSprites(naveJugador,UnEnemigoAVerificar)){
                    HuboColision=true;
                    UnEnemigoAVerificar.setPosition(UnEnemigoAVerificar.getPositionX(),UnEnemigoAVerificar.getPositionY()-500);
                }
            }
            if(HuboColision==true){
                Log.d("DetectarColision","Hubo Colision");
                Vidas--;
                if (Vidas == 0)
                {
                    PonerBoton();
                    activo = true;
                }
            }else {
                Log.d("DetectarColision","NO Hubo Colision");
            }
        }
        void DetectarPuntos(){
            boolean HuboPuntos;
            HuboPuntos=false;
            for(Sprite UnEnemigoAVerificar: arrTubos){
                if(InterseccionEntreSprites(naveJugador,UnEnemigoAVerificar)){
                    HuboPuntos=true;

                    UnEnemigoAVerificar.setRotation(40);

                    puntos++;
                }
            }
            if(HuboPuntos==true){
                Log.d("DetectarColision","PUNTO");

            }else {
               // Log.d("DetectarColision","NO Hubo Colision");
            }
        }

        @Override
            public boolean ccTouchesBegan(MotionEvent event){

            activo = true;
            //SecuenciaArriba();
            //MoverNaveJugador(event.getX(),PantallaDelDispositivo.getHeight() - event.getY());
            Salto();
            return true;
        }
        @Override
        public boolean ccTouchesEnded(MotionEvent event){

            activo = false;
            //MoverNaveJugador(event.getX(),PantallaDelDispositivo.getHeight() - event.getY());
            return true;
        }
        void MoverNaveJugador(float DestinoX, float DestinoY){
            float MovimientoHorizontal, MoviemientoVertical, SuavizadotDeMoviemiento;
            MovimientoHorizontal = DestinoX -PantallaDelDispositivo.getWidth()/2;
            MoviemientoVertical = DestinoY - PantallaDelDispositivo.getHeight()/2;

            SuavizadotDeMoviemiento=20;
            MovimientoHorizontal=MovimientoHorizontal/SuavizadotDeMoviemiento;
            MoviemientoVertical=MoviemientoVertical/SuavizadotDeMoviemiento;

            float PosicionFinalX=naveJugador.getPositionX()+MovimientoHorizontal;
            float PosicionFinalY=naveJugador.getPositionY()+MoviemientoVertical;

            if (PosicionFinalX<naveJugador.getWidth()/2){
                PosicionFinalX=naveJugador.getWidth()/2;
            }
            if (PosicionFinalX>PantallaDelDispositivo.getWidth()-naveJugador.getWidth()/2){
                PosicionFinalX=PantallaDelDispositivo.getWidth()-naveJugador.getWidth()/2;
            }
            if (PosicionFinalY<naveJugador.getHeight()/2){
                PosicionFinalY=naveJugador.getHeight()/2;
            }
            if (PosicionFinalY>PantallaDelDispositivo.getHeight()-naveJugador.getHeight()/2){
                PosicionFinalY=PantallaDelDispositivo.getHeight()-naveJugador.getHeight()/2;
            }
            naveJugador.setPosition(PosicionFinalX,PosicionFinalY);
        }

        void PonerBoton(){
            MenuItemImage BotonDisparo, BotonPausa;
            BotonDisparo=MenuItemImage.item("botonDisparo.png","botonDisparoPresionado.png", this,"PresionaBotonDisparo");

            float PosicionBotonDisparoX, PosicionBotonDisparoY;
            PosicionBotonDisparoX=BotonDisparo.getWidth();
            PosicionBotonDisparoY=BotonDisparo.getHeight();
            BotonDisparo.setPosition(PosicionBotonDisparoX,PosicionBotonDisparoY);

            BotonPausa=MenuItemImage.item("botonPausa.png","botonPausaPresionado.png", this,"PresionaBotonPausa");

            float PosicionBotonPausaX, PosicionBotonPausaY;
            PosicionBotonPausaX=PantallaDelDispositivo.width/2;
            PosicionBotonPausaY=PantallaDelDispositivo.height/2;
            BotonPausa.setPosition(PosicionBotonPausaX,PosicionBotonPausaY);

            Menu MenuDeBotones;
            MenuDeBotones=Menu.menu(BotonDisparo,BotonPausa);
            MenuDeBotones.setPosition(0,0);
            super.addChild(MenuDeBotones);
        }
        public void PresionaBotonDisparo(){

        }
        public void PresionaBotonPausa(){
            naveJugador.setPosition(PantallaDelDispositivo.width/2,naveJugador.getPositionY());
            activo = false;
        }
        public void SecuenciaAbajo(){
            MoveBy IrHaciaAbajo, IrHaciaArriba, IrHaciaDerecha;
            IrHaciaAbajo=MoveBy.action(1,0,-50);
            IrHaciaArriba=MoveBy.action(1,0,300);
            IrHaciaDerecha=MoveBy.action(1,300,0);

            CallFuncN FinDelMovimiento;
            FinDelMovimiento=CallFuncN.action(this,"FinDelTrayecto");
            IntervalAction secuancia;
            secuancia= Sequence.actions(IrHaciaAbajo,FinDelMovimiento);

            naveJugador.runAction(secuancia);
            super.addChild(naveJugador);
        }
        public void SecuenciaArriba(){
            MoveBy IrHaciaAbajo, IrHaciaArriba, IrHaciaDerecha;
            IrHaciaAbajo=MoveBy.action(1,0,-300);
            IrHaciaArriba=MoveBy.action(1,0,150);
            IrHaciaDerecha=MoveBy.action(1,300,0);


            CallFuncN FinDelMovimiento;
            FinDelMovimiento=CallFuncN.action(this,"FinDelTrayecto");
            IntervalAction secuancia;
            secuancia= Sequence.actions(IrHaciaArriba,FinDelMovimiento);

            naveJugador.runAction(secuancia);
            super.addChild(naveJugador);
        }
        public void FinDelTrayecto(CocosNode ObjectoLlamador){
         super.removeChild(ObjectoLlamador,true);
            arrEnemigos.remove(ObjectoLlamador);
        }
        public void Gravedad()
        {
            naveJugador.setPosition(naveJugador.getPositionX(),naveJugador.getPositionY()-15);
        }
        public void Salto()
        {
            naveJugador.setPosition(naveJugador.getPositionX(),naveJugador.getPositionY()+35);
        }

    }
}
