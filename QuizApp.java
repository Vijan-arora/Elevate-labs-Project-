import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.util.Duration;

import java.util.*;

public class QuizApp extends Application {

    // --- GLOBAL DATA ---
    private Stage window;
    private String currentUser = "";
    private int index = 0;
    private int score = 0;
    private int timeLeft = 60; // 1-minute quiz
    private Timeline timer;

    // UI elements reused
    private Label timerLabel = new Label("Time: 60");
    private Label questionLabel = new Label();
    private RadioButton optA = new RadioButton();
    private RadioButton optB = new RadioButton();
    private RadioButton optC = new RadioButton();
    private RadioButton optD = new RadioButton();
    private ToggleGroup group = new ToggleGroup();

    // Hardcoded question set
    private final List<Question> questions = Arrays.asList(
            new Question("What is JVM?", "Java Virtual Machine", "Java Vendor Machine", "Joint Venture Model", "None", 'A'),
            new Question("Which keyword creates an object?", "make", "create", "new", "object", 'C'),
            new Question("Which is NOT a Java feature?", "OOP", "Portable", "Secure", "Manual Memory Management", 'D'),
            new Question("Which data type is 64-bit?", "int", "double", "float", "short", 'B'),
            new Question("Which company owns Java?", "Amazon", "IBM", "Oracle", "Microsoft", 'C')
    );

    @Override
    public void start(Stage stage) {
        this.window = stage;
        stage.setTitle("Online Quiz System");
        showLogin();
        stage.show();
    }

    /** -------------------- LOGIN SCREEN -------------------- **/
    private void showLogin() {
        Label title = new Label("Login");
        title.setStyle("-fx-font-size: 26px;");

        TextField userField = new TextField();
        userField.setPromptText("Enter username");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter password");

        Label msg = new Label();
        msg.setStyle("-fx-text-fill: red;");

        Button loginBtn = new Button("Login");
        loginBtn.setOnAction(e -> {
            if (userField.getText().isEmpty() || passField.getText().isEmpty()) {
                msg.setText("Fields cannot be empty!");
            } else {
                currentUser = userField.getText();
                startQuiz();
            }
        });

        VBox layout = new VBox(10, title, userField, passField, loginBtn, msg);
        layout.setAlignment(Pos.CENTER);
        window.setScene(new Scene(layout, 400, 350));
    }

    /** -------------------- QUIZ SCREEN -------------------- **/
    private void startQuiz() {
        Collections.shuffle(questions); // Randomize order

        optA.setToggleGroup(group);
        optB.setToggleGroup(group);
        optC.setToggleGroup(group);
        optD.setToggleGroup(group);

        timerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: red;");
        showQuestion();
        startTimer();

        Button nextBtn = new Button("Next");
        nextBtn.setOnAction(e -> nextQuestion());

        VBox layout = new VBox(15, timerLabel, questionLabel, optA, optB, optC, optD, nextBtn);
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.setStyle("-fx-padding: 20;");

        window.setScene(new Scene(layout, 500, 400));
    }

    private void showQuestion() {
        Question q = questions.get(index);
        questionLabel.setText((index + 1) + ". " + q.text);

        optA.setText(q.a);
        optB.setText(q.b);
        optC.setText(q.c);
        optD.setText(q.d);

        group.selectToggle(null); // clear selection
    }

    private void nextQuestion() {
        Question q = questions.get(index);
        RadioButton selected = (RadioButton) group.getSelectedToggle();

        if (selected != null) {
            char chosen = answerToLetter(q, selected.getText());
            if (chosen == q.correct)
                score++;
        }

        index++;

        if (index == questions.size()) {
            timer.stop();
            showResult();
        } else {
            showQuestion();
        }
    }

    private char answerToLetter(Question q, String answer) {
        if (answer.equals(q.a)) return 'A';
        if (answer.equals(q.b)) return 'B';
        if (answer.equals(q.c)) return 'C';
        return 'D';
    }

    /** -------------------- TIMER -------------------- **/
    private void startTimer() {
        timeLeft = 60;

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            timerLabel.setText("Time: " + timeLeft);

            if (timeLeft <= 0) {
                timer.stop();
                showResult();
            }
        }));

        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    /** -------------------- RESULT SCREEN -------------------- **/
    private void showResult() {
        Label result = new Label("Quiz Completed!");
        result.setStyle("-fx-font-size: 24px;");

        Label scoreLabel = new Label("Score: " + score + " / " + questions.size());
        scoreLabel.setStyle("-fx-font-size: 20px;");

        Button retry = new Button("Retry");
        retry.setOnAction(e -> resetQuiz());

        VBox layout = new VBox(20, result, scoreLabel, retry);
        layout.setAlignment(Pos.CENTER);
        window.setScene(new Scene(layout, 400, 300));
    }

    private void resetQuiz() {
        index = 0;
        score = 0;
        startQuiz();
    }

    /** -------------------- QUESTION STRUCT -------------------- **/
    static class Question {
        String text, a, b, c, d;
        char correct;

        public Question(String text, String a, String b, String c, String d, char correct) {
            this.text = text;
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.correct = correct;
        }
    }

    /** -------------------- MAIN -------------------- **/
    public static void main(String[] args) {
        launch(args);
    }
}
