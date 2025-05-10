package org.example.akigatorapp.models;

public class ScriptResult {
    String fullOutput;
    String guess;

    public ScriptResult(String fullOutput, String guess) {
        this.fullOutput = fullOutput;
        this.guess = guess;
    }

    public String getFullOutput() {
        return fullOutput;
    }

    public void setFullOutput(String fullOutput) {
        this.fullOutput = fullOutput;
    }

    public String getGuess() {
        return guess;
    }

    public void setGuess(String guess) {
        this.guess = guess;
    }
}
