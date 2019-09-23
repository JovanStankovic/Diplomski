package com.example.gametest;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity{
    //Pravimo okvir u kojem moze da se igra
    private FrameLayout gameFrame;
    private int frameHeight, frameWidth, initialFrameWidth;
    private LinearLayout startLayout;
    //Slike
    private ImageView boxBlue, boxPink, bomb, money, cross, boxSelection, boxSelectionPink;
    private Drawable imageBoxRight, imageBoxLeft, imageBoxPinkRight, imageBoxPinkLeft;
    //Velicine
    private int boxSize;
    //Pozicije
    private float boxX, boxY;
    private float boxPinkX, boxPinkY;
    private float bombX, bombY;
    private float moneyX, moneyY;
    private float crossX, crossY;

    //Rezultat
    private TextView scoreLabel, highScoreLabel, nextLevel, gameNameLabel, gameCompleted;
    private int score, highScore, timeCount;
    private SharedPreferences settings;
    //Klasa
    private Timer timer;
    private Handler handler = new Handler();
    private SoundPlayer soundPlayer;
    //Buttons
    private Button playButton, quitGameButton, customizeButton, resumeButton, backButton, mainMenuButton, quitButton;
    private RadioButton selectionRadioButton, selection2RadioButton;
    //Status2
    private boolean start_flg = false;
    private boolean action_flg = false;
    private boolean pink_flg = false;
    private boolean black_flg = false;
    private boolean pause_flg = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPlayer = new SoundPlayer(this);

        gameFrame = findViewById(R.id.gameFrame);
        gameNameLabel = findViewById(R.id.gameNameLabel);
        startLayout = findViewById(R.id.startLayout);
        boxBlue = findViewById(R.id.box);
        boxSelection = findViewById(R.id.boxSelection);
        boxSelectionPink = findViewById(R.id.boxSelectionPink);
        boxPink = findViewById(R.id.boxPink);
        cross = findViewById(R.id.pink);
        bomb = findViewById(R.id.black);
        money = findViewById(R.id.orange);
        scoreLabel = findViewById(R.id.scoreLabel);
        nextLevel = findViewById(R.id.nextLevel);
        gameCompleted = findViewById(R.id.gameCompleted);
        highScoreLabel = findViewById(R.id.highScoreLabel);
        playButton = findViewById(R.id.playButton);
        quitGameButton = findViewById(R.id.quitGameButton);
        backButton = findViewById(R.id.backButton);
        resumeButton = findViewById(R.id.resumeButton);
        quitButton = findViewById(R.id.quitButton);
        customizeButton = findViewById(R.id.customizeButton);
        mainMenuButton = findViewById(R.id.mainMenuButton);

        selectionRadioButton = findViewById(R.id.selectionRadioButton);
        selection2RadioButton = findViewById(R.id.selection2RadioButton);

        imageBoxLeft = getResources().getDrawable(R.drawable.box_left);
        imageBoxRight = getResources().getDrawable(R.drawable.box_right);
        imageBoxPinkLeft = getResources().getDrawable(R.drawable.box_pink_left);
        imageBoxPinkRight = getResources().getDrawable(R.drawable.box_pink_right);

        //highScore
        settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        highScore = settings.getInt("HIGH_SCORE", 0);
        highScoreLabel.setText("High Score : " + highScore);


    }


    public void runGame() {
        changePos();
    }

    public void changePos() {

        //ENDGAME
        if (score == 30) {

            score = score + 1;
            gameCompleted.setText("Game Completed GOOD JOB!");
            timer.cancel();
            timer = null;
            gameCompleted.postDelayed(new Runnable() {
                @Override
                public void run() {
                    gameCompleted.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.slide_in_left));
                    gameCompleted.setVisibility(View.VISIBLE);
                }
            }, 500);
            gameCompleted.postDelayed(new Runnable() {
                @Override
                public void run() {
                    gameCompleted.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.slide_out_right));
                    gameCompleted.setVisibility(View.INVISIBLE);
                    startLayout.setVisibility(View.VISIBLE);
                }
            }, 3500);
            cross.setVisibility(View.INVISIBLE);
            money.setVisibility(View.INVISIBLE);
            bomb.setVisibility(View.INVISIBLE);
            changeFrameWidth(initialFrameWidth);
            boxBlue.setVisibility(View.INVISIBLE);
            boxPink.setVisibility(View.INVISIBLE);

        }
        //LEVEL4
        if (score >= 600 && score < 1000) {
            //Add timeCount;
            timeCount += 20;
            //Orange
            moneyY += 27;

            float orangeCenterX = moneyX + money.getWidth() / 2;
            float orangeCenterY = moneyY + money.getHeight() / 2;

            if (hitCheck(orangeCenterX, orangeCenterY)) {
                moneyY = frameHeight + 100;
                score += 10;
                soundPlayer.playHitOrangeSound();
            }
            if (moneyY - 34 == frameHeight) {
                frameWidth = frameWidth * 85 / 100;
                changeFrameWidth(frameWidth);
            }
            if (moneyY > frameHeight) {
                moneyY = -100;
                moneyX = (float) Math.floor(Math.random() * (frameWidth - money.getWidth()));
            }

            money.setX(moneyX);
            money.setY(moneyY);

            //Pink
            if (!pink_flg && timeCount % 25000 == 0) {
                pink_flg = true;
                crossY = -20;
                crossX = (float) Math.floor(Math.random() * (frameWidth - cross.getWidth()));
            }

            if (pink_flg) {
                crossY += 30;

                float pinkCenterX = crossX + cross.getWidth() / 2;
                float pinkCenterY = crossY + cross.getHeight() / 2;

                if (hitCheck(pinkCenterX, pinkCenterY)) {
                    crossY = frameHeight + 30;
                    score += 30;
                    soundPlayer.playHitPinkSound();
                    //Prosirujemo Frame
                    if (initialFrameWidth > frameWidth * 115 / 100) {
                        frameWidth = frameWidth * 110 / 100;
                        changeFrameWidth(frameWidth);
                    }
                }

                if (crossY > frameHeight) pink_flg = false;
                cross.setX(crossX);
                cross.setY(crossY);
            }
            //Black

            if (!black_flg && timeCount % 350 == 0) {
                black_flg = true;
                bombY = -20;
                bombX = (float) Math.floor(Math.random() * (frameWidth - bomb.getWidth()));
            }
            if (black_flg) {
                bombY += 42;

                float blackCenterX = bombX + bomb.getWidth() / 2;
                float blackCenterY = bombY + bomb.getHeight() / 2;

                if (hitCheck(blackCenterX, blackCenterY)) {
                    bombY = frameHeight + 90;
                    soundPlayer.playHitBLackSound();
                    //Smanjujemo frame
                    frameWidth = frameWidth * 85 / 100;
                    changeFrameWidth(frameWidth);

                }
                if (bombY > frameHeight) black_flg = false;
                bomb.setY(bombY);
                bomb.setX(bombX);

            }
            chosenBox();

            scoreLabel.setText("Score: " + score + "          Level 4");
        }

        //LEVEL3


        else if (score >= 300 && score < 600) {
            //Add timeCount;
            timeCount += 20;
            //Orange
            moneyY += 22;

            float orangeCenterX = moneyX + money.getWidth() / 2;
            float orangeCenterY = moneyY + money.getHeight() / 2;

            if (hitCheck(orangeCenterX, orangeCenterY)) {
                moneyY = frameHeight + 100;
                score += 10;
                soundPlayer.playHitOrangeSound();
            }
            if (moneyY - 20 == frameHeight) {
                frameWidth = frameWidth * 85 / 100;
                changeFrameWidth(frameWidth);
            }

            if (moneyY > frameHeight) {
                moneyY = -100;
                moneyX = (float) Math.floor(Math.random() * (frameWidth - money.getWidth()));
            }

            money.setX(moneyX);
            money.setY(moneyY);

            //Pink
            if (!pink_flg && timeCount % 20000 == 0) {
                pink_flg = true;
                crossY = -20;
                crossX = (float) Math.floor(Math.random() * (frameWidth - cross.getWidth()));
            }

            if (pink_flg) {
                crossY += 40;

                float pinkCenterX = crossX + cross.getWidth() / 2;
                float pinkCenterY = crossY + cross.getHeight() / 2;

                if (hitCheck(pinkCenterX, pinkCenterY)) {
                    crossY = frameHeight + 30;
                    score += 30;
                    soundPlayer.playHitPinkSound();
                    //Prosirujemo Frame
                    if (initialFrameWidth > frameWidth * 115 / 100) {
                        frameWidth = frameWidth * 110 / 100;
                        changeFrameWidth(frameWidth);
                    }
                }

                if (crossY > frameHeight) pink_flg = false;
                cross.setX(crossX);
                cross.setY(crossY);
            }
            //Black

            if (!black_flg && timeCount % 500 == 0) {
                black_flg = true;
                bombY = -20;
                bombX = (float) Math.floor(Math.random() * (frameWidth - bomb.getWidth()));
            }
            if (black_flg) {
                bombY += 34;

                float blackCenterX = bombX + bomb.getWidth() / 2;
                float blackCenterY = bombY + bomb.getHeight() / 2;

                if (hitCheck(blackCenterX, blackCenterY)) {
                    bombY = frameHeight + 90;
                    soundPlayer.playHitBLackSound();
                    //Smanjujemo frame
                    frameWidth = frameWidth * 85 / 100;
                    changeFrameWidth(frameWidth);
                }

                if (bombY > frameHeight) black_flg = false;
                bomb.setY(bombY);
                bomb.setX(bombX);

            }
            //Move Box
            chosenBox();

            scoreLabel.setText("Score: " + score + "          Level 3");
        }

        //LEVEL2


        else if (score >= 100 && score < 300) {
            //Add timeCount;
            timeCount += 20;
            //Orange
            moneyY += 17;

            float orangeCenterX = moneyX + money.getWidth() / 2;
            float orangeCenterY = moneyY + money.getHeight() / 2;

            if (hitCheck(orangeCenterX, orangeCenterY)) {
                moneyY = frameHeight + 100;
                score += 10;
                soundPlayer.playHitOrangeSound();
            }
            if (orangeCenterY - 46 == frameHeight) {
                frameWidth = frameWidth * 85 / 100;
                changeFrameWidth(frameWidth);
            }

            if (moneyY > frameHeight) {
                moneyY = -100;
                moneyX = (float) Math.floor(Math.random() * (frameWidth - money.getWidth()));
            }

            money.setX(moneyX);
            money.setY(moneyY);

            //Pink
            if (!pink_flg && timeCount % 15000 == 0) {
                pink_flg = true;
                crossY = -20;
                crossX = (float) Math.floor(Math.random() * (frameWidth - cross.getWidth()));
            }

            if (pink_flg) {
                crossY += 30;

                float pinkCenterX = crossX + cross.getWidth() / 2;
                float pinkCenterY = crossY + cross.getHeight() / 2;

                if (hitCheck(pinkCenterX, pinkCenterY)) {
                    crossY = frameHeight + 30;
                    score += 30;
                    soundPlayer.playHitPinkSound();
                    //Prosirujemo Frame
                    if (initialFrameWidth > frameWidth * 115 / 100) {
                        frameWidth = frameWidth * 110 / 100;
                        changeFrameWidth(frameWidth);
                    }
                }

                if (crossY > frameHeight) pink_flg = false;
                cross.setX(crossX);
                cross.setY(crossY);
            }
            //Black

            if (!black_flg && timeCount % 1000 == 0) {
                black_flg = true;
                bombY = -20;
                bombX = (float) Math.floor(Math.random() * (frameWidth - bomb.getWidth()));
            }
            if (black_flg) {
                bombY += 26;

                float blackCenterX = bombX + bomb.getWidth() / 2;
                float blackCenterY = bombY + bomb.getHeight() / 2;

                if (hitCheck(blackCenterX, blackCenterY)) {
                    bombY = frameHeight + 90;
                    soundPlayer.playHitBLackSound();
                    //Smanjujemo frame
                    frameWidth = frameWidth * 85 / 100;
                    changeFrameWidth(frameWidth);
                }
                if (bombY > frameHeight) black_flg = false;
                bomb.setY(bombY);
                bomb.setX(bombX);

            }
            chosenBox();

            scoreLabel.setText("Score: " + score + "          Level 2");

        }

        //LEVEL1
        else if (score < 100) {
            //Add timeCount;
            timeCount += 20;
            //Orange
            moneyY += 12;

            float orangeCenterX = moneyX + money.getWidth() / 2;
            float orangeCenterY = moneyY + money.getHeight() / 2;

            if (hitCheck(orangeCenterX, orangeCenterY)) {
                moneyY = frameHeight + 100;
                score += 10;
                soundPlayer.playHitOrangeSound();
            }
            if (orangeCenterY - 46 == frameHeight) {
                frameWidth = frameWidth * 85 / 100;
                changeFrameWidth(frameWidth);
            }

            if (moneyY > frameHeight) {
                moneyY = -100;
                moneyX = (float) Math.floor(Math.random() * (frameWidth - money.getWidth()));
            }
            if (frameWidth <= boxSize + 100) {
                gameOver();
            }
            money.setX(moneyX);
            money.setY(moneyY);

            //Pink
            if (!pink_flg && timeCount % 10000 == 0) {
                pink_flg = true;
                crossY = -20;
                crossX = (float) Math.floor(Math.random() * (frameWidth - cross.getWidth()));
            }

            if (pink_flg) {
                crossY += 20;

                float pinkCenterX = crossX + cross.getWidth() / 2;
                float pinkCenterY = crossY + cross.getHeight() / 2;

                if (hitCheck(pinkCenterX, pinkCenterY)) {
                    crossY = frameHeight + 30;
                    score += 30;
                    soundPlayer.playHitPinkSound();
                    //Prosirujemo Frame
                    if (initialFrameWidth > frameWidth * 115 / 100) {
                        frameWidth = frameWidth * 110 / 100;
                        changeFrameWidth(frameWidth);
                    }
                }

                if (crossY > frameHeight) pink_flg = false;
                cross.setX(crossX);
                cross.setY(crossY);
            }
            //Black

            if (!black_flg && timeCount % 3500 == 0) {
                black_flg = true;
                bombY = -20;
                bombX = (float) Math.floor(Math.random() * (frameWidth - bomb.getWidth()));
            }
            if (black_flg) {
                bombY += 18;

                float blackCenterX = bombX + bomb.getWidth() / 2;
                float blackCenterY = bombY + bomb.getHeight() / 2;

                if (hitCheck(blackCenterX, blackCenterY)) {
                    bombY = frameHeight + 90;
                    soundPlayer.playHitBLackSound();
                    //Smanjujemo frame
                    frameWidth = frameWidth * 85 / 100;
                    changeFrameWidth(frameWidth);
                }

                if (bombY > frameHeight) black_flg = false;
                bomb.setY(bombY);
                bomb.setX(bombX);

            }
            chosenBox();

            scoreLabel.setText("Score: " + score + "          Level 1");
        }
    }

    public void moveBoxBlue() {
        if (action_flg) {
            boxX += 12;
            boxBlue.setImageDrawable(imageBoxRight);
        } else {
            boxX -= 12;
            boxBlue.setImageDrawable(imageBoxLeft);
        }

        //Check Box position
        if (boxX < 0) {
            boxX = 0;
            boxBlue.setImageDrawable(imageBoxRight);
        }
        if (frameWidth - boxSize < boxX) {
            boxX = frameWidth - boxSize;
            boxBlue.setImageDrawable(imageBoxLeft);
        }

        boxBlue.setX(boxX);
    }

    public void moveBoxPink() {
        if (action_flg) {
            boxX += 12;
            boxPink.setImageDrawable(imageBoxPinkRight);
        } else {
            boxX -= 12;
            boxPink.setImageDrawable(imageBoxPinkLeft);
        }

        //Check Box position
        if (boxX < 0) {
            boxX = 0;
            boxPink.setImageDrawable(imageBoxPinkRight);
        }
        if (frameWidth - boxSize < boxX) {
            boxX = frameWidth - boxSize;
            boxPink.setImageDrawable(imageBoxPinkLeft);
        }

        boxPink.setX(boxX);
    }

    public void chosenBox() {
        if (selectionRadioButton.isChecked() == true) {
            moveBoxBlue();
            boxPink.setVisibility(View.INVISIBLE);
        } else {
            moveBoxPink();
            boxBlue.setVisibility(View.INVISIBLE);
        }
    }


    public void openChooseCharacter(View view) {

        gameNameLabel.setVisibility(View.INVISIBLE);
        highScoreLabel.setVisibility(View.INVISIBLE);
        playButton.setVisibility(View.INVISIBLE);
        quitGameButton.setVisibility(View.INVISIBLE);
        customizeButton.setVisibility(View.INVISIBLE);

        scoreLabel.setText("Choose your character");
        backButton.setVisibility(View.VISIBLE);
        selectionRadioButton.setVisibility(View.VISIBLE);
        selection2RadioButton.setVisibility(View.VISIBLE);
        boxSelection.setVisibility(View.VISIBLE);
        boxSelectionPink.setVisibility(View.VISIBLE);

    }


    public void back(View view) {
        gameNameLabel.setVisibility(View.VISIBLE);
        highScoreLabel.setVisibility(View.VISIBLE);
        playButton.setVisibility(View.VISIBLE);
        quitGameButton.setVisibility(View.VISIBLE);
        customizeButton.setVisibility(View.VISIBLE);

        scoreLabel.setText("Score: 0");
        backButton.setVisibility(View.INVISIBLE);
        selectionRadioButton.setVisibility(View.INVISIBLE);
        selection2RadioButton.setVisibility(View.INVISIBLE);
        boxSelection.setVisibility(View.INVISIBLE);
        boxSelectionPink.setVisibility(View.INVISIBLE);


    }


    public boolean hitCheck(float x, float y) {

        if (boxX <= x - 10 && x - 10 <= boxX + boxSize &&
                boxY <= y + 45 && y + 45 <= frameHeight) {
            return true;
        }
        return false;
    }


    public void changeFrameWidth(int frameWidth) {
        ViewGroup.LayoutParams params = gameFrame.getLayoutParams();
        params.width = frameWidth;
        gameFrame.setLayoutParams(params);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        nextLevelWithPause();
        if (start_flg) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                action_flg = true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                action_flg = false;
            }
        }
        return true;
    }

    public void startGame(View view) {

        startLayout.setVisibility(View.INVISIBLE);
        frameWidth = initialFrameWidth;
        start_flg = true;
        soundPlayer.playGameStartMusic();

        if (frameHeight == 0) {
            frameHeight = gameFrame.getHeight();
            frameWidth = gameFrame.getWidth();
            initialFrameWidth = frameWidth;

            if (selectionRadioButton.isChecked()) {
                boxSize = boxBlue.getHeight();
                boxX = boxBlue.getX();
                boxY = boxBlue.getY();
            } else {
                boxSize = boxBlue.getHeight();
                boxX = boxBlue.getX();
                boxY = boxBlue.getY();
            }

        }

        if (selectionRadioButton.isChecked()) {
            boxBlue.setX(0.0f);
            boxBlue.setVisibility(View.VISIBLE);
        } else {
            boxPink.setX(0.0f);
            boxPink.setVisibility(View.VISIBLE);
        }


        bomb.setY(3000.0f);
        money.setY(3000.0f);
        cross.setY(3000.0f);

        bombY = bomb.getY();
        moneyY = money.getY();
        crossY = cross.getY();


        cross.setVisibility(View.VISIBLE);
        bomb.setVisibility(View.VISIBLE);
        money.setVisibility(View.VISIBLE);

        timeCount = 0;
        score = 0;
        gameNameLabel.setText("Bounty Hunter");

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (start_flg) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            runGame();
                        }
                    });
                }
            }
        }, 0, 20);

    }

    public void pauseGame(View view) {

        pause_flg = true;
        timer.cancel();
        timer = null;

        changeFrameWidth(initialFrameWidth);
        boxBlue.setVisibility(View.INVISIBLE);
        boxPink.setVisibility(View.INVISIBLE);
        cross.setVisibility(View.INVISIBLE);
        money.setVisibility(View.INVISIBLE);
        bomb.setVisibility(View.INVISIBLE);

        resumeButton.setVisibility(View.VISIBLE);
        mainMenuButton.setVisibility(View.VISIBLE);
        quitButton.setVisibility(View.VISIBLE);
    }

    public void resumeGame(View view) {

        pause_flg = false;

        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {


                        if (selectionRadioButton.isChecked()) {
                            boxBlue.setVisibility(View.VISIBLE);
                        } else {
                            boxPink.setVisibility(View.VISIBLE);
                        }
                        changeFrameWidth(frameWidth);
                        cross.setVisibility(View.VISIBLE);
                        money.setVisibility(View.VISIBLE);
                        bomb.setVisibility(View.VISIBLE);
                        runGame();
                        ;
                        resumeButton.setVisibility(View.INVISIBLE);
                        mainMenuButton.setVisibility(View.INVISIBLE);
                        quitButton.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }, 0, 20);

    }

    public void mainMenu(View view) {

        changeFrameWidth(initialFrameWidth);

        startLayout.setVisibility(View.VISIBLE);
        resumeButton.setVisibility(View.INVISIBLE);
        mainMenuButton.setVisibility(View.INVISIBLE);
        quitButton.setVisibility(View.INVISIBLE);
    }

    public void pause() {

        pause_flg = true;
        timer.cancel();
        timer = null;

        changeFrameWidth(initialFrameWidth);
        boxBlue.setVisibility(View.INVISIBLE);
        boxPink.setVisibility(View.INVISIBLE);
        cross.setVisibility(View.INVISIBLE);
        money.setVisibility(View.INVISIBLE);
        bomb.setVisibility(View.INVISIBLE);


        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        runGame();
                        if (frameWidth <= boxSize + 100) {
                            gameOver();
                        } else {
                            changeFrameWidth(frameWidth);
                            if (selectionRadioButton.isChecked()) {
                                boxBlue.setVisibility(View.VISIBLE);
                            } else boxPink.setVisibility(View.VISIBLE);
                            cross.setVisibility(View.VISIBLE);
                            money.setVisibility(View.VISIBLE);
                            bomb.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }, 3300, 20);

    }

    public void nextLevel() {

        nextLevel.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextLevel.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.slide_in_left));
                nextLevel.setVisibility(View.VISIBLE);
            }
        }, 500);
        nextLevel.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextLevel.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.slide_out_right));
                nextLevel.setVisibility(View.INVISIBLE);
            }
        }, 2500);
        score = score + 40;
    }

    public void nextLevelWithPause() {
        if (score >= 600 && score <= 630) {
            pause();
            nextLevel();
        } else if (score >= 300 && score <= 330) {
            pause();
            nextLevel();
        } else if (score >= 100 && score <= 130) {
            pause();
            nextLevel();
        }
    }


    public void gameOver() {

        timer.cancel();
        timer = null;
        start_flg = false;

        //Befre showing starting Lyaout sleep 1s
        try {
            TimeUnit.SECONDS.sleep(1);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        changeFrameWidth(initialFrameWidth);

        startLayout.setVisibility(View.VISIBLE);
        gameNameLabel.setText("Game Over");
        boxPink.setVisibility(View.INVISIBLE);
        boxBlue.setVisibility(View.INVISIBLE);
        cross.setVisibility(View.INVISIBLE);
        money.setVisibility(View.INVISIBLE);
        bomb.setVisibility(View.INVISIBLE);


        //Update HighScore
        if (score > highScore) {
            highScore = score;
            highScoreLabel.setText("High Score : " + highScore);

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("HIGH_SCORE", highScore);
            editor.apply();
        }
    }

    public void quitGame(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else {
            finish();
        }
    }
}


