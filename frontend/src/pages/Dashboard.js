import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const Dashboard = () => {
  const { logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div className="flex flex-col items-center justify-center h-screen bg-gradient-to-br from-purple-600 to-indigo-600 text-white px-4">
      <h2 className="text-4xl font-bold mb-4 animate-fade-in">
        Welcome to the Dashboard
      </h2>
      <p className="mb-8 text-lg animate-fade-in delay-100">
        Start a new game below!
      </p>
      <Link
        to="/game/new"
        className="bg-white text-indigo-600 font-semibold px-6 py-3 rounded-full shadow-lg hover:scale-105 transform transition-all duration-300"
      >
        Start Game
      </Link>

      <Link
        to="/game/results"
        className="mt-6 bg-white text-indigo-600 font-semibold px-6 py-3 rounded-full shadow-lg hover:scale-105 transform transition-all duration-300"
      >
        View Game Results
      </Link>

      {}
      <button
        onClick={handleLogout}
        className="mt-6 bg-red-600 text-white py-2 px-6 rounded-full shadow-lg hover:bg-red-700 transition duration-200"
      >
        Logout
      </button>
    </div>
  );
};

export default Dashboard;
