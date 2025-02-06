package edu.uiuc.cs427app.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import dagger.hilt.android.AndroidEntryPoint;
import edu.uiuc.cs427app.BuildConfig;
import edu.uiuc.cs427app.R;
import edu.uiuc.cs427app.data.models.ChatMessage;

import edu.uiuc.cs427app.data.models.WeatherInformation;
import edu.uiuc.cs427app.ui.adapter.PromptAdapter;

@AndroidEntryPoint
public class WeatherActivity extends BaseActivity implements View.OnClickListener, PromptAdapter.OnPromptClickListener{


    public static final GenerativeModel gm;

    static {
        gm = new GenerativeModel("gemini-1.5-flash-001", BuildConfig.AI_API_KEY);
    }
    private WeatherInformation weather;
    private RecyclerView recyclerView;
    private PromptAdapter adapter;
    private List<ChatMessage> messages;
    private EditText editText;
    private Button sendButton;

    /**
     * Creates the activity
     * @param savedInstanceState the instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_activity);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messages = new ArrayList<>();

        editText = findViewById(R.id.editTextText);
        sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);

        String weatherCondition = getIntent().getStringExtra("weatherCondition");
        String windCondition = getIntent().getStringExtra("windCondition");
        String temperature = getIntent().getStringExtra("temperature");
        String humidity = getIntent().getStringExtra("humidity");


        // Generate a temporary set of weather insights
        weather = WeatherInformation.builder()
                .weatherCondition(weatherCondition)
                .windCondition(windCondition)
                .temperature(temperature)
                .humidity(humidity).build();

        // Process future to render response
        generateAIPrompts(weather, 3).thenAccept(response -> {
            runOnUiThread(() -> {
                List<String> prompts = List.of(response.split(","));
                for (String prompt : prompts) {
                    messages.add(new ChatMessage(prompt.trim(), ChatMessage.MessageType.PROMPT));
                }
                adapter = new PromptAdapter(messages, this);
                recyclerView.setAdapter(adapter);
            });
        }).exceptionally(throwable -> {
            Log.e("app", "Error generating prompts", throwable);
            return null;
        });
    }


    /**
     * On click listener to handle any clicks on the page
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sendButton) {
            String userInput = editText.getText().toString().trim();
            if (!userInput.isEmpty()) {
                // Clear the input field
                editText.setText("");

                // Add the user's message to the chat
                ChatMessage userMessage = new ChatMessage(userInput, ChatMessage.MessageType.USER_MESSAGE);
                messages.add(userMessage);
                adapter.notifyItemInserted(messages.size() - 1);
                recyclerView.scrollToPosition(messages.size() - 1);

                // Send the message to Gemini
                sendQuestionToGemini(userInput);
            }
        }
    }

    // AI Pre-prompt primer
    public static final String AI_PRIMER = "You are a helpful chatbot designed to assist users by generating context-relevant, quick-select questions." +
            " Your task is to generate a list of questions the user can select to ask you." +
            " These questions should be based on the provided WeatherInformation, and they should be simple, relevant, and tailored to the user's current context." +
            " Each question should be a short, clear, and actionable suggestion, formatted as a comma-separated list." +
            " Avoid questions that require additional data to answer." +
            " For example, if the weather data indicates cool temperatures and the possibility of rain, you could suggest questions like: " +
            "'What should I wear today for cool weather?', 'Do I need an umbrella today?', or 'What should I prepare for an outdoor event in case of rain?'." +
            " Do not include irrelevant, generic questions, or questions that ask details already provided in the weather information." +
            " Instead of including numbers in your questions include abstractions such as 'warm', 'cool', or 'moderate'.";


    /**
     * Generate the AI prompts
     * @param weather weather information
     * @param targetLength int
     * @return CompletableFuture
     */
    public CompletableFuture<String> generateAIPrompts(WeatherInformation weather, int targetLength) {

        // Create gemini call
        Log.i("app", "LLM Prompted Weather Info: " + weather);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Content content = new Content.Builder()
                .addText(AI_PRIMER)
                .addText(weather.toString())
                .addText("Generate " + targetLength + " questions.")
                .build();
        Executor executor = Executors.newSingleThreadExecutor();

        // Create future
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        // Add callbacks
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            /**
             * called if the future is successful
             * @param result the resu.t of the content generation
             */
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                Log.println(Log.INFO, "app", "Gemini Response: " + resultText);
                completableFuture.complete(resultText);
            }

            /**
             * called if the future failed
             * @param t the reference to the throwable
             */
            @Override
            public void onFailure(@NonNull Throwable t) {
                completableFuture.completeExceptionally(t);
            }
        }, executor);

        return completableFuture;
    }

    /**
     * Generate the AI response
     * @param weather weather information
     * @param question String
     * @return CompletableFuture
     */
    public CompletableFuture<String> generateAIResponse(WeatherInformation weather, String question) {

        // AI prompt for generating the response
        String AI_RESPONSE_PRIMER = "You are a helpful assistant providing detailed answers based on the user's question and the given weather information. " +
                "Consider the weather details and provide a clear and concise answer to the user's question.";

        // Create Gemini call
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Content content = new Content.Builder()
                .addText(AI_RESPONSE_PRIMER)
                .addText("Weather Information: " + weather.toString())
                .addText("User Question: " + question)
                .build();
        Executor executor = Executors.newSingleThreadExecutor();

        // Create future
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        // Add callbacks
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            /**
             * called if the future is successful
             * @param result the resu.t of the content generation
             */
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                Log.println(Log.INFO, "app", "Gemini Response: " + resultText);
                completableFuture.complete(resultText);
            }

            /**
             * called if the future failed
             * @param t the reference to the throwable
             */
            @Override
            public void onFailure(@NonNull Throwable t) {
                completableFuture.completeExceptionally(t);
            }
        }, executor);

        return completableFuture;
    }

    /**
     * onclick for the prompt
     * @param prompt String
     */
    @Override
    public void onPromptClick(String prompt) {
        editText.setText("");

        ChatMessage userMessage = new ChatMessage(prompt, ChatMessage.MessageType.USER_MESSAGE);
        messages.add(userMessage);
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);

        sendQuestionToGemini(prompt);
    }

    /**
     * Sends question to Gemini
     * @param prompt String
     */
    private void sendQuestionToGemini(String prompt) {
        generateAIResponse(weather, prompt).thenAccept(response -> {
            runOnUiThread(() -> {
                // Add the AI response to the chat
                ChatMessage aiMessage = new ChatMessage(response.trim(), ChatMessage.MessageType.AI_MESSAGE);
                messages.add(aiMessage);
                adapter.notifyItemInserted(messages.size() - 1);
                recyclerView.scrollToPosition(messages.size() - 1);
            });
        }).exceptionally(throwable -> {
            // Handle exception
            Log.e("app", "Error generating AI response", throwable);
            return null;
        });
    }

    /**
     * show the given response
     * @param response String
     */
    private void showResponse(String response) {
        new AlertDialog.Builder(this)
                .setTitle("Response")
                .setMessage(response)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

}