package com.neurondigital.gravityflip;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.neurondigital.nudge.BackgroundRoller;
import com.neurondigital.nudge.Button;
import com.neurondigital.nudge.Instance;
import com.neurondigital.nudge.ObjectManager;
import com.neurondigital.nudge.Physics;
import com.neurondigital.nudge.Screen;
import com.neurondigital.nudge.Share;
import com.neurondigital.nudge.SingleScore;
import com.neurondigital.nudge.Sprite;

public class MainGame extends Screen {

	//paints
	Paint background_shader = new Paint();
	Paint Title_Paint = new Paint();
	Paint SubTitle_Paint = new Paint();
	Paint Score_Paint = new Paint();
	Paint Instruction_Paint = new Paint();
	Paint Instruction_Paint2 = new Paint();
	Paint floor_paint = new Paint();
	Paint star_paint = new Paint();
	Paint building_paint = new Paint();
	Paint sky_paint = new Paint();
	Paint play_stroke = new Paint();
	Paint play_paint = new Paint();
	Paint GameOver_Paint = new Paint();

	//background
	Sprite background_buildings_top, background_buildings_bottom;
	BackgroundRoller background_top, background_bottom;
	Sprite robotMenu, boardMenu, moon;
	Instance moon_small;

	Instance robot;

	//physics
	Physics physics = new Physics();

	//states
	final int MENU = 0, GAMEPLAY = 1, GAMEOVER = 2;
	int state = MENU;
	boolean pause = false, notstarted = true;

	//menu buttons
	Button btn_Play, btn_Highscores, btn_Exit, btn_Home, btn_share, btn_Replay, btn_sound_mute, btn_music_mute, btn_pause, btn_rate, btn_continue;
	Sprite play_btn_sprite, pause_btn_sprite;

	//score
	int score = 0;
	Sprite score_cup;
	SingleScore.Highscore topScore;

	//sound
	SoundPool sp;
	MediaPlayer music;
	int sound_click, sound_flip, sound_gameover, sound_coin;
	boolean sound_muted = false, music_muted = false;
	Sprite sound_on, sound_off, music_on, music_off;

	//ad
	private InterstitialAd interstitial;
	int ad_counter = 0;
	AdRequest adRequest;

	//game over counter
	int gameover_counter = 0;
	boolean game_over = false;

	// floor generator
	FloorGenerator bottom_floorGenerator, top_floorGenerator;

	//item generator
	ObjectManager itemGenerator;
	ObjectManager BottomJunkGenerator, TopJunkGenerator;
	final int spike = 0, coin = 1;

	//TODO: variables you can change to control game speed, delays...
	int gameover_delay = 20;
	int initial_gravity = -700, gravity;
	int floor_height = 15;
	int building_height = 50;
	float building_scale = 0.6f;
	int robot_speed = 15;

	//floor design
	final int __ = 0, b0 = 1, b1 = 2, b2 = 3, t0 = 4, t1 = 5, t2 = 6, board_top = 7, board_bot = 8;
	//TODO: change the floor design from the 2 arrays below. 
	//t0 means top floor left edge, t1 is the center piece, t2 is the right piece. b0 means bottom floor left edge, b1 is the center piece, b2 is the right piece. __ represents a space between bars.
	//These 2 arrays repreent the top and bottom floors respectivly. When the player reaches the end the floor is looped forever.
	int[] top_tile_list = new int[] { __, __, __, __, __, __, __, __, __, __, __, __, t0, t1, t1, t1, t1, t1, t1, t1, t1, t1, t2, __, __, __, __, t0, t1, t1, t1, t1, t2, __, __, __, __, __, t0, t1, t1, t1, t2, __, __, __, __, board_top, __, __, __, __, t0, t1, t1, t1, t2, __, __, __, __, t0, t1, t1, t2, __, __, __, __, t0, t1, t2, __, __, __, __, board_top, __, __, __, __, board_top, __, __, __, __, __, __, t0, t1, t1, t2, __, __, __, __, __, board_top, __, __, __, __, __, __, __, __, __, __, board_top, t1, t1, t1, t1, t1, t1, t1, t1, t2, __, __, __, __, __, board_top, __, __, __, __, __, board_top, __, __, __, __, __, board_top, __, __, __, __, t0, t1, t1, t1, t1, t1, t1, t1, t1, t1, t1, t2, __, __, __, __, __, __, __, t0, t1, t1, t1, t1, t1, t1, t1, t2, __, __, __, __, __, board_top, __, __, __, __, __, __, board_top, __, __, __, __, __, __, __, board_top, board_top, __, __, __, __, __, t0, t1, t2, __, __, __, __, t0, t1, t2, __, __, __, __, __, t0, t1, t2, __, __, __, __, t0, t1, t2, __, __, __, __, __, __, __, t0, t1, t1, t2, __, __, __, __, __, __, __, board_top, __, __, __, __, __, __, __, __, __, t1, t1, t1, t1, __, __ };//13 board top
	int[] bottom_tile_list = new int[] { b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b2, __, __, __, __, __, b0, b1, b1, b1, b1, b1, b1, b2, __, __, __, __, __, b0, b1, b1, b1, b1, b2, __, __, __, __, b0, b1, b1, b2, __, __, __, __, board_bot, __, __, __, __, b0, b1, b1, b1, b2, __, __, __, __, b0, b1, b1, b2, __, __, __, __, board_bot, __, __, __, __, board_bot, __, __, __, b0, b1, b1, board_bot, __, __, __, __, b0, b1, b1, b2, __, __, __, __, b0, b1, b1, b1, b1, b1, b1, b1, b1, b1, b2, __, __, __, __, board_bot, __, __, __, __, __, b0, b1, b2, __, __, __, __, __, board_bot, __, __, __, __, __, board_bot, __, __, __, __, board_bot, __, __, __, __, __, __, __, __, __, __, __, __, __, b0, b1, b1, b1, b2, __, __, __, __, __, __, __, __, __, __, __, b0, b1, b1, b2, __, __, __, __, board_bot, board_bot, __, __, __, __, board_bot, board_bot, __, __, __, __, __, __, b0, b1, b1, b2, __, __, __, __, b0, b1, b2, __, __, __, __, __, b0, b1, b2, __, __, __, __, b0, b1, b1, __, __, __, __, __, b1, b1, b1, b1, b1, b1, __, __, __, __, __, b1, b1, b1, b1, b1, __, __, __, __, b1, b1, b1, b1, b1, b1, board_bot, __, __, __, __, b0, b1 };//13 board _bottom

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		//setDebugMode(true);
		//initialiseAccelerometer();

