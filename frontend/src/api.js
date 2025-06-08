import axios from "axios";

const API_URL = process.env.REACT_APP_API_URL;

export const sendResetPasswordEmail = async (email) => {
  try {
    const response = await axios.post(
      `${API_URL}/api/auth/forgot-password`,
      { email },
      { withCredentials: true }
    );
    return response.data;
  } catch (error) {
    console.error("Error sending email", error);
    throw error;
  }
};
