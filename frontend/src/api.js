import axios from "axios";

const API_URL = "http://localhost:8091";

export const sendResetPasswordEmail = async (email) => {
  try {
    const response = await axios.post(
      `${API_URL}/auth/forgot-password`,
      { email },
      { withCredentials: true }
    );
    return response.data;
  } catch (error) {
    console.error("Error sending email", error);
    throw error;
  }
};
