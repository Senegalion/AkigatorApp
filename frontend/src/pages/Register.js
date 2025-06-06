import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

const Register = () => {
  const [formData, setFormData] = useState({
    name: "",
    surname: "",
    username: "",
    email: "",
    password: "",
  });
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) =>
    setFormData({ ...formData, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const response = await fetch("http://localhost:8080/api/auth/signup", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        const errorMessage = await response.text();
        setError(errorMessage);
        return;
      }
      alert("Registration successful!");
      navigate("/login");
    } catch (error) {
      setError("Something went wrong. Please try again.");
    }
  };

  return (
    <div className="container mx-auto mt-10 p-6 bg-gradient-to-br from-pink-400 via-purple-500 to-indigo-600 rounded-xl shadow-lg">
      <h2 className="text-3xl font-bold text-center text-white mb-5">
        Register
      </h2>
      {error && <div className="text-center text-red-500 mb-4">{error}</div>}
      <form onSubmit={handleSubmit} className="space-y-4">
        {["name", "surname", "username", "email", "password"].map(
          (field, index) => (
            <div key={index} className="relative">
              <label htmlFor={field} className="text-white text-lg">
                {field.charAt(0).toUpperCase() + field.slice(1)}
              </label>
              <input
                type={field === "password" ? "password" : "text"}
                name={field}
                value={formData[field]}
                onChange={handleChange}
                required
                className="w-full p-3 mt-2 text-black rounded-lg shadow-md focus:outline-none focus:ring-2 focus:ring-indigo-300 transition duration-300"
              />
            </div>
          )
        )}
        <button
          type="submit"
          className="w-full p-3 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition duration-300"
        >
          Register
        </button>
      </form>
    </div>
  );
};

export default Register;
