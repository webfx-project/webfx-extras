package dev.webfx.extras.player.audio.javafxmedia;

import dev.webfx.extras.media.metadata.MetadataUtil;
import dev.webfx.extras.panes.ScalePane;
import dev.webfx.extras.player.Player;
import dev.webfx.extras.player.Status;
import dev.webfx.extras.styles.bootstrap.Bootstrap;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.kit.util.properties.Unregisterable;
import dev.webfx.platform.util.Objects;
import dev.webfx.stack.i18n.I18n;
import dev.webfx.stack.i18n.controls.I18nControls;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.util.Duration;

public class AudioMediaView {

    private final Player player;
    final static HBox container = new HBox();

    protected Pane backwardButton;
    protected Pane forwardButton;
    protected Pane playButton;
    protected Pane pauseButton;
    protected final ProgressBar progressBar = new ProgressBar();
    protected final VBox titleAndProgressBarVBox = new VBox();
    static final int PLAYER_WIDTH = 700;
    protected StringProperty titleProperty = new SimpleStringProperty();
    protected LongProperty durationProperty = new SimpleLongProperty();
    protected double elapsedTimeMilliseconds = 0;
    protected Label elapsedTimeLabel;

    private Unregisterable mediaPlayerBinding;

    private static final String BACKWARD_BUTTON_PATH_32 = "M0.90073 10.0738C0.848166 10.0318 0.805773 9.97708 0.777065 9.91417C0.748358 9.85127 0.734165 9.78198 0.735664 9.71207L0.902374 1.90094C0.904501 1.8203 0.927299 1.74181 0.968309 1.67395C1.00932 1.6061 1.06698 1.55146 1.13505 1.51593C1.20333 1.48071 1.27937 1.46603 1.35492 1.47347C1.43048 1.48092 1.50269 1.5102 1.56373 1.55816L3.16813 2.8323L3.29432 2.93277C5.08103 1.10791 7.50828 -0.00692496 10.1742 0.0478267C15.5209 0.15733 19.7866 4.87471 19.6837 10.5622C19.5808 16.2499 15.1473 20.7888 9.80059 20.6793C9.60874 20.6749 9.41704 20.6647 9.22573 20.6487C9.22573 20.6487 9.09735 20.6417 8.9498 20.5698L8.9446 20.5669C8.87231 20.5301 8.8059 20.4814 8.74778 20.4227C8.73847 20.4142 8.72944 20.4052 8.72068 20.3959L8.71548 20.391C8.71165 20.3866 8.71028 20.3822 8.70699 20.3779C8.58814 20.2367 8.5238 20.053 8.52687 19.8636C8.52687 19.8557 8.52906 19.8484 8.52934 19.8405L8.57724 17.1889H8.57943C8.57943 17.1807 8.57751 17.1731 8.57751 17.165C8.57926 17.0656 8.59939 16.9676 8.63675 16.8766C8.67412 16.7855 8.72799 16.7031 8.79528 16.6342C8.86257 16.5653 8.94197 16.5112 9.02893 16.4749C9.1159 16.4387 9.20872 16.421 9.30211 16.4229V16.4221C9.48991 16.4467 9.67875 16.4615 9.86793 16.4663C13.0313 16.5313 15.6548 13.8461 15.7156 10.4815C15.7766 7.11661 13.2522 4.32632 10.0888 4.26137C8.75353 4.234 7.52033 4.70317 6.53076 5.4994L6.80066 5.71433L8.40643 6.98847C8.46693 7.03714 8.51378 7.10247 8.5419 7.17741C8.57003 7.25236 8.57837 7.33405 8.56602 7.41367C8.54083 7.57559 8.43188 7.71043 8.28379 7.75965L1.27357 10.1411C1.14682 10.1847 1.00913 10.1594 0.90073 10.0738Z";
    private static final String FORWARD_BUTTON_PATH_32 = "M19.7243 10.0738C19.7768 10.0318 19.8192 9.97708 19.8479 9.91417C19.8766 9.85127 19.8908 9.78198 19.8893 9.71207L19.7226 1.90094C19.7205 1.8203 19.6977 1.74181 19.6567 1.67395C19.6157 1.6061 19.558 1.55146 19.4899 1.51593C19.4217 1.48071 19.3456 1.46603 19.2701 1.47347C19.1945 1.48092 19.1223 1.5102 19.0613 1.55816L17.4569 2.8323L17.3307 2.93277C15.544 1.10791 13.1167 -0.00692496 10.4508 0.0478267C5.10405 0.15733 0.838351 4.87471 0.941277 10.5622C1.0442 16.2499 5.47771 20.7888 10.8244 20.6793C11.0163 20.6749 11.208 20.6647 11.3993 20.6487C11.3993 20.6487 11.5277 20.6417 11.6752 20.5698L11.6804 20.5669C11.7527 20.5301 11.8191 20.4814 11.8772 20.4227C11.8865 20.4142 11.8956 20.4052 11.9043 20.3959L11.9095 20.391C11.9134 20.3866 11.9147 20.3822 11.918 20.3779C12.0369 20.2367 12.1012 20.053 12.0981 19.8636C12.0981 19.8557 12.0959 19.8484 12.0957 19.8405L12.0478 17.1889H12.0456C12.0456 17.1807 12.0475 17.1731 12.0475 17.165C12.0457 17.0656 12.0256 16.9676 11.9882 16.8766C11.9509 16.7855 11.897 16.7031 11.8297 16.6342C11.7624 16.5653 11.683 16.5112 11.5961 16.4749C11.5091 16.4387 11.4163 16.421 11.3229 16.4229C11.1351 16.4476 10.9462 16.4615 10.7571 16.4663C7.59372 16.5313 4.97019 13.8461 4.90942 10.4815C4.84838 7.11661 7.37281 4.32632 10.5362 4.26137C11.8715 4.234 13.1047 4.70317 14.0942 5.4994L13.8243 5.71433L12.2186 6.98847C12.1581 7.03714 12.1112 7.10247 12.0831 7.17741C12.055 7.25236 12.0466 7.33405 12.059 7.41367C12.0842 7.57559 12.1931 7.71043 12.3412 7.75965L19.3514 10.1411C19.4782 10.1847 19.6159 10.1594 19.7243 10.0738Z";


