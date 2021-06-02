package com.ohata.tap.birds.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

public class FelipeN190560AMGFINAL extends ApplicationAdapter {
	//Texturas
	private  SpriteBatch batch;
	private Texture[] passaro;
	private  Texture fundo;
	private  Texture canoTopo;
	private  Texture canoBaixo;
	private  Texture gameover;
	private  Texture logo;
	private  Texture gold;
	private  Texture silver;

	//movimentação
	private  float variacao = 0;
	private  int gravidade = 0;
	private  int pontos = 0;
	private  int melhorponto = 0;
	private float posicaoInicialVerticalPassaro = 0;
	private float posicaoInicialHorizontalPassaro = 0;
	private Random random;
	private  float goldorSilver;
	private int estadoJogo = 0;

	//Colisão
	private ShapeRenderer shapeRenderer;
	private Circle circuloPassaro;
	private Rectangle retanguloCanoCima;
	private Rectangle retanguloCanoBaixo;
	private Circle circuloSilver;
	private Circle circuloGold;

	//Tela
	private  float larguraDispositivo;
	private  float alturaDispositivo;
	private  float posicaoCanoshorizontal;
	private float posicaoCanosVertical;
	private  float espacoEntreCanos;
	private  float posicaoMoedahorizontal;
	private float posicaoMoedaVertical;


	//Textos
	BitmapFont textoPontuacao;
	BitmapFont textoReiniciar;
	BitmapFont textoMelhorPontuaçao;
	private boolean passouCano = false;
	private boolean PegouMoeda = false;
	Preferences preferencias;

	//Sons
	Sound somVoando;
	Sound somColisao;
	Sound somPontuacao;
	Sound somMoeda;

	@Override
	public void create () {
		inicializarTexturas();
		inicializarObjetos();

	}

	private void inicializarTexturas() {

		//Sprites do meu passaro
		passaro = new Texture[5];
		passaro[0] = new Texture("Bird_Fly01.png");
		passaro[1] = new Texture("Bird_Fly02.png");
		passaro[2] = new Texture("Bird_Fly03.png");
		passaro[3] = new Texture("Bird_Stand01.png");
		passaro[4] = new Texture("Bird_Stand02.png");

		//inicia o sprite do meu fundo
		fundo = new Texture("fundo.png");

		//inicia o sprite dos canos
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");
		gameover = new Texture("game_over.png");
		logo = new Texture("BirdLOGO.png");
		silver = new Texture("Coin_Silver.png");
		gold = new Texture("Coin_Gold.png");

	}

