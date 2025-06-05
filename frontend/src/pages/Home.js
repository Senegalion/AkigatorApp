import React from "react";
import { Link } from "react-router-dom";
import alligatorImg from "../assets/alligator.jpg";

const Home = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-green-400 via-blue-500 to-purple-600 flex flex-col items-center justify-center text-white px-4">
      <div className="text-center space-y-6 animate-fade-in">
        <h1 className="text-5xl font-extrabold drop-shadow-lg">
          🐊 Welcome to <span className="text-yellow-300">Akigator</span>!
        </h1>
        <p className="text-xl">
          An AI-powered guessing game... with attitude 🧠🔥
        </p>

        <div className="w-full max-w-md mx-auto bg-white rounded-3xl shadow-2xl border-4 border-white p-6">
          {" "}
          {/* Changed padding here */}
          <img
            src={alligatorImg}
            alt="Akigator mascot"
            className="object-contain w-full h-64 transition-transform duration-500 hover:scale-105 rounded-2xl"
          />
        </div>

        <div className="flex flex-col sm:flex-row gap-4 justify-center mt-6">
          <Link
            to="/login"
            className="bg-white text-indigo-700 font-semibold py-3 px-6 rounded-full shadow-md hover:bg-indigo-100 transition duration-300"
          >
            Login
          </Link>
          <Link
            to="/register"
            className="bg-yellow-300 text-indigo-900 font-semibold py-3 px-6 rounded-full shadow-md hover:bg-yellow-400 transition duration-300"
          >
            Register
          </Link>
          <Link
            to="/how-to-play"
            className="bg-transparent border-2 border-white text-white font-semibold py-3 px-6 rounded-full hover:bg-white hover:text-indigo-700 transition duration-300"
          >
            How to Play
          </Link>
        </div>

        <div className="mt-12 backdrop-blur-sm bg-white/10 border border-white/20 p-6 rounded-2xl max-w-2xl mx-auto text-left text-base leading-relaxed shadow-xl">
          <h2 className="text-3xl font-bold mb-4 text-white drop-shadow">
            How it works 🕹️
          </h2>
          <p className="text-white/90">
            First, <strong>register</strong> and <strong>log in</strong>. Then
            choose a category — currently, only{" "}
            <span className="italic underline">Animals</span> is available.
            <br />
            <br />
            Think of any animal, but don’t say it out loud! 🐾 When the game
            starts, our clever AI alligator <strong>Akigator</strong> will ask
            you a series of smart yes/no questions to guess which animal you're
            thinking of.
            <br />
            <br />
            New categories like <em>Famous People</em>, <em>Movies</em>, or{" "}
            <em>Cartoons</em> are coming soon — stay tuned! 🚀
          </p>
        </div>
      </div>
    </div>
  );
};

export default Home;
