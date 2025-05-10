package org.example.akigatorapp.controllers;

import org.example.akigatorapp.models.*;
import org.example.akigatorapp.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/game")
public class GameController {

    private final GameSessionRepository gameSessionRepository;
    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final EntityRepository entityRepository;
    private final AnswerRepository answerRepository;

    @Autowired
    public GameController(GameSessionRepository gameSessionRepository, QuestionRepository questionRepository, CategoryRepository categoryRepository, UserRepository userRepository, EntityRepository entityRepository, AnswerRepository answerRepository) {
        this.gameSessionRepository = gameSessionRepository;
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.entityRepository = entityRepository;
        this.answerRepository = answerRepository;
    }

    @GetMapping("/new-session")
    public String newSessionPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        model.addAttribute("categories", categoryRepository.findAll());
        return "new-session";
    }

    @PostMapping("/start")
    public String startGame(@RequestParam("category") Long categoryId,
                            Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return "error";
        }
        UserEntity user = userOptional.get();

        Optional<CategoryEntity> categoryOptional = categoryRepository.findByCategoryId(categoryId);
        if (categoryOptional.isEmpty()) {
            return "error";
        }
        CategoryEntity category = categoryOptional.get();

        runPythonScript("start", null);

        GameSessionEntity session = new GameSessionEntity();
        session.setUser(user);
        session.setCategory(category);
        session.setStartTime(OffsetDateTime.now());
        session.setEndTime(null);
        session.setCompleted(false);
        session.setResult(ResultName.UNRESOLVED);
        gameSessionRepository.save(session);
        gameSessionRepository.flush();


        return "redirect:/game/play?sessionId=" + session.getGameSessionId();
    }

    private ScriptResult runPythonScript(String mode, Long answerId) {
        try {
            ProcessBuilder pb;

            if (answerId == null) {
                pb = new ProcessBuilder("python", "C:\\AkigatorML\\AkigatorApp\\ml_service.py", "--mode", mode);
            } else {
                pb = new ProcessBuilder("python", "C:\\AkigatorML\\AkigatorApp\\ml_service.py", "--mode", mode, "--answer_id", String.valueOf(answerId));
            }

            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            String guess = null;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                if (line.startsWith("GUESS=")) {
                    guess = line.substring(6).trim();
                }
            }


            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return new ScriptResult(output.toString(), guess);
            } else {
                return new ScriptResult("Error: Exit code " + exitCode, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ScriptResult("Error running Python script: " + e.getMessage(), null);
        }
    }

    @GetMapping("/play")
    public String playGame(@RequestParam("sessionId") Long sessionId,
                           @RequestParam(value = "questionId", required = false) Long questionId,
                           Model model) {
        Optional<GameSessionEntity> sessionOptional = gameSessionRepository.findByGameSessionId(sessionId);
        if (sessionOptional.isEmpty()) {
            return "error";
        }

        GameSessionEntity session = sessionOptional.get();
        List<QuestionEntity> questions = questionRepository.findByCategory(session.getCategory());
        if (questions.isEmpty()) {
            return "error";
        }

        QuestionEntity currentQuestion = null;
        if (questionId != null) {
            Optional<QuestionEntity> currentQuestionOptional = questions.stream()
                    .filter(q -> q.getQuestionId().equals(questionId))
                    .findFirst();
            currentQuestion = currentQuestionOptional.orElse(null);
        } else {
            currentQuestion = questions.get(0);
        }

        if (currentQuestion == null) {
            return "error";
        }

        int currentIndex = questions.indexOf(currentQuestion);
        QuestionEntity nextQuestion = (currentIndex < questions.size() - 1) ? questions.get(currentIndex + 1) : null;

        List<AnswerEntity> possibleAnswers = answerRepository.findByQuestion(currentQuestion);

        model.addAttribute("sessionId", sessionId);
        model.addAttribute("question", currentQuestion);
        model.addAttribute("nextQuestionId", nextQuestion != null ? nextQuestion.getQuestionId() : null);
        model.addAttribute("answers", possibleAnswers);

        return "play-game";
    }


    @PostMapping("/answer")
    public String answerQuestion(@RequestParam("sessionId") Long sessionId,
                                 @RequestParam("questionId") Long questionId,
                                 @RequestParam("answer") Long selectedAnswerId,
                                 @RequestParam(value = "nextQuestionId", required = false) Long nextQuestionId,
                                 Model model) {

        Optional<GameSessionEntity> sessionOptional = gameSessionRepository.findByGameSessionId(sessionId);
        if (sessionOptional.isEmpty()) {
            return "error";
        }

        Optional<QuestionEntity> questionOptional = questionRepository.findById(questionId);
        if (questionOptional.isEmpty()) {
            return "error";
        }

        QuestionEntity question = questionOptional.get();
        List<AnswerEntity> answers = answerRepository.findByQuestion(question);

        if (answers.stream().noneMatch(a -> a.getAnswerId().equals(selectedAnswerId))) {
            return "error";
        }

        Optional<AnswerEntity> selectedAnswer = answerRepository.findById(selectedAnswerId);
        if (selectedAnswer.isEmpty()) {
            return "error";
        }

        ScriptResult result = runPythonScript("next", selectedAnswer.get().getAnswerId());
        if (result.getGuess() != null && !result.getGuess().isBlank()) {
            return "redirect:/game/guess?sessionId=" + sessionId;
        }

        List<QuestionEntity> updatedQuestions = questionRepository.findByCategory(sessionOptional.get().getCategory());

        if (updatedQuestions.isEmpty()) {
            return "redirect:/game/guess?sessionId=" + sessionId;
        }

        QuestionEntity newestQuestion = updatedQuestions.get(updatedQuestions.size() - 1);
        return "redirect:/game/play?sessionId=" + sessionId + "&questionId=" + newestQuestion.getQuestionId();
    }

    @GetMapping("/guess")
    public String makeGuess(@RequestParam("sessionId") Long sessionId, Model model) {
        Optional<GameSessionEntity> sessionOptional = gameSessionRepository.findByGameSessionId(sessionId);
        if (sessionOptional.isEmpty()) {
            return "error";
        }

        ScriptResult scriptResult = runPythonScript("next", null);
        String guess = scriptResult.getGuess() != null ? scriptResult.getGuess() : "Unknown Character";

        model.addAttribute("sessionId", sessionId);
        model.addAttribute("guess", guess);
        return "guess-result";
    }

    @PostMapping("/guess-result")
    public String processGuess(@RequestParam("sessionId") Long sessionId, @RequestParam("correct") boolean correct) {
        Optional<GameSessionEntity> sessionOptional = gameSessionRepository.findByGameSessionId(sessionId);
        if (sessionOptional.isEmpty()) {
            return "error";
        }
        GameSessionEntity session = sessionOptional.get();

        session.setEndTime(OffsetDateTime.now());
        session.setCompleted(true);
        session.setResult(correct ? ResultName.LOSS : ResultName.WIN);

        gameSessionRepository.save(session);

        return "redirect:/game/game-sessions";
    }


    @PostMapping("/create-session")
    public String createSession() {
        GameSessionEntity session = new GameSessionEntity();
        session.setStartTime(OffsetDateTime.from(LocalDateTime.now()));
        session.setCompleted(false);
        gameSessionRepository.save(session);
        return "redirect:/game/category";
    }

    @GetMapping("/game-sessions")
    public String listSessions(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/auth/login";
        }

        Optional<UserEntity> userOptional = userRepository.findByUsername(auth.getName());
        if (userOptional.isEmpty()) {
            return "error";
        }

        UserEntity user = userOptional.get();
        List<GameSessionEntity> gameSessions = gameSessionRepository.findByUser(user);

        List<GameSessionEntity> winSessions = gameSessions.stream()
                .filter(session -> session.getResult() == ResultName.WIN)
                .sorted((s1, s2) -> s2.getStartTime().compareTo(s1.getStartTime()))
                .toList();

        List<GameSessionEntity> lossSessions = gameSessions.stream()
                .filter(session -> session.getResult() == ResultName.LOSS)
                .sorted((s1, s2) -> s2.getStartTime().compareTo(s1.getStartTime()))
                .toList();

        List<GameSessionEntity> topSessions = winSessions.stream().limit(3).toList();

        model.addAttribute("topSessions", topSessions);
        model.addAttribute("winSessions", winSessions);
        model.addAttribute("lossSessions", lossSessions);

        return "game-sessions";
    }

}