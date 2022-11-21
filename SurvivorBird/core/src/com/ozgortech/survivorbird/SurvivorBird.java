package com.ozgortech.survivorbird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;

import java.util.Random;

//Şimdi öncelikle şunu belirtmek istiyorum bu konu tamamen bir dış kütüphanenin(libgdx)
//Özelliklerine bağlı kalınarak işlenen bir konu o nedenle benim de ileride libgdx ile
//oyun yapma niyetim pek olmadığı için çok da ayrıntılı anlatmayabilirim
public class SurvivorBird extends ApplicationAdapter {
	SpriteBatch batch; // Spritebatch sprite yığını diye çevirisi olan bir obje,
	//bütün spritelar bunda işlem görüyor gibi düşünebiliriz
	Texture background,bird; // görsellerimi texture olarak veriyoruz
	Texture bee;
	float birdX,birdY;
	int gameState = 0; // gameState 0 olması oyun başlamamış anlamında 1 olması oyun başlamış
	// anlamında kullanalım dedik
	float velocity = 2;
	float gravity = 0.3f;
	int numberOfEnemySets = 3;
	float beeX[] = new float[numberOfEnemySets];
	float beeY1[] = new float[numberOfEnemySets];
	float beeY2[] = new float[numberOfEnemySets];
	float beeY3[] = new float[numberOfEnemySets];

	float distance; // setler arası mesafe

	Circle birdCircle;
	Circle[] beeCircles1;
	Circle[] beeCircles2;
	Circle[] beeCircles3;

	//ShapeRenderer shapeRenderer;

	int score = 0;
	boolean hop = false;

	BitmapFont font,font2;

	@Override
	public void create () {
		//Create'i onCreate'e ya da unity'de ki start methoduna benzetebiliriz.
		batch = new SpriteBatch();
		background = new Texture("background.png");// texture'ın kaynağını belirtiyoruz
		bird = new Texture("bird.png");
		bee = new Texture("bee.png");
		birdX = Gdx.graphics.getWidth()/3;
		birdY = Gdx.graphics.getHeight()/3;

		birdCircle = new Circle();
		beeCircles1 = new Circle[numberOfEnemySets];
		beeCircles2 = new Circle[numberOfEnemySets];
		beeCircles3 = new Circle[numberOfEnemySets];
		// Circle'ları collider gibi düşünebiliriz. Bu circle'ları objelerin tam üzerine çizdiriyoruz

		//shapeRenderer = new ShapeRenderer();
		// Circle'lar gözle görünmediği için shapeRendererlar ile test ama.çlı onların
		// oldukları yeri çizebiliriz ama oyun hazır olduğunda kapatıyoruz.

		distance = Gdx.graphics.getWidth()/2; // setler arasında ekran boyutunun yarısı kadar
		// mesafe olsun dedik

		for (int i = 0; i < numberOfEnemySets; i++) {
			beeX[i] = Gdx.graphics.getWidth() + i*distance;

			beeY1[i] = Gdx.graphics.getHeight() * new Random().nextFloat()* 0.8f;
			beeY2[i] = Gdx.graphics.getHeight() * new Random().nextFloat()* 0.8f;
			beeY3[i] = Gdx.graphics.getHeight() * new Random().nextFloat()* 0.8f;

			beeCircles1[i] = new Circle();
			beeCircles2[i] = new Circle();
			beeCircles3[i] = new Circle();
		}
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(4);

		font2 = new BitmapFont();
		font2.setColor(Color.WHITE);
		font2.getData().setScale(6);
	}

