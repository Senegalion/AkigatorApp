import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const GameResultsPage = () => {
  const [sessions, setSessions] = useState({
    wins: [],
    losses: [],
    topWins: [],
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const token = localStorage.getItem("token");

  useEffect(() => {
    const fetchSessions = async () => {
      try {
        const res = await axios.get("http://localhost:8091/api/game/sessions", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setSessions(res.data);
      } catch (err) {
        setError("Failed to fetch game sessions.");
        console.error("Failed to fetch game sessions", err);
      } finally {
        setLoading(false);
      }
    };

    fetchSessions();
  }, [token]);

  const handleBackToDashboard = () => {
    navigate("/dashboard");
  };

  if (loading) {
    return <div className="text-white text-xl">Loading game sessions...</div>;
  }

  if (error) {
    return <div className="text-white text-xl">{error}</div>;
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-600 to-indigo-600 flex items-center justify-center">
      <div className="bg-white p-8 rounded-2xl shadow-2xl w-full max-w-3xl">
        <h2 className="text-3xl font-bold text-gray-800 mb-6">Game Results</h2>

        {/* Top 3 Wins */}
        <section className="mb-8">
          <h3 className="text-2xl font-semibold text-gray-800 mb-4">
            Top 3 Wins
          </h3>
          <table className="table-auto w-full text-sm text-left text-gray-700">
            <thead className="bg-indigo-600 text-white">
              <tr>
                <th className="px-4 py-2">Start Time</th>
                <th className="px-4 py-2">End Time</th>
                <th className="px-4 py-2">Result</th>
              </tr>
            </thead>
            <tbody>
              {sessions.topWins.map((session, index) => (
                <tr key={index} className="border-b hover:bg-gray-50">
                  <td className="px-4 py-2">
                    {new Date(session.startTime).toLocaleString()}
                  </td>
                  <td className="px-4 py-2">
                    {session.endTime
                      ? new Date(session.endTime).toLocaleString()
                      : "Ongoing"}
                  </td>
                  <td className="px-4 py-2">{session.result}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>

        {/* All Wins */}
        <section className="mb-8">
          <h3 className="text-2xl font-semibold text-gray-800 mb-4">
            All Wins
          </h3>
          <table className="table-auto w-full text-sm text-left text-gray-700">
            <thead className="bg-indigo-600 text-white">
              <tr>
                <th className="px-4 py-2">Start Time</th>
                <th className="px-4 py-2">End Time</th>
                <th className="px-4 py-2">Result</th>
              </tr>
            </thead>
            <tbody>
              {sessions.wins.map((session, index) => (
                <tr key={index} className="border-b hover:bg-gray-50">
                  <td className="px-4 py-2">
                    {new Date(session.startTime).toLocaleString()}
                  </td>
                  <td className="px-4 py-2">
                    {session.endTime
                      ? new Date(session.endTime).toLocaleString()
                      : "Ongoing"}
                  </td>
                  <td className="px-4 py-2">{session.result}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>

        {/* All Losses */}
        <section>
          <h3 className="text-2xl font-semibold text-gray-800 mb-4">
            All Losses
          </h3>
          <table className="table-auto w-full text-sm text-left text-gray-700">
            <thead className="bg-indigo-600 text-white">
              <tr>
                <th className="px-4 py-2">Start Time</th>
                <th className="px-4 py-2">End Time</th>
                <th className="px-4 py-2">Result</th>
              </tr>
            </thead>
            <tbody>
              {sessions.losses.map((session, index) => (
                <tr key={index} className="border-b hover:bg-gray-50">
                  <td className="px-4 py-2">
                    {new Date(session.startTime).toLocaleString()}
                  </td>
                  <td className="px-4 py-2">
                    {session.endTime
                      ? new Date(session.endTime).toLocaleString()
                      : "Ongoing"}
                  </td>
                  <td className="px-4 py-2">{session.result}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>

        <button
          onClick={handleBackToDashboard}
          className="mt-8 w-full bg-indigo-600 text-white py-3 px-6 rounded-full shadow-lg text-lg hover:bg-indigo-700 transition duration-200"
        >
          Back to Dashboard
        </button>
      </div>
    </div>
  );
};

export default GameResultsPage;