    public AudioMediaView(Player player) {
        this.player = player;
        player.mediaProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                playNewMedia();
            }
        });
        Label title = new Label();
        title.textProperty().bind(titleProperty);
        titleProperty.set(I18n.getI18nText(JavaFXMediaI18nKeys.ChooseAnAudioTrackForBehind));
        title.setTextFill(Color.WHITE);
        title.getStyleClass().add(Bootstrap.H4);
        title.setMaxWidth(450);
        elapsedTimeLabel = new Label("00:00:00");
        elapsedTimeLabel.getStyleClass().add("time");
        //We put a min width here because when we play, the width can change, depending if it displays 1 et 8.
        elapsedTimeLabel.setMinWidth(60);
        Label durationLabel = new Label("00:00:00");
        durationLabel.getStyleClass().add("time");
        durationLabel.textProperty().bind(new StringBinding() {
            {
                super.bind(durationProperty); // Bind to the LongProperty
            }

            @Override
            protected String computeValue() {
                bindMediaPlayer();
                return formatDuration(durationProperty.get());
            }
        });

        HBox progressBarContainer = new HBox();
        progressBarContainer.setSpacing(10);
        progressBar.setPrefWidth(300);
        progressBar.getStyleClass().add("progress-bar");
        progressBar.setOnMouseClicked(e -> seekX(e.getX()));
        progressBar.setOnMouseDragged(e -> seekX(e.getX()));
        progressBar.setCursor(Cursor.HAND);
        progressBarContainer.getChildren().addAll(elapsedTimeLabel, progressBar, durationLabel);
        progressBarContainer.setAlignment(Pos.CENTER);
        titleAndProgressBarVBox.setSpacing(10);
        titleAndProgressBarVBox.getChildren().addAll(title, progressBarContainer);
        titleAndProgressBarVBox.setAlignment(Pos.CENTER_LEFT);
        container.setSpacing(30);
        container.setMinWidth(PLAYER_WIDTH);
        container.setMaxWidth(PLAYER_WIDTH);
        container.setMinHeight(127);
        container.setMaxHeight(127);
        backwardButton = createBackwardButton();
        backwardButton.setOnMouseClicked(e -> seekRelative(-15));
        playButton = createPlayButton();
        playButton.setOnMouseClicked(e->play());
        playButton.setDisable(true);
        pauseButton = createPauseButton();
        pauseButton.setVisible(false);
        pauseButton.setOnMouseClicked(e->pause());
        StackPane playPauseStackPane = new StackPane();
        playPauseStackPane.getChildren().addAll(playButton,pauseButton);
        forwardButton = createForwardButton();
        forwardButton.setOnMouseClicked(e -> seekRelative(+15));

        HBox buttonsHBox = new HBox();
        buttonsHBox.setPadding(new Insets(0, 0, 0, 30));
        buttonsHBox.setSpacing(10);
        buttonsHBox.setAlignment(Pos.CENTER);
        Label minus15sLabel = I18nControls.newLabel(JavaFXMediaI18nKeys.AbrSecond,15);
        minus15sLabel.setTextFill(Color.WHITE);
        minus15sLabel.setScaleX(0.8);
        minus15sLabel.setScaleY(0.8);
        Label plus15sLabel = I18nControls.newLabel(JavaFXMediaI18nKeys.AbrSecond,15);
        plus15sLabel.setScaleX(0.8);
        plus15sLabel.setScaleY(0.8);
        plus15sLabel.setTextFill(Color.WHITE);
        buttonsHBox.getChildren().addAll(backwardButton, minus15sLabel, playPauseStackPane, plus15sLabel, forwardButton);
        container.getStyleClass().add("audio-player");
        container.getChildren().addAll(buttonsHBox, titleAndProgressBarVBox);
    }


    private void playNewMedia() {
        player.setOnEndOfPlaying(player::stop); // Forcing stop status (sometimes this doesn't happen automatically for any reason)
        seekX(0);
        titleProperty.set(MetadataUtil.getTitle(player.mediaProperty().get().getMetadata()));
        if(player.mediaProperty().get().getMetadata()!=null)
            durationProperty.set(MetadataUtil.getDurationMillis(player.mediaProperty().get().getMetadata()));
    }

    public Node getContainer() {
        return container;
    }

    public Pane createBackwardButton() {
        return embedButton(new StackPane(
            createSVGButton(BACKWARD_BUTTON_PATH_32, null, Color.WHITE)));
    }

    public Pane createPlayButton() {
        return embedButton(new StackPane(
            createSVGButton("M 21.5415 0.3727 C 17.3107 0.3727 13.1749 1.6827 9.657 4.1372 C 6.1392 6.5917 3.3974 10.0803 1.7783 14.1619 C 0.1593 18.2435 -0.2644 22.7349 0.561 27.0679 C 1.3864 31.401 3.4238 35.3811 6.4154 38.505 C 9.4071 41.629 13.2187 43.7564 17.3682 44.6183 C 21.5178 45.4802 25.819 45.0378 29.7277 43.3472 C 33.6365 41.6565 36.9774 38.7934 39.3279 35.1201 C 41.6785 31.4467 42.933 27.128 42.933 22.7101 C 42.933 16.7858 40.6793 11.1042 36.6676 6.9151 C 32.6559 2.7261 27.215 0.3727 21.5415 0.3727 Z M 32.9203 24.1381 L 14.5847 33.7113 C 14.3517 33.8329 14.0928 33.8902 13.8326 33.8779 C 13.5724 33.8656 13.3194 33.7841 13.0979 33.641 C 12.8764 33.4979 12.6936 33.2981 12.5669 33.0604 C 12.4401 32.8228 12.3736 32.5553 12.3738 32.2833 V 13.1369 C 12.3738 12.865 12.4406 12.5978 12.5674 12.3603 C 12.6942 12.1229 12.8771 11.9233 13.0986 11.7804 C 13.3201 11.6375 13.5729 11.5561 13.833 11.5438 C 14.093 11.5316 14.3518 11.589 14.5847 11.7105 L 32.9203 21.2837 C 33.1738 21.4163 33.3869 21.62 33.5358 21.8719 C 33.6847 22.1238 33.7636 22.414 33.7636 22.7101 C 33.7636 23.0062 33.6847 23.2963 33.5358 23.5482 C 33.3869 23.8002 33.1738 24.0038 32.9203 24.1365", null, Color.WHITE)));
    }

    public Pane createPauseButton() {
        return embedButton(new StackPane(
            createSVGButton("M28.1736 0.578125C13.1268 0.578125 0.929688 13.0787 0.929688 28.4999C0.929688 43.9211 13.1268 56.4217 28.1736 56.4217C43.2204 56.4217 55.4175 43.9211 55.4175 28.4999C55.4175 13.0787 43.2204 0.578125 28.1736 0.578125ZM25.4492 39.6686H20.0004V17.3312H25.4492V39.6686ZM36.3468 39.6686H30.898V17.3312H36.3468V39.6686Z", null, Color.WHITE)));
    }


    private void updateElapsedTimeAndProgressBar(Duration elapsed) {
        elapsedTimeMilliseconds = elapsed.toMillis();
        updateText(elapsedTimeLabel, formatDuration((long) elapsed.toMillis()));
        progressBar.setProgress(elapsed.toMillis() / durationProperty.get());
    }

    private static void updateText(Label text, String newContent) {
        if (text!=null && !Objects.areEquals(newContent, text.getText()))
            text.setText(newContent);
    }

    public Pane createForwardButton() {
        return embedButton(new StackPane(
            createSVGButton(FORWARD_BUTTON_PATH_32,null, Color.WHITE)));
    }

    private void bindMediaPlayer() {
        unbindMediaPlayer(); // in case this view was previously bound with another player
        // audio
            mediaPlayerBinding = FXProperties.runNowAndOnPropertiesChange(() -> {
                updatePlayPauseButtons(null);
                boolean isPlaying = player.isPlaying();
                Status status = player.getStatus();
                if (status == null || status == Status.LOADING)
                    progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                else
                    updateElapsedTimeAndProgressBar(status == Status.NO_MEDIA ? Duration.ZERO : isPlaying || status == Status.PAUSED ? player.getCurrentTime() : new Duration(durationProperty.get()));
            }, player.statusProperty(), player.currentTimeProperty());
        // Not yet supported by WebFX
        //mediaPlayer.setOnError(() -> System.out.println("An error occurred: " + mediaPlayer.getError()));

    }

    protected void updatePlayPauseButtons(Boolean anticipatePlaying) {
        if(playButton!=null) playButton.setDisable(false);
        Status status = player.getStatus();
        boolean playing = status == Status.PLAYING;
        if(pauseButton!=null) {
            pauseButton.setVisible((anticipatePlaying != null ? anticipatePlaying : playing));
        }
        if(playButton!=null) {
            playButton.setVisible((anticipatePlaying != null ? !anticipatePlaying : !playing));
        }
        // Sometimes this method is called with an anticipated value for isPlaying (ex: play() method). So we check if
        // the player is really playing
        // Note: when paused, we prefer to show the image (if available) if the player is not able to resume (navigation api not supported)
        // Note: using setVisible(false) doesn't prevent wistia player to appear sometimes, while setOpacity(0) does
    }

    private void seekRelative(double relativeSeconds) {
        player.seek(player.getCurrentTime().add(Duration.seconds(relativeSeconds)));
    }
    private void seekX(double x) {
        double percentage = x / progressBar.getWidth();
        Duration seekTime = new Duration(durationProperty.get()).multiply(percentage);
        player.seek(seekTime);
    }


    private void unbindMediaPlayer() {
        if (mediaPlayerBinding != null) {
            mediaPlayerBinding.unregister();
            mediaPlayerBinding = null;
        }
        updatePlayPauseButtons(false);
        updateElapsedTimeAndProgressBar(Duration.ZERO);
    }

    private static SVGPath createSVGButton(String content, Paint stroke, Paint fill) {
        SVGPath path = new SVGPath();
        path.setContent(content);
        path.setStroke(stroke);
        path.setStrokeLineCap(StrokeLineCap.ROUND);
        path.setStrokeLineJoin(StrokeLineJoin.ROUND);
        path.setFill(fill);
        path.setStrokeWidth(1.5);
        return path;
    }

    private static Pane embedButton(Pane container) {
        container.setMaxSize(32, 32); // Setting a max size, so it can scale
        Pane pane = new ScalePane(container);
        pane.setCursor(Cursor.HAND);
        return pane;
    }


    public static String formatDuration(long durationMillis) {
        // Calculate hours, minutes, and seconds
        long hours = durationMillis / (1000 * 60 * 60); // Calculate hours
        long minutes = (durationMillis / (1000 * 60)) % 60; // Calculate minutes
        long seconds = (durationMillis / 1000) % 60; // Calculate seconds

        return (hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
        }

    private void play() {
        // Starting playing
        if(elapsedTimeMilliseconds>= durationProperty.get()) {
            player.resetToInitialState();
        }
        player.play();
        updatePlayPauseButtons(true);
    }

    private void pause() {
        player.pause();
    }

    public StringProperty getTitleProperty() {
        return titleProperty;
    }

}