	private void inicializarObjetos() {

		batch = new SpriteBatch();
		random = new Random();

		//escalona meus objs
		larguraDispositivo = Gdx.graphics.getWidth();
		alturaDispositivo = Gdx.graphics.getHeight();
		posicaoInicialVerticalPassaro = alturaDispositivo / 2;
		posicaoCanoshorizontal = larguraDispositivo;
		espacoEntreCanos = 350;

		//mostra minha pontuação
		textoPontuacao = new BitmapFont();
		textoPontuacao.setColor(com.badlogic.gdx.graphics.Color.WHITE);
		textoPontuacao.getData().setScale(10);

		//mostra minha melhor pontuação
		textoMelhorPontuaçao = new BitmapFont();
		textoMelhorPontuaçao.setColor(com.badlogic.gdx.graphics.Color.GREEN);
		textoMelhorPontuaçao.getData().setScale(2);

		//mostra o reiniciar
		textoReiniciar = new BitmapFont();
		textoReiniciar.setColor(com.badlogic.gdx.graphics.Color.RED);
		textoReiniciar.getData().setScale(2);

		//inicializa minhas colisões
		shapeRenderer = new ShapeRenderer();
		circuloPassaro = new Circle();
		retanguloCanoCima = new Rectangle();
		retanguloCanoBaixo = new Rectangle();
		circuloGold = new Circle();
		circuloSilver = new Circle();


		//inicializa meus Sons
		somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));
		somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
		somMoeda = Gdx.audio.newSound(Gdx.files.internal("coins.wav"));

		//ponturação Máxima
		preferencias = Gdx.app.getPreferences("flappybird");
		melhorponto = preferencias.getInteger("melhorponto", 0);


	}

	//desenha meus componenstes na tela
	@Override
	public void render () {

		verificarEstadoJogo();
		validarPontos();
		desenharTexturas();
		detectarColisao();



	}

	private void verificarEstadoJogo() {

		boolean toqueTela = Gdx.input.justTouched();

		if (estadoJogo == 0) {
			//joga o passaro pra cima e muda meu estado
			posicaoMoedahorizontal = larguraDispositivo + larguraDispositivo /2;

			if (Gdx.input.justTouched()) {
				gravidade = -25;
				estadoJogo = 1;
				somVoando.play();
			}
		} else if (estadoJogo == 1) {

			//identifica o toque do meu jogador
			if (Gdx.input.justTouched()) {
				somVoando.play();
				gravidade = -20;

			}
			//Realiza a movimentação dos canos na horizontal
			posicaoCanoshorizontal -= Gdx.graphics.getDeltaTime() * 200;
			if (posicaoCanoshorizontal < -canoBaixo.getWidth()) {
				posicaoCanoshorizontal = larguraDispositivo;
				posicaoCanosVertical = random.nextInt(400) - 200;
				passouCano = false;
			}
			//Realiza a movimentação da moeda na horizontal
			posicaoMoedahorizontal -= Gdx.graphics.getDeltaTime() * 200;
			if (posicaoMoedahorizontal < -gold.getWidth()) {
				posicaoMoedahorizontal = larguraDispositivo + larguraDispositivo /2;
				posicaoMoedaVertical = random.nextInt(500) - 300;
				goldorSilver = random.nextInt(100) - 10;
				PegouMoeda = false;
			}

			//ativa a gravidade no meu passaro
			if (posicaoInicialVerticalPassaro > 0 || toqueTela)
				posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;

			gravidade++;
		} else if (estadoJogo == 2) {

			//seta minha melhor pontuação
			if (melhorponto < pontos) {
				melhorponto = pontos;
				preferencias.putInteger("melhorponto", melhorponto);
			}

			//bate meu passaro no cano e joga ele pra traz
			posicaoInicialHorizontalPassaro -= Gdx.graphics.getDeltaTime() * 500;

			//reinicia meu jogo
			if (toqueTela) {
				estadoJogo = 0;
				pontos = 0;
				gravidade = 0;
				posicaoInicialHorizontalPassaro = 0;
				posicaoInicialVerticalPassaro = alturaDispositivo / 2;
				posicaoCanoshorizontal = larguraDispositivo;
				posicaoMoedahorizontal = larguraDispositivo + larguraDispositivo /2;


			}


		}
	}

	private void validarPontos() {

		if(posicaoCanoshorizontal < 50 - passaro[0].getHeight()){
			if (!passouCano)
			{
				pontos++;
				passouCano = true;
				somPontuacao.play();
			}

		}
		//valida o ponto da minha moeda
		if ( PegouMoeda)
		{
			posicaoMoedahorizontal = larguraDispositivo + larguraDispositivo /2;
			posicaoMoedaVertical = random.nextInt(500) - 300;

			if(goldorSilver < 30 ){
				pontos += 5;
			}else {
				pontos += 10;
			}
			somMoeda.play();
			goldorSilver = random.nextInt(100) - 10; //troca o moeda entre gold e silver
			PegouMoeda = false;
		}

		//anima meu passaro
		variacao += Gdx.graphics.getDeltaTime() * 10;


		if (estadoJogo == 0) {

			if (variacao > 5)
				variacao = 3;
		}
		else {
			if (variacao > 3)
				variacao = 0;
		}


	}

	private void desenharTexturas() {
		batch.begin();

		//posiciona meu fundo
		batch.draw(fundo, 0,0,larguraDispositivo,alturaDispositivo);

		//posiciona meus sprites
		batch.draw(passaro[(int)variacao], 50 + posicaoInicialHorizontalPassaro, posicaoInicialVerticalPassaro);
		batch.draw(canoBaixo, posicaoCanoshorizontal, alturaDispositivo/2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanosVertical);
		batch.draw(canoTopo, posicaoCanoshorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + posicaoCanosVertical);

		if(goldorSilver > 30 )
		{
			batch.draw(gold, posicaoMoedahorizontal, alturaDispositivo / 2 + posicaoMoedaVertical);

		}else
		{
			batch.draw(silver, posicaoMoedahorizontal, alturaDispositivo / 2 + posicaoMoedaVertical);
		}

		textoPontuacao.draw(batch, String.valueOf(pontos), larguraDispositivo /2, alturaDispositivo - 100);

		//TELA de inicio de jogo
		if (estadoJogo == 0) {
			batch.draw(logo, larguraDispositivo / 2 - logo.getWidth() / 2, alturaDispositivo / 2 - logo.getHeight() / 2);
			textoReiniciar.draw(batch, "TOQUE NA TELA PARA INICIAR!", larguraDispositivo / 2 - 250, alturaDispositivo / 2 - logo.getWidth() / 2);
		}


		//TELA de gameover
		if (estadoJogo == 2)
		{


			batch.draw(gameover, larguraDispositivo / 2 - gameover.getWidth() / 2, alturaDispositivo / 2);
			textoReiniciar.draw(batch, "TOQUE NA TELA PARA REINICIAR!", larguraDispositivo / 2 - 250, alturaDispositivo / 2 - gameover.getWidth() / 2);
			textoMelhorPontuaçao.draw(batch, "SUA MELHOR PONTUAÇÃO É: " + melhorponto + " PONTOS", larguraDispositivo / 2 - 300, alturaDispositivo / 2 - gameover.getWidth() / 4);
		}

		batch.end();
	}

	private void detectarColisao() {
		//desenha minhas colisões

		circuloPassaro.set(50 + passaro[0].getWidth() / 2, posicaoInicialVerticalPassaro + passaro[0].getHeight() / 2, passaro[0].getWidth() / 2);

		retanguloCanoBaixo.set(posicaoCanoshorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanosVertical, canoBaixo.getWidth(), canoBaixo.getHeight());
		retanguloCanoCima.set(posicaoCanoshorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + posicaoCanosVertical, canoTopo.getWidth(), canoTopo.getHeight());



		circuloGold.set(posicaoMoedahorizontal, alturaDispositivo / 2 + posicaoMoedaVertical + gold.getHeight() / 2, gold.getWidth() / 2);

			//	circuloSilver.set(posicaoMoedahorizontal, alturaDispositivo / 2 + posicaoMoedaVertical + silver.getHeight() / 2, silver.getWidth() / 2);



		//detecta colisão
		boolean colisaoCanoCima = Intersector.overlaps(circuloPassaro,retanguloCanoCima);
		boolean colisaoCanoBaixo = Intersector.overlaps(circuloPassaro,retanguloCanoBaixo);
		//boolean colisaoSilver = Intersector.overlaps(circuloPassaro,circuloSilver);
		boolean colisaoGold = Intersector.overlaps(circuloPassaro,circuloGold);



		//Indentifica minha colição
		if(colisaoGold){
			if(!PegouMoeda)
			{
				PegouMoeda = true;

			}



		}


		if (colisaoCanoBaixo || colisaoCanoCima)
		{
			//Gdx.app.log("Log", "Bateu");
			if (estadoJogo == 1)
			{
				somColisao.play();
			}

			estadoJogo = 2;
		}
	}

	@Override
	public void dispose () {

	}

}