	@Override
	public void render () {
		//render'ı ise unity'deki update metoduna benzetebiliriz.

		batch.begin(); // batch begin ile end arasına batch işlemlerimizi yani bir nevi
		// çizdirme işlemlerimizi yapıyoruz
		batch.draw(background,0,0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		// arkaplanı çizdirmek için çizilecek texture'ı , başlangıç olarak hangi piksellerden
		// çizilmeye başlanacağını(0,0 ekranın en sol alt köşesi oluyor), ve hangi boyutta
		// çizmek istediğimizi söylüyoruz(burada ekranın tamamını kaplasın dedik ekran boyutlarını
		// vererek)
		if (gameState == 0){
			if (Gdx.input.justTouched()){//dokunmayı algılayan method
				//eğer oyun başlamamışsa dokununca başlasın diyoruz
				gameState = 1;
			}
		}else if(gameState == 1){

			for (int i = 0; i < numberOfEnemySets; i++) {

				// Setler halinde 3 er arı oluşturuyoruz, y konumlarını randomize ettik,
				//X konumları ise set içindeki her bir arının aynı ancak setler arasında
				// distance dediğimiz kadar mesafe var.
				batch.draw(bee,beeX[i],beeY1[i],
						Gdx.graphics.getWidth()/15,Gdx.graphics.getHeight()/10);
				batch.draw(bee,beeX[i],beeY2[i],
						Gdx.graphics.getWidth()/15,Gdx.graphics.getHeight()/10);
				batch.draw(bee,beeX[i],beeY3[i],
						Gdx.graphics.getWidth()/15,Gdx.graphics.getHeight()/10);
				beeX[i] -= 5; // arıları sola doğru kaydır

				beeCircles1[i].set(beeX[i]+Gdx.graphics.getWidth()/30,
						beeY1[i]+Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth()/30);
				beeCircles2[i].set(beeX[i]+Gdx.graphics.getWidth()/30,
						beeY2[i]+Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth()/30);
				beeCircles3[i].set(beeX[i]+Gdx.graphics.getWidth()/30,
						beeY3[i]+Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth()/30);

				if (birdY < 0){ // kuş ekranın altına düşmüşse oyun bitsin
					gameState = 2;
				}else if (birdY > Gdx.graphics.getHeight() - Gdx.graphics.getHeight()/10){
					//Eğer kuş ekranın tepesine gelmişse dikey hızını sıfırla
					velocity = 0;
				}
				if (beeX[i] < birdX && hop == false){
					score++;
					hop = true;
					System.out.println(score);
					// mevcut arının x'i kuşun x inden düşük hale gelmişse skoru 1 artır
				}

				if (beeX[i] < 0){
					// eğer setteki arıların konumu 0 dan az olacak hale gelmişse başa gelsinler
					// diyoruz
					beeX[i] = Gdx.graphics.getWidth() + distance;
					beeY1[i] = Gdx.graphics.getHeight() * new Random().nextFloat()* 0.8f;
					beeY2[i] = Gdx.graphics.getHeight() * new Random().nextFloat()* 0.8f;
					beeY3[i] = Gdx.graphics.getHeight() * new Random().nextFloat()* 0.8f;
					hop = false;
				}
			}

			//he eğer oyun başlamışsa kuşumuzun y konumu velocity kadar azalsın, ayrıca velocity de
			// sürekli hafif hafif artsın
			velocity += gravity;
			birdY -= velocity;
			if (Gdx.input.justTouched()){
				// dokunduğumuzda kuşumuz yükselsin diye ters bir hız verelim dedik
				if (birdY < Gdx.graphics.getHeight() - Gdx.graphics.getHeight()/10){
					velocity = -10;
				}

			}
		}else if (gameState == 2){
			score = 0;
			font2.draw(batch,"Game Over, Tap to Play Again",Gdx.graphics.getWidth()/4,
					Gdx.graphics.getHeight()/2);
			birdY = Gdx.graphics.getHeight()/3;
			for (int i = 0; i < numberOfEnemySets; i++) {
				beeX[i] = Gdx.graphics.getWidth() + i*distance;

				beeY1[i] = Gdx.graphics.getHeight() * new Random().nextFloat()* 0.8f;
				beeY2[i] = Gdx.graphics.getHeight() * new Random().nextFloat()* 0.8f;
				beeY3[i] = Gdx.graphics.getHeight() * new Random().nextFloat()* 0.8f;

				beeCircles1[i] = new Circle();
				beeCircles2[i] = new Circle();
				beeCircles3[i] = new Circle();
				velocity = 2;

				if (Gdx.input.justTouched()){//dokunmayı algılayan method
					//eğer oyun bitmişse dokununca tekrar başlasın diyoruz
					gameState = 1;
				}
			}
		}



		batch.draw(bird,birdX,birdY,Gdx.graphics.getWidth()/15,Gdx.graphics.getHeight()/10);
		// yine kuşun çizimi de aynı sadece kuşun konumu değişebileceği için bir değişkende tuttuk
		// x ve y konumunu ve boyutuna ekranın boyutuna göre makul bi boyut verelim dedik

		font.draw(batch,score+"",Gdx.graphics.getWidth()/10,Gdx.graphics.getHeight()/4);
		// skoru yazdır


		batch.end();

		birdCircle.set(birdX + Gdx.graphics.getWidth()/30,
				birdY+Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth()/30);

		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.BLACK);
		//shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);

		for (int i = 0; i < numberOfEnemySets; i++) {
			//shapeRenderer.circle(beeCircles1[i].x,beeCircles1[i].y,beeCircles1[i].radius);
			//shapeRenderer.circle(beeCircles2[i].x,beeCircles2[i].y,beeCircles2[i].radius);
			//shapeRenderer.circle(beeCircles3[i].x,beeCircles3[i].y,beeCircles3[i].radius);

			if(Intersector.overlaps(birdCircle,beeCircles1[i]) ||
			Intersector.overlaps(birdCircle, beeCircles2[i]) ||
			Intersector.overlaps(birdCircle,beeCircles3[i])){
				gameState = 2;
				// kuş arılarla temas ederse oyunu bitir
			}
		}
		//shapeRenderer.end();
	}
	
	@Override
	public void dispose () {

	}
}