		if (getResources().getString(R.string.InterstitialAd_unit_id).length() > 0) {
			// Create the interstitial
			interstitial = new InterstitialAd(this);
			interstitial.setAdUnitId(getResources().getString(R.string.InterstitialAd_unit_id));

			// Create ad request.
			adRequest = new AdRequest.Builder()
					.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
					.addTestDevice("275D94C2B5B93B3C4014933E75F92565")///nexus7//////testing
					.addTestDevice("91608B19766D984A3F929C31EC6AB947") /////////////////testing//////////////////remove///////////
					.addTestDevice("6316D285813B01C56412DAF4D3D80B40") ///test htc sensesion xl
					.addTestDevice("8C416F4CAF490509A1DA82E62168AE08")//asus transformer
					.addTestDevice("7B4C6D080C02BA40EF746C4900BABAD7")//Galaxy S4
					.build();

			// Begin loading your interstitial.
			//interstitial.loadAd(adRequest);
		}
		//initialise banner ad
		this.BANNER_AD_UNIT_ID = getResources().getString(R.string.BannerAd_unit_id);
		showBanner();

	}

	public void openAd() {
		if (getResources().getString(R.string.InterstitialAd_unit_id).length() > 0) {
			runOnUiThread(new Runnable() {
				public void run() {
					if (!interstitial.isLoaded()) {
						interstitial.loadAd(adRequest);
					}
					interstitial.setAdListener(new AdListener() {
						public void onAdLoaded() {
							interstitial.show();
						}

					});

				}
			});
		}
	}

	@Override
	public void Start() {
		super.Start();
		//fonts
		Typeface Orbitron = Typeface.createFromAsset(getAssets(), "Orbitron-Medium.ttf");
		//TODO: change any font sizes from here.
		//set paints
		//title
		Title_Paint.setTextSize(dpToPx(60));
		Title_Paint.setAntiAlias(true);
		Title_Paint.setColor(getResources().getColor(R.color.black));
		Title_Paint.setTypeface(Orbitron);

		//gameover paint
		GameOver_Paint.setTextSize(dpToPx(60));
		GameOver_Paint.setAntiAlias(true);
		GameOver_Paint.setColor(getResources().getColor(R.color.white_cyan));
		GameOver_Paint.setTypeface(Orbitron);

		//SubTitle Paint
		SubTitle_Paint.setTextSize(dpToPx(25));
		SubTitle_Paint.setAntiAlias(true);
		SubTitle_Paint.setColor(getResources().getColor(R.color.black));
		SubTitle_Paint.setTypeface(Orbitron);

		//play stroke
		play_stroke.setTextSize(dpToPx(55));
		play_stroke.setAntiAlias(true);
		play_stroke.setStrokeWidth(dpToPx(3));
		play_stroke.setColor(getResources().getColor(R.color.blue));
		play_stroke.setTypeface(Orbitron);
		play_stroke.setStyle(Style.STROKE);
		play_stroke.setStrokeJoin(Join.ROUND);
		play_stroke.setStrokeMiter(10);

		play_paint.setTextSize(dpToPx(55));
		play_paint.setAntiAlias(true);
		play_paint.setColor(getResources().getColor(R.color.black));
		play_paint.setTypeface(Orbitron);

		//score Paint
		Score_Paint.setTextSize(dpToPx(50));
		Score_Paint.setAntiAlias(true);
		Score_Paint.setColor(getResources().getColor(R.color.white_cyan));
		Score_Paint.setTypeface(Orbitron);

		//Instruction Paint
		Instruction_Paint.setTextSize(dpToPx(15));
		Instruction_Paint.setAntiAlias(true);
		Instruction_Paint.setColor(getResources().getColor(R.color.black));
		Instruction_Paint.setTypeface(Orbitron);

		Instruction_Paint2.setTextSize(dpToPx(20));
		Instruction_Paint2.setAntiAlias(true);
		Instruction_Paint2.setColor(getResources().getColor(R.color.white_cyan));
		Instruction_Paint2.setTypeface(Orbitron);

		//floor paint
		floor_paint.setAntiAlias(true);
		floor_paint.setColor(getResources().getColor(R.color.black));

		//star paint
		star_paint.setAntiAlias(true);
		star_paint.setColor(getResources().getColor(R.color.white_cyan));

		//sky paint
		sky_paint.setAntiAlias(true);
		sky_paint.setColor(getResources().getColor(R.color.grey));

		//building paint
		building_paint.setAntiAlias(true);
		building_paint.setColor(getResources().getColor(R.color.dark_grey));

		//get menu ready
		//play button
		btn_Play = new Button(getResources().getString(R.string.Play), 55, Orbitron, getResources().getColor(R.color.black), 0, 0, this, false);
		btn_Play.x = (ScreenWidth() / 2) - btn_Play.getWidth() / 2;
		btn_Play.y = (ScreenHeight() / 2) - btn_Play.getHeight() / 2;

		//exit button
		btn_Exit = new Button(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.exit), ScreenWidth() * 0.07f), 0, 0, this, false);
		btn_Exit.x = ScreenWidth() - btn_Exit.getWidth() * 1.2f;
		btn_Exit.y = ScreenHeight() - btn_Exit.getHeight() * 1.2f - dpToPx(floor_height);

		//rate button
		btn_rate = new Button(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.rate), ScreenWidth() * 0.08f), 0, 0, this, false);
		btn_rate.x = ScreenWidth() - btn_Exit.getWidth() * 1.2f - btn_rate.getWidth() * 1.3f;
		btn_rate.y = ScreenHeight() - btn_rate.getHeight() * 1.2f - dpToPx(floor_height);

		//highscores button
		btn_Highscores = new Button(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.highscore), ScreenWidth() * 0.08f), 0, 0, this, false);
		btn_Highscores.x = ScreenWidth() - btn_Exit.getWidth() * 1.2f - btn_Highscores.getWidth() * 1.3f - btn_rate.getWidth() * 1.3f;
		btn_Highscores.y = ScreenHeight() - btn_Highscores.getHeight() * 1.2f - dpToPx(floor_height);
		
		//share button
		btn_share = new Button(getResources().getString(R.string.Share), 35, Orbitron, getResources().getColor(R.color.white_cyan), 0, 0, this, false);
		btn_share.x = (ScreenWidth() / 2) - btn_share.getWidth() / 2;
		btn_share.y = (ScreenHeight() * 0.64f);

		//home button
		btn_Home = new Button(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.menu), ScreenWidth() * 0.07f), 0, 0, this, false);
		btn_Home.x = (ScreenWidth() / 2) - btn_share.getWidth() * 0.75f - (btn_Home.getWidth());
		btn_Home.y = (ScreenHeight() * 0.62f);

		//replay button
		btn_Replay = new Button(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.replay), ScreenWidth() * 0.065f), 0, 0, this, false);
		btn_Replay.x = (ScreenWidth() / 2) + btn_share.getWidth() * 0.75f;
		btn_Replay.y = (ScreenHeight() * 0.62f);

		//play button during pause
		btn_continue = new Button(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.play), ScreenWidth() * 0.08f), 0, 0, this, false);
		btn_continue.x = (ScreenWidth() / 2) - btn_continue.getWidth() * 0.5f;
		btn_continue.y = (ScreenHeight() * 0.58f);

		//sound buttons
		music_on = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.music_on), ScreenHeight() * 0.1f);
		music_off = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.music_off), ScreenHeight() * 0.1f);
		sound_off = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.sound_off), ScreenHeight() * 0.1f);
		sound_on = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.sound_on), ScreenHeight() * 0.1f);
		//music mute
		btn_music_mute = new Button(music_on, 0, 0, this, false);
		btn_music_mute.x = ScreenWidth() - btn_music_mute.getWidth() * 1.2f;
		btn_music_mute.y = btn_music_mute.getHeight() * 0.06f;
		//sound mute
		btn_sound_mute = new Button(sound_on, ScreenWidth() - btn_music_mute.getWidth() * 2.5f, btn_music_mute.getHeight() * 0.15f, this, false);

		//pause button
		play_btn_sprite = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.play), ScreenHeight() * 0.2f);
		pause_btn_sprite = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.pause), ScreenHeight() * 0.08f);
		btn_pause = new Button(pause_btn_sprite, ScreenWidth() - btn_music_mute.getWidth() * 4f, btn_music_mute.getHeight() * 0.17f, this, false);

		//menu robot
		robotMenu = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.robot_menu), ScreenHeight() * 0.53f);
		boardMenu = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.board_bottom), ScreenWidth() * 0.6f);

		//moon
		moon = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.moon), ScreenWidth() * 0.2f);
		moon_small = new Instance(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.moon), ScreenWidth() * 0.08f), ScreenWidth() * 0.9f, ScreenHeight() * 0.35f, this, false);

		//set world origin
		setOrigin(BOTTOM_LEFT);

		//initialise character
		robot = new Instance(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.robot), (ScreenHeight() * 0.25f), 7, 7, 463), 50, 300, this, true);

		//initialise background
		//static
		background_buildings_top = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.back_bottom), ScreenWidth());
		background_buildings_bottom = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.back_bottom), ScreenWidth());
		background_buildings_top.rotate(180);
		//moving
		background_top = new BackgroundRoller(BitmapFactory.decodeResource(getResources(), R.drawable.back_bottom), this, dpToPx(floor_height) + dpToPx(building_height), 3);
		background_top.image1.sprite.rotate(180);
		background_top.image2.sprite.rotate(180);
		background_bottom = new BackgroundRoller(BitmapFactory.decodeResource(getResources(), R.drawable.back_bottom), this, 0, 3);
		background_bottom.setY(ScreenHeight() - dpToPx(floor_height) - dpToPx(building_height) - background_bottom.image1.getHeight());

		//initialise floor
		//TODO: Add any floor bar to this array
		Sprite[] floor_list = new Sprite[] {
				new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.floor_bottom_1), ScreenHeight() * 0.1f, true),
				new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.floor_bottom_2), ScreenHeight() * 0.1f, true),
				new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.floor_bottom_3), ScreenHeight() * 0.1f, true),
				new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.floor_top_1), ScreenHeight() * 0.1f, true),
				new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.floor_top_2), ScreenHeight() * 0.1f, true),
				new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.floor_top_3), ScreenHeight() * 0.1f, true),
				new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.board_top), ScreenHeight() * 0.07f, true),
				new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.board_bottom), ScreenHeight() * 0.07f, true),
		};

		bottom_floorGenerator = new FloorGenerator(this, floor_list, 0, (int) (ScreenHeight() * 0.20f), bottom_tile_list);
		top_floorGenerator = new FloorGenerator(this, floor_list, 0, (int) (ScreenHeight() * 0.90f), top_tile_list);

		//initialise item generator
		//TODO: Add any special item to this array, then add it to the time line below next to coin
		Instance[] item_list = new Instance[] {
				new Instance(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.spike), (ScreenHeight() * 0.1f)), 0, 0, this, false, spike),
				new Instance(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.coin), (ScreenHeight() * 0.1f)), 0, 0, this, false, coin)
		};
		item_list[coin].speedx = -ScreenWidth() * 0.001f * robot_speed;
		item_list[spike].speedx = -ScreenWidth() * 0.001f * robot_speed;

		itemGenerator = new ObjectManager(item_list, this, true, ObjectManager.RIGHT, 0.2f, 0.8f);
		itemGenerator.add_timeperiod(0, 800, new Integer[] { coin }, 40);//add items you wish to be generated during game here next to coin
		itemGenerator.add_timeperiod(800, 100000, new Integer[] { coin }, 50);

		//initialise top junk generator
		//TODO: Add any image you wish to display on the top of the screen to this array
		Instance[] Junk_list_top = new Instance[] {
				new Instance(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.robotparts1), (ScreenHeight() * 0.1f)), 0, 0, this, false),
				new Instance(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.robotparts2), (ScreenHeight() * 0.1f)), 0, 0, this, false),
				new Instance(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.robotparts3), (ScreenHeight() * 0.1f)), 0, 0, this, false),
				new Instance(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.robotparts4), (ScreenHeight() * 0.1f)), 0, 0, this, false)
		};
		for (int i = 0; i < Junk_list_top.length; i++)
			Junk_list_top[i].speedx = -ScreenWidth() * 0.001f * robot_speed;

		BottomJunkGenerator = new ObjectManager(Junk_list_top, this, true, ObjectManager.RIGHT, 1 - ((float) (dpToPx(floor_height) + Junk_list_top[0].getHeight() / 2) / (float) ScreenHeight()), ((float) (dpToPx(floor_height) + Junk_list_top[0].getHeight() / 2) / (float) ScreenHeight()));
		BottomJunkGenerator.add_timeperiod(0, 1000000, new Integer[] { 0, 1, 2, 3 }, 70);

		//initialise bottom junk generator
		//TODO: Add any image you wish to display on the bottom of the screen to this array
		Instance[] Junk_list_bottom = new Instance[] {
				new Instance(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.robotparts1), (ScreenHeight() * 0.1f)), 0, 0, this, false),
				new Instance(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.robotparts2), (ScreenHeight() * 0.1f)), 0, 0, this, false),
				new Instance(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.robotparts3), (ScreenHeight() * 0.1f)), 0, 0, this, false),
				new Instance(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.robotparts4), (ScreenHeight() * 0.1f)), 0, 0, this, false)
		};
		for (int i = 0; i < Junk_list_bottom.length; i++) {
			Junk_list_bottom[i].sprite.flip(Sprite.VERTICAL);
			Junk_list_bottom[i].speedx = -ScreenWidth() * 0.001f * robot_speed;
		}

		TopJunkGenerator = new ObjectManager(Junk_list_bottom, this, true, ObjectManager.RIGHT, ((float) dpToPx(floor_height) / (float) ScreenHeight()), 1 - ((float) dpToPx(floor_height) / (float) ScreenHeight()));
		TopJunkGenerator.add_timeperiod(0, 1000000, new Integer[] { 0, 1, 2, 3 }, 65);

		//initialise sound fx
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

		//initialise music and sound effects
		//TODO: you can rename sound files from here
		music = MediaPlayer.create(activity, R.raw.music);
		sound_click = sp.load(activity, R.raw.click, 1);
		sound_flip = sp.load(activity, R.raw.flip, 1);
		sound_gameover = sp.load(activity, R.raw.gameover, 1);
		sound_coin = sp.load(activity, R.raw.coin, 1);

	}

	@Override
	synchronized public void Step() {
		super.Step();
		if (state == MENU) {

		} else if (state == GAMEPLAY) {

			//things to pause
			if (!notstarted && !pause && !game_over) {
				//robot movement
				robot.Update();
				bottom_floorGenerator.restart_if_larger_than_last_tile((int) robot.x);
				top_floorGenerator.restart_if_larger_than_last_tile((int) robot.x);

				//move camera
				if (ScreenX(robot.x) > ScreenWidth() / 2) {
					cameraX += robot.speedx;
					background_bottom.step();
					background_top.step();
					moon_small.x -= dpToPx(1) * 0.4f;

					//update item generation
					itemGenerator.update();
					//collision to special items
					for (int i = 0; i < itemGenerator.Objects_live.size(); i++) {
						if (robot.CollidedWith(itemGenerator.Objects_live.get(i))) {
							if (itemGenerator.Objects_live.get(i).tag == spike) {
								robot.accelerationy = 0;
								robot.speedy = 0;
								game_over = true;
								robot.speedx = 0;
								robot.sprite.PauseOn(2);
								robot.rotate(270);
								robot.sprite.flip(Sprite.HORIZONTAL);
								if (sound_gameover != 0 && !sound_muted)
									sp.play(sound_gameover, 1, 1, 0, 0, 1);
							}
							if (itemGenerator.Objects_live.get(i).tag == coin) {
								score += 5;
								if (sound_coin != 0 && !sound_muted)
									sp.play(sound_coin, 1, 1, 0, 0, 1);
							}
							itemGenerator.Objects_live.remove(i);
						}
					}

					//update junk generators
					TopJunkGenerator.update();
					BottomJunkGenerator.update();
				}

				//handle collisions to floors. Gameover if floor touched
				if (gravity <= 0) {
					//bottom floor
					if (robot.y <= robot.getHeight() + bottom_floorGenerator.startY - (bottom_floorGenerator.sprites[0].getHeight())) {
						//robot.accelerationy = 0;
						//robot.speedy = 0;
						//robot.y = robot.getHeight() + dpToPx(floor_height);
						game_over = true;
						robot.speedx = 0;
						robot.sprite.PauseOn(2);
						robot.rotate(90);
						if (sound_gameover != 0 && !sound_muted)
							sp.play(sound_gameover, 1, 1, 0, 0, 1);
					} else {
						robot.accelerationy = dpToPx(gravity) * 0.005f;
					}
				} else {
					//upper floor
					if (robot.y >= top_floorGenerator.startY - top_floorGenerator.sprites[0].getHeight() + (top_floorGenerator.sprites[0].getHeight())) {
						//robot.accelerationy = 0;
						//robot.speedy = 0;
						//robot.y = ScreenHeight() - dpToPx(floor_height);
						game_over = true;
						robot.speedx = 0;
						robot.sprite.PauseOn(2);
						robot.rotate(270);
						robot.sprite.flip(Sprite.HORIZONTAL);
						if (sound_gameover != 0 && !sound_muted)
							sp.play(sound_gameover, 1, 1, 0, 0, 1);

					} else {
						robot.accelerationy = dpToPx(gravity) * 0.005f;
					}

				}

				//character to top boards collisions
				if (top_floorGenerator.collision(robot)) {
					if ((robot.speedy > 0)) {
						robot.y = top_floorGenerator.startY - top_floorGenerator.sprites[0].getHeight() + 5;
						robot.accelerationy = 0;
						robot.speedy = 0;

					}
				}

				//character to bottom boards collisions
				if (bottom_floorGenerator.collision(robot)) {
					if ((robot.speedy < 0)) {
						robot.y = robot.getHeight() + bottom_floorGenerator.startY - 5;
						robot.accelerationy = 0;
						robot.speedy = 0;
					}
				}

			}

			//check for game over
			if (game_over)
				gameover_counter++;
			else
				gameover_counter = 0;
			if (gameover_counter > gameover_delay)
				GameOver();

		}

	}

	@Override
	public synchronized void onAccelerometer(PointF point) {

	}

	@Override
	public synchronized void BackPressed() {
		if (state == GAMEPLAY) {
			StopMusic();
			state = MENU;
		} else if (state == MENU) {
			StopMusic();
			Exit();

		} else if (state == GAMEOVER) {
			state = MENU;
		}
	}

	@Override
	public synchronized void onTouch(float TouchX, float TouchY, MotionEvent event) {
		//handle constant events like sound buttons
		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			if (btn_sound_mute.isTouched(event)) {
				btn_sound_mute.Highlight(getResources().getColor(R.color.blue));
			}
			if (btn_music_mute.isTouched(event)) {
				btn_music_mute.Highlight(getResources().getColor(R.color.blue));
			}
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			//refresh all
			btn_music_mute.LowLight();
			btn_sound_mute.LowLight();

			if (btn_sound_mute.isTouched(event)) {
				toggleSoundFx();
			}
			if (btn_music_mute.isTouched(event)) {
				toggleMusic();
			}
		}

		if (state == MENU) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (btn_Play.isTouched(event)) {
					btn_Play.Highlight(getResources().getColor(R.color.blue));
				}
				if (btn_Highscores.isTouched(event)) {
					btn_Highscores.Highlight(getResources().getColor(R.color.blue));
				}
				if (btn_Exit.isTouched(event)) {
					btn_Exit.Highlight(getResources().getColor(R.color.blue));
				}
				if (btn_rate.isTouched(event)) {
					btn_rate.Highlight(getResources().getColor(R.color.blue));
				}
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				//refresh all
				btn_Play.LowLight();
				btn_Highscores.LowLight();
				btn_Exit.LowLight();
				btn_rate.LowLight();

				if (btn_Play.isTouched(event)) {
					if (sound_click != 0 && !sound_muted)
						sp.play(sound_click, 1, 1, 0, 0, 1);
					StartGame();
				}
				if (btn_Highscores.isTouched(event)) {
					if (sound_click != 0 && !sound_muted)
						sp.play(sound_click, 1, 1, 0, 0, 1);
					//open leaderboard
					OpenLeaderBoard();
				}
				if (btn_rate.isTouched(event)) {
					Rate();
				}
				if (btn_Exit.isTouched(event)) {
					Exit();
				}

			}
			if (event.getAction() == MotionEvent.ACTION_MOVE) {

			}
		} else if (state == GAMEOVER) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (btn_Home.isTouched(event)) {
					btn_Home.Highlight(getResources().getColor(R.color.blue));
				}
				if (btn_share.isTouched(event)) {
					btn_share.Highlight(getResources().getColor(R.color.blue));
				}
				if (btn_Replay.isTouched(event)) {
					btn_Replay.Highlight(getResources().getColor(R.color.blue));
				}
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				//refresh all
				btn_Home.LowLight();
				btn_share.LowLight();
				btn_Replay.LowLight();
				if (btn_Home.isTouched(event)) {
					state = MENU;
					if (sound_click != 0 && !sound_muted)
						sp.play(sound_click, 1, 1, 0, 0, 1);
				}
				if (btn_share.isTouched(event)) {
					//share with facebook
					share();
					if (sound_click != 0 && !sound_muted)
						sp.play(sound_click, 1, 1, 0, 0, 1);
				}
				if (btn_Replay.isTouched(event)) {
					StartGame();
					if (sound_click != 0 && !sound_muted)
						sp.play(sound_click, 1, 1, 0, 0, 1);

				}
			}
		} else if (state == GAMEPLAY) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (btn_pause.isTouched(event))
					btn_pause.Highlight(getResources().getColor(R.color.blue));

				if (btn_Home.isTouched(event))
					btn_Home.Highlight(getResources().getColor(R.color.blue));

				if (btn_continue.isTouched(event))
					btn_continue.Highlight(getResources().getColor(R.color.blue));

				if (btn_Replay.isTouched(event))
					btn_Replay.Highlight(getResources().getColor(R.color.blue));

				//start game
				if (notstarted) {
					notstarted = false;
					robot.sprite.Play();
				} else {
					//unpause
					if (pause) {
						if (btn_Home.isTouched(event)) {
							state = MENU;
							if (sound_click != 0 && !sound_muted)
								sp.play(sound_click, 1, 1, 0, 0, 1);
						}
						if (btn_continue.isTouched(event)) {
							unPause();
							if (sound_click != 0 && !sound_muted)
								sp.play(sound_click, 1, 1, 0, 0, 1);
						}
						if (btn_Replay.isTouched(event)) {
							StartGame();
							if (sound_click != 0 && !sound_muted)
								sp.play(sound_click, 1, 1, 0, 0, 1);

						}
					} else {
						//pause
						if (btn_pause.isTouched(event)) {
							togglePause();
							if (sound_click != 0 && !sound_muted)
								sp.play(sound_click, 1, 1, 0, 0, 1);
						} else if (bottom_floorGenerator.collision(robot) || top_floorGenerator.collision(robot)) {
							//flip gravity
							gravity = -gravity;
							robot.sprite.flip(Sprite.VERTICAL);
							score++;
							if (sound_flip != 0 && !sound_muted)
								sp.play(sound_flip, 1, 1, 0, 0, 1);
						}

					}
				}
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				btn_pause.LowLight();
				btn_Home.LowLight();
				btn_continue.LowLight();
				btn_Replay.LowLight();

			}

		}
	}

	//..................................................Game Functions..................................................................................................................................

	public void StartGame() {
		//refresh score
		score = 0;

		//refresh robot
		robot.accelerationy = -ScreenHeight() * 0.0001f * gravity;
		robot.speedx = ScreenWidth() * 0.001f * robot_speed;
		robot.speedy = 0;
		robot.y = robot.getHeight() + bottom_floorGenerator.startY;
		robot.x = dpToPx(30);//29000;
		robot.sprite.PauseOn(0);
		robot.sprite.rotate(0);

		//refresh moon
		moon_small.x = ScreenWidth() * 0.9f;

		//refresh camera
		cameraY = 0;
		cameraX = 0;//29000;

		//not started
		notstarted = true;
		game_over = false;
		state = GAMEPLAY;
		PlayMusic();

		//set gravity
		gravity = initial_gravity;

		//gameover counter
		gameover_counter = 0;

		//refresh items
		itemGenerator.restart();
		itemGenerator.Objects_live.clear();

		//refresh junk
		BottomJunkGenerator.restart();
		TopJunkGenerator.restart();
		BottomJunkGenerator.Objects_live.clear();
		TopJunkGenerator.Objects_live.clear();

		//refresh floor
		top_floorGenerator.restart();
		bottom_floorGenerator.restart();

		//pause off
		pause = false;
		btn_pause.sprite = pause_btn_sprite;

	}

	public void share() {
		//share
		Share sharer = new Share();
		Bitmap screenshot = Bitmap.createBitmap(ScreenWidth(), ScreenHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas();
		canvas.setBitmap(screenshot);
		int temp_state = state;
		state = GAMEPLAY;
		Draw(canvas);
		state = temp_state;
		sharer.share_screenshot(this, screenshot);
	}

	public synchronized void GameOver() {
		//if player signed into Google play update score
		updateScore(score);

		//get topscore
		topScore = new SingleScore(screen).load_localscore();

		pause = false;
		ad_counter++;
		if (ad_counter >= getResources().getInteger(R.integer.ad_shows_every_X_gameovers)) {
			openAd();
			ad_counter = 0;
		}
		StopMusic();
		state = GAMEOVER;
	}

	public void PlayMusic() {
		if (!music_muted && state == GAMEPLAY) {
			music = MediaPlayer.create(activity, R.raw.music);
			music.setVolume(0.4f, 0.4f);
			music.start();
			music.setLooping(true);
		}
	}

	public void StopMusic() {
		if (music != null)
			music.stop();
	}

	public void toggleMusic() {
		if (music_muted) {

			music_muted = false;
			btn_music_mute.sprite = music_on;
			if (!pause) {
				PlayMusic();
			}
		} else {
			music_muted = true;
			btn_music_mute.sprite = music_off;
			StopMusic();
		}
	}

	public void toggleSoundFx() {
		if (sound_muted) {
			sound_muted = false;
			btn_sound_mute.sprite = sound_on;
		} else {
			sound_muted = true;
			btn_sound_mute.sprite = sound_off;
		}
	}

	public void pause() {
		if (state == GAMEPLAY) {
			pause = true;
			StopMusic();
			robot.sprite.PauseOn(0);
		}
	}

	public void unPause() {
		pause = false;
		if (!music_muted)
			PlayMusic();
		robot.sprite.Play();

	}

	public void togglePause() {
		if (state == GAMEPLAY) {
			if (pause)
				unPause();
			else
				pause();

		}
	}

	public void Rate() {
		Uri uri = Uri.parse("market://details?id=" + getPackageName());
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
			startActivity(goToMarket);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, getResources().getString(R.string.unable_to_reach_market), Toast.LENGTH_LONG).show();
		}
	}

	//...................................................Rendering of screen............................................................................................................................
	@Override
	public void Draw(Canvas canvas) {
		//draw background
		renderBackground(canvas);

		if (state == MENU) {
			//draw buildings
			background_buildings_bottom.draw(canvas, 0, ScreenHeight() - background_buildings_bottom.getHeight() - dpToPx(floor_height) - dpToPx(building_height));
			canvas.drawRect(0, ScreenHeight() - dpToPx(floor_height) - dpToPx(building_height), ScreenWidth(), ScreenHeight(), building_paint);

			robotMenu.draw(canvas, (ScreenWidth() * 0.05f), (ScreenHeight() * 0.85f - robotMenu.getHeight()));
			boardMenu.draw(canvas, (ScreenWidth() * 0.04f), (ScreenHeight() * 0.85f));

			//draw moon
			moon.draw(canvas, ScreenWidth() * 0.8f, ScreenHeight() * 0.2f);

			//title
			canvas.drawText(getResources().getString(R.string.app_name), (ScreenWidth() / 2) - (Title_Paint.measureText(getResources().getString(R.string.app_name)) / 2), (ScreenHeight() * 0.25f), Title_Paint);

			//play button
			canvas.drawText(getResources().getString(R.string.Play), btn_Play.x, btn_Play.y + btn_Play.getHeight(), play_stroke);
			btn_Play.draw(canvas);

			//don't display google play top scores if no app id is inserted
			if (getResources().getString(R.string.app_id).length() > 0)
				btn_Highscores.draw(canvas);

			btn_Exit.draw(canvas);
			btn_rate.draw(canvas);

		} else if (state == GAMEPLAY || state == GAMEOVER) {

			//draw moon
			moon_small.draw(canvas);

			//draw buildings
			background_bottom.draw(canvas);
			background_top.draw(canvas);
			canvas.drawRect(0, ScreenHeight() - dpToPx(floor_height) - dpToPx(building_height), ScreenWidth(), ScreenHeight(), building_paint);
			canvas.drawRect(0, dpToPx(floor_height), ScreenWidth(), dpToPx(floor_height) + dpToPx(building_height), building_paint);

			//draw floors
			canvas.drawRect(0, ScreenHeight() - dpToPx(floor_height), ScreenWidth(), ScreenHeight(), floor_paint);
			canvas.drawRect(0, 0, ScreenWidth(), dpToPx(floor_height), floor_paint);

			//draw floors
			bottom_floorGenerator.drawFloor(canvas);
			top_floorGenerator.drawFloor(canvas);

			//draw items
			itemGenerator.drawObjects(canvas);

			//draw junk
			TopJunkGenerator.drawObjects(canvas);
			BottomJunkGenerator.drawObjects(canvas);

			//draw robot
			robot.draw(canvas);

			//draw score
			if (state != GAMEOVER)
				canvas.drawText("" + score, (ScreenWidth() * 0.5f) - (Title_Paint.measureText("" + score) / 2), (float) (ScreenHeight() * 0.2f), Score_Paint);
			//canvas.drawText("c:" + cameraX + "r:" + robot.x + "|" + score, (ScreenWidth() * 0.5f) - (Title_Paint.measureText("" + score) / 2), (float) (ScreenHeight() * 0.2f), Score_Paint);

			//before game starts
			if (notstarted) {
				//draw instructions
				StaticLayout instructionlayout = new StaticLayout(getResources().getString(R.string.Tap_to_start), new TextPaint(Instruction_Paint), (int) ((ScreenWidth() / 1.60)), Layout.Alignment.ALIGN_CENTER, 1.2f, 1f, false);
				canvas.translate((ScreenWidth() / 2) - (ScreenWidth() / 3.3f), (ScreenHeight() / 2) - instructionlayout.getHeight() / 2); //position the text
				instructionlayout.draw(canvas);
				canvas.translate(-((ScreenWidth() / 2) - (ScreenWidth() / 3.3f)), -((ScreenHeight() / 2) - instructionlayout.getHeight() / 2)); //position the text
				//draw tap to start
				canvas.drawText(getResources().getString(R.string.Tap_to_start2), (ScreenWidth() / 2) - (Instruction_Paint2.measureText(getResources().getString(R.string.Tap_to_start2)) / 2), (ScreenHeight() / 2) + (instructionlayout.getHeight() * 0.75f), Instruction_Paint2);
			} else if (pause) {
				canvas.drawColor(getResources().getColor(R.color.trans_black));
				canvas.drawText(getResources().getString(R.string.Paused), (ScreenWidth() / 2) - (GameOver_Paint.measureText(getResources().getString(R.string.Paused)) / 2), (float) (ScreenHeight() * 0.4), GameOver_Paint);
				btn_Home.draw(canvas);
				btn_Replay.draw(canvas);
				btn_continue.draw(canvas);
			}

			//pause button
			btn_pause.draw(canvas);

		}

		if (state == GAMEOVER) {
			//draw transparent black overlay
			canvas.drawColor(getResources().getColor(R.color.trans_black));
			canvas.drawText(getResources().getString(R.string.game_over), (ScreenWidth() / 2) - (GameOver_Paint.measureText(getResources().getString(R.string.game_over)) / 2), (ScreenHeight() * 0.3f), GameOver_Paint);

			canvas.drawText("" + score, (ScreenWidth() / 2) - (play_paint.measureText("" + score) / 2), (ScreenHeight() * 0.55f), play_paint);
			canvas.drawText("" + score, (ScreenWidth() / 2) - (play_stroke.measureText("" + score) / 2), (ScreenHeight() * 0.55f), play_stroke);
			canvas.drawText(getResources().getString(R.string.your_top) + " " + topScore.score, (ScreenWidth() / 2) - (SubTitle_Paint.measureText(getResources().getString(R.string.your_top) + " " + topScore.score) * 0.5f), (ScreenHeight() * 0.38f), SubTitle_Paint);

			btn_share.draw(canvas);
			btn_Home.draw(canvas);
			btn_Replay.draw(canvas);

		}
		//draw sound buttons
		btn_sound_mute.draw(canvas);
		btn_music_mute.draw(canvas);

		//physics.drawDebug(canvas);
		super.Draw(canvas);
	}

	//Rendering of background
	public void renderBackground(Canvas canvas) {

		//TODO: you may wish to change background colors from here
		canvas.drawColor(getResources().getColor(R.color.grey));

		for (int i = 0; i < 100; i++) {
			canvas.drawCircle((float) (Math.sin(i)) * ScreenWidth(), (float) ScreenHeight() * ((float) i / 100), (float) dpToPx(1), star_paint);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		super.onPause();
		pause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
