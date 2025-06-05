import React, { useEffect, useState, useCallback } from "react";
import axios from "axios";
import { useSearchParams, useNavigate } from "react-router-dom";

const PlayGamePage = () => {
  const [searchParams] = useSearchParams();
  const sessionId = searchParams.get("sessionId");

  const [question, setQuestion] = useState(null);
  const [answers, setAnswers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [guess, setGuess] = useState(null);
  const [guessAvailable, setGuessAvailable] = useState(false);

  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  const fetchQuestion = useCallback(
    async (questionId = null) => {
      setLoading(true);
      try {
        const res = await axios.get("http://localhost:8091/api/game/play", {
          params: {
            sessionId,
            ...(questionId ? { questionId } : {}),
          },
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        setQuestion(res.data.question);
        setAnswers(res.data.answers);
        setGuess(null);
        setGuessAvailable(false);
      } catch (err) {
        console.error("Failed to load question", err);
      } finally {
        setLoading(false);
      }
    },
    [sessionId, token]
  );

  useEffect(() => {
    if (!sessionId) {
      console.error("Brak sessionId w URL");
      navigate("/dashboard");
      return;
    }
    fetchQuestion();
  }, [sessionId, fetchQuestion, navigate]);

  const handleAnswer = async (answerId) => {
    try {
      const res = await axios.post(
        "http://localhost:8091/api/game/answer",
        {},
        {
          params: {
            sessionId,
            questionId: question?.questionId,
            answerId,
          },
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (res.data.guessAvailable && res.data.guess) {
        setGuess(res.data.guess);
        setGuessAvailable(true);
      } else if (res.data.nextQuestionId) {
        fetchQuestion(res.data.nextQuestionId);
      } else {
        setGuess("Unknown");
        setGuessAvailable(true);
      }
    } catch (err) {
      console.error("Failed to submit answer", err);
    }
  };

  const handleGuessResponse = async (correct) => {
    try {
      await axios.post(
        "http://localhost:8091/api/game/guess-result",
        {},
        {
          params: {
            sessionId,
            correct,
          },
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      navigate("/game/summary");
    } catch (err) {
      console.error("Failed to submit guess result", err);
    }
  };

  if (loading)
    return <div className="text-center mt-10">Loading question...</div>;

  if (!question)
    return <div className="text-center mt-10">No question available!</div>;

  if (guessAvailable) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-purple-600 to-indigo-600">
        <div className="bg-white p-6 rounded-2xl shadow-xl max-w-md w-full text-center">
          <h2 className="text-xl font-semibold mb-4">🤖 I guess...</h2>
          <p className="text-2xl font-bold text-indigo-700 mb-6">{guess}</p>
          <p className="mb-4">Was I right?</p>
          <div className="flex justify-center gap-4">
            <button
              onClick={() => handleGuessResponse(true)}
              className="bg-green-500 text-white px-4 py-2 rounded-lg hover:bg-green-600"
            >
              Yes
            </button>
            <button
              onClick={() => handleGuessResponse(false)}
              className="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600"
            >
              No
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-indigo-600 to-purple-600">
      <div className="bg-white p-6 rounded-2xl shadow-xl max-w-md w-full">
        <h2 className="text-xl font-semibold mb-4 text-indigo-700">
          {question?.content}
        </h2>
        <div className="space-y-3">
          {answers.map((a) => (
            <button
              key={a.answerId}
              onClick={() => handleAnswer(a.answerId)}
              className="w-full bg-indigo-500 text-white py-2 px-4 rounded-lg hover:bg-indigo-600 transition"
            >
              {a.content}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
};

export default PlayGamePage;
