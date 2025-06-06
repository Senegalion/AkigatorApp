import axios from "axios";

const API_URL = "http://localhost:8080/api/auth";

const login = async (username, password) => {
  try {
    const response = await axios.post(`${API_URL}/signin`, {
      username,
      password,
    });
    return response.data;
  } catch (error) {
    console.error("Login failed", error);
    return null;
  }
};

const register = async (userData) => {
  try {
    const response = await axios.post(`${API_URL}/signup`, userData);
    return response.data;
  } catch (error) {
    console.error("Registration failed", error);
    return null;
  }
};

export default { login, register };
