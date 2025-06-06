import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const SummaryPage = () => {
  const [sessionSummary, setSessionSummary] = useState(null);
  const [loading, setLoading] = useState(true); // Track loading state
  const [error, setError] = useState(null); // Track error state
  const navigate = useNavigate();

  const sessionId = new URLSearchParams(window.location.search).get(
    "sessionId"
  );
  const token = localStorage.getItem("token");

  useEffect(() => {
    if (!sessionId) {
      // Redirect to the GameResultsPage if no sessionId is found
      navigate("/game/results");
      return; // Exit early to avoid further execution
    }

    const fetchSummary = async () => {
      try {
        const res = await axios.get(
          `http://localhost:8080/api/game/sessions/${sessionId}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setSessionSummary(res.data);
      } catch (err) {
        setError("Failed to fetch session summary"); // Set error message
        console.error("Failed to fetch session summary", err);
      } finally {
        setLoading(false); // Set loading to false after fetching is done
      }
    };

    fetchSummary();
  }, [sessionId, token, navigate]); // Add navigate to dependencies

  const handleReturnToDashboard = () => {
    navigate("/dashboard");
  };

  const handleViewResults = () => {
    // Navigate to the GameResultsPage after clicking "View Game Results"
    navigate("/game/results");
  };

  if (loading) {
    return <div className="text-center">Loading summary...</div>;
  }

  if (error) {
    return <div className="text-center text-red-500">{error}</div>; // Display error if any
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-purple-600 to-indigo-600">
      <div className="bg-white p-6 rounded-2xl shadow-xl max-w-md w-full text-center">
        <h2 className="text-xl font-semibold mb-4">Game Summary</h2>
        <p className="text-lg mb-4">Your guess was {sessionSummary.result}</p>
        <p className="mb-4">
          You played in the category: {sessionSummary.categoryName}
        </p>

        {/* Button to navigate to Game Results */}
        <div className="flex justify-center gap-4">
          <button
            onClick={handleReturnToDashboard}
            className="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600"
          >
            Back to Dashboard
          </button>
          <button
            onClick={handleViewResults}
            className="bg-green-500 text-white px-4 py-2 rounded-lg hover:bg-green-600"
          >
            View Game Results
          </button>
        </div>
      </div>
    </div>
  );
};

export default SummaryPage;
