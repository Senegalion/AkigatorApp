# 🐊 Akigator - Interactive Web Application with Machine Learning  

Akigator is an intelligent guessing game inspired by Akinator, where the computer asks questions to guess what you're thinking. Built using **Spring Boot** for the backend and **React with Tailwind CSS** for the frontend, it leverages **machine learning principles** to refine its guesses over time. Hosted and deployed on **AWS infrastructure** for scalability and reliability.

## 🌐 Live Demo 
You can try the app live here:
👉 [live-demo](http://ec2-34-238-157-217.compute-1.amazonaws.com/) (hosted on AWS)

![image](https://github.com/user-attachments/assets/08705e3b-feb5-406b-9399-faac1e5d33b0)


## 🚀 Features  

✅ **Interactive Q&A System** – The computer asks a series of questions to deduce your answer.  
✅ **Machine Learning Enhancement** – The system improves over time based on past answers.  
✅ **User Authentication** – Secure login and game session tracking.  
✅ **Game History** – Review previous sessions and results.  
✅ **Admin Panel** – Manage questions, categories, and game logic.  

## 🎯 Project Goals  

1️⃣ **Concept & Idea** – Brainstorming and defining core functionality.  
2️⃣ **Initial Implementation** – Setting up backend logic, database, and basic UI.  
3️⃣ **Refinement & UX Enhancements** – Improving AI accuracy and refining the interface.  

## 🛠️ Tech Stack  

- **Backend**: Java, Spring Boot, Spring Security  
- **Frontend**: React, JavaScript, Tailwind CSS
- **Database**: PostgreSQL (hosted on AWS)
- **Authentication**: Spring Security  
- **Machine Learning**: Integrated ML models on backend
- **Cloud Infrastructure**: AWS (EC2, RDS, S3, etc.)
- **Version Control**: Git & GitHub

## 🔧 Installation & Setup  

1. **Clone the repository:**  
   ```bash
   git clone https://github.com/your-username/akigator.git
   cd akigator

2. Backend setup:

   - Configure PostgreSQL on Docker (local or AWS RDS).
   - Update backend configuration in src/main/resources/application.properties.
   - Run backend:
      ```bash
      ./mvnw spring-boot:run

3. Frontend setup:

   - Navigate to frontend directory:
      ```bash
      cd frontend
   
   - Install dependencies and start React app:
      ```bash
      npm install
      npm start

4. Access the app:

   - Frontend usually runs on http://localhost:3000
   
   - Backend API on http://localhost:8080 (ensure CORS configured)


## 📂 Folder Structure  
/akigator  
├── backend/  
│   ├── src/main/java/org/example/akigatorapp  
│   └── src/main/resources/application.properties  
├── frontend/  
│   ├── src/  
│   ├── tailwind.config.js  
│   └── package.json  
└── README.md  


## 🏗️ Future Improvements  
🔹 More accurate guessing algorithms  
🔹 Dark mode & improved UI/UX  
🔹 Optimize for mobile devices  
🔹 Enhance guessing algorithms with more advanced machine learning techniques  

## 🏆 Contributors  
👨‍💻 Łukasz Pelikan - Fullstack Java Developer  
👨‍🎨 Ksawery Raszczak - Data Engineer  

## 📬 Contact & Support  
For any issues or feature requests, please open an issue.  
Happy coding! 🚀  
