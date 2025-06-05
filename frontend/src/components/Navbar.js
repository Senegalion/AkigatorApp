import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const Navbar = () => {
  const { user, logout } = useAuth();

  return (
    <nav className="bg-white/10 backdrop-blur-md border-b border-white/20 p-4 rounded-xl shadow-md flex justify-between items-center mb-8">
      <div className="text-xl font-bold text-yellow-300 tracking-widest drop-shadow-md">
        🐊 Akigator
      </div>
      <div className="flex gap-4">
        <Link to="/" className="text-white hover:underline">
          Home
        </Link>
        {user ? (
          <>
            <Link to="/dashboard" className="text-white hover:underline">
              Dashboard
            </Link>
            <button
              onClick={logout}
              className="text-white bg-red-500 hover:bg-red-600 px-4 py-2 rounded-full font-semibold transition"
            >
              Logout
            </button>
          </>
        ) : (
          <>
            <Link
              to="/login"
              className="text-white bg-indigo-500 hover:bg-indigo-600 px-4 py-2 rounded-full font-semibold transition"
            >
              Login
            </Link>
            <Link
              to="/register"
              className="text-white bg-yellow-400 hover:bg-yellow-500 px-4 py-2 rounded-full font-semibold transition"
            >
              Register
            </Link>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
