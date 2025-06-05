import React from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import { AuthProvider, useAuth } from "./context/AuthContext";
import Navbar from "./components/Navbar";
import HowToPlay from "./pages/HowToPlay";
import NewSessionPage from "./pages/NewSessionPage";
import PlayGamePage from "./pages/PlayGamePage";
import SummaryPage from "./pages/SummaryPage";
import GameResultsPage from "./pages/GameResultsPage";

const PrivateRoute = ({ element }) => {
  const { user } = useAuth();
  return user ? element : <Navigate to="/login" />;
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="min-h-screen bg-gradient-to-r from-purple-500 via-indigo-500 to-blue-500 text-white p-6">
          <Navbar />
          <div className="max-w-4xl mx-auto py-10">
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/how-to-play" element={<HowToPlay />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route
                path="/dashboard"
                element={<PrivateRoute element={<Dashboard />} />}
              />
              <Route
                path="/game/new"
                element={<PrivateRoute element={<NewSessionPage />} />}
              />
              <Route path="/game/play" element={<PlayGamePage />} />
              <Route path="/game/summary" element={<SummaryPage />} />
              <Route
                path="/game/results"
                element={<PrivateRoute element={<GameResultsPage />} />}
              />{" "}
              {/* Nowa trasa */}
            </Routes>
          </div>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
