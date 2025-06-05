import React from "react";

const HowToPlay = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-green-400 via-blue-500 to-purple-600 flex flex-col items-center justify-center text-white px-4 py-10">
      <div className="text-center max-w-3xl space-y-8 animate-fade-in">
        <h1 className="text-4xl sm:text-5xl font-extrabold drop-shadow-lg">
          🎮 How to Play <span className="text-yellow-300">Akigator</span>
        </h1>

        <div className="bg-white/10 backdrop-blur-md border border-white/20 rounded-2xl p-8 text-left shadow-xl text-white/90 space-y-4 text-lg leading-relaxed">
          <p>
            Think of an animal, but don’t say it out loud! 🐾 Akigator — our
            clever AI alligator — will try to guess what you're thinking of by
            asking you a series of yes/no questions.
          </p>

          <p>
            The AI is trained on real-world data and tries to narrow down
            possibilities with each question. Just answer honestly, and see if
            it can outsmart you!
          </p>

          <p>
            🧠 The more you play, the better Akigator gets. It learns from
            feedback and becomes sharper over time.
          </p>

          <p>
            🚀 In the future, we’ll add new categories like{" "}
            <em>Famous People</em>, <em>Movies</em>, and <em>Cartoons</em>.
          </p>

          <p>
            Ready to test the AI? Go to your <strong>Dashboard</strong> and
            start a new game!
          </p>
        </div>
      </div>
    </div>
  );
};

export default HowToPlay;
