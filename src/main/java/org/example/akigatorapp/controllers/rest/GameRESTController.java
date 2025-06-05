package org.example.akigatorapp.controllers.rest;

import org.example.akigatorapp.models.*;
import org.example.akigatorapp.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "http://localhost:3000")
public class GameRESTController {
    private final GameSessionRepository gameSessionRepository;
    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final EntityRepository entityRepository;
    private final AnswerRepository answerRepository;

    private String latestGuess = null;

    @Autowired
    public GameRESTController(GameSessionRepository gameSessionRepository,
                              QuestionRepository questionRepository,
                              CategoryRepository categoryRepository,
                              UserRepository userRepository,
                              EntityRepository entityRepository,
                              AnswerRepository answerRepository) {
        this.gameSessionRepository = gameSessionRepository;
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.entityRepository = entityRepository;
        this.answerRepository = answerRepository;
    }

    @GetMapping("/categories")
    public List<CategoryDto> getAllCategories() {
        List<CategoryEntity> categories = categoryRepository.findAll();

        return categories.stream()
                .map(category -> new CategoryDto(category.getCategoryId(), category.getName(), category.getDescription()))
                .collect(Collectors.toList());
    }

    @PostMapping("/start")
    public ResponseEntity<?> startGame(@RequestParam("categoryId") Long categoryId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> userOpt = userRepository.findByUsername(auth.getName());
        Optional<CategoryEntity> categoryOpt = categoryRepository.findByCategoryId(categoryId);

        if (userOpt.isEmpty() || categoryOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid user or category");
        }

        runPythonScript("start", null);

        GameSessionEntity session = new GameSessionEntity();
        session.setUser(userOpt.get());
        session.setCategory(categoryOpt.get());
        session.setStartTime(OffsetDateTime.now());
        session.setEndTime(null);
        session.setCompleted(false);
        session.setResult(ResultName.UNRESOLVED);

        gameSessionRepository.saveAndFlush(session);
        return ResponseEntity.ok(new GameSessionDto(
                session.getGameSessionId(),
                session.getCategory().getName(),
                session.getStartTime().toString(),
                session.getDuration(),
                session.getResult().toString()
        ));
    }

    @GetMapping("/play")
    public ResponseEntity<?> playGame(@RequestParam("sessionId") Long sessionId,
                                      @RequestParam(value = "questionId", required = false) Long questionId) {
        Optional<GameSessionEntity> sessionOpt = gameSessionRepository.findByGameSessionId(sessionId);
        if (sessionOpt.isEmpty()) return ResponseEntity.badRequest().body("Invalid session");

        GameSessionEntity session = sessionOpt.get();
        List<QuestionEntity> questions = questionRepository.findByCategory(session.getCategory());
        if (questions.isEmpty()) return ResponseEntity.badRequest().body("No questions found");

        QuestionEntity currentQuestion = questionId == null
                ? questions.get(0)
                : questions.stream().filter(q -> q.getQuestionId().equals(questionId)).findFirst().orElse(null);

        if (currentQuestion == null) return ResponseEntity.badRequest().body("Invalid question");

        List<AnswerEntity> answers = answerRepository.findByQuestion(currentQuestion);
        return ResponseEntity.ok(Map.of(
                "question", new QuestionDto(currentQuestion),
                "answers", answers.stream().map(AnswerDto::new).toList()
        ));
    }

    @PostMapping("/answer")
    public ResponseEntity<?> answerQuestion(@RequestParam("sessionId") Long sessionId,
                                            @RequestParam("questionId") Long questionId,
                                            @RequestParam("answerId") Long answerId) {
        Optional<GameSessionEntity> sessionOpt = gameSessionRepository.findByGameSessionId(sessionId);
        Optional<QuestionEntity> questionOpt = questionRepository.findById(questionId);
        Optional<AnswerEntity> answerOpt = answerRepository.findById(answerId);

        if (sessionOpt.isEmpty() || questionOpt.isEmpty() || answerOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid input");
        }

        ScriptResult result = runPythonScript("next", answerId);

        if (result.getGuess() != null && !result.getGuess().isBlank()) {
            latestGuess = result.getGuess();
            return ResponseEntity.ok(Map.of("guess", result.getGuess(), "guessAvailable", true));
        }

        List<QuestionEntity> updatedQuestions = questionRepository.findByCategory(sessionOpt.get().getCategory());
        if (updatedQuestions.isEmpty()) {
            return ResponseEntity.ok(Map.of("guessAvailable", true));
        }

        QuestionEntity newestQuestion = updatedQuestions.get(updatedQuestions.size() - 1);
        return ResponseEntity.ok(Map.of("nextQuestionId", newestQuestion.getQuestionId(), "guessAvailable", false));
    }

    @GetMapping("/guess")
    public ResponseEntity<?> getGuess(@RequestParam("sessionId") Long sessionId) {
        Optional<GameSessionEntity> sessionOpt = gameSessionRepository.findByGameSessionId(sessionId);
        if (sessionOpt.isEmpty()) return ResponseEntity.badRequest().body("Invalid session");

        String guess = latestGuess != null ? latestGuess : "Unknown";
        latestGuess = null;

        return ResponseEntity.ok(Map.of("guess", guess));
    }

    @PostMapping("/guess-result")
    public ResponseEntity<?> processGuess(@RequestParam("sessionId") Long sessionId,
                                          @RequestParam("correct") boolean correct) {
        Optional<GameSessionEntity> sessionOpt = gameSessionRepository.findByGameSessionId(sessionId);
        if (sessionOpt.isEmpty()) return ResponseEntity.badRequest().body("Invalid session");

        GameSessionEntity session = sessionOpt.get();
        session.setCompleted(true);
        session.setEndTime(OffsetDateTime.now());
        session.setResult(correct ? ResultName.LOSS : ResultName.WIN);

        gameSessionRepository.save(session);
        return ResponseEntity.ok("Session ended");
    }

    @GetMapping("/sessions")
    public ResponseEntity<?> getUserSessions() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> userOpt = userRepository.findByUsername(auth.getName());

        if (userOpt.isEmpty()) return ResponseEntity.status(401).body("Unauthorized");

        List<GameSessionEntity> allSessions = gameSessionRepository.findByUser(userOpt.get());

        List<GameSessionDto> wins = allSessions.stream()
                .filter(s -> s.getResult() == ResultName.WIN)
                .sorted(Comparator.comparing(GameSessionEntity::getStartTime).reversed())
                .map(this::mapToDto)
                .toList();

        List<GameSessionDto> losses = allSessions.stream()
                .filter(s -> s.getResult() == ResultName.LOSS)
                .sorted(Comparator.comparing(GameSessionEntity::getStartTime).reversed())
                .map(this::mapToDto)
                .toList();

        List<GameSessionDto> topWins = wins.stream().limit(3).toList();

        Map<String, Object> result = new HashMap<>();
        result.put("wins", wins);
        result.put("losses", losses);
        result.put("topWins", topWins);

        return ResponseEntity.ok(result);
    }

    private GameSessionDto mapToDto(GameSessionEntity session) {
        String duration = "N/A";
        if (session.getEndTime() != null) {
            long seconds = session.getEndTime().toEpochSecond() - session.getStartTime().toEpochSecond();
            long minutes = seconds / 60;
            long remainingSeconds = seconds % 60;
            duration = String.format("%d:%02d", minutes, remainingSeconds);
        }

        return new GameSessionDto(
                session.getGameSessionId(),
                session.getCategory().getName(),
                session.getStartTime().toString(),
                duration,
                session.getResult().name()
        );
    }

    private ScriptResult runPythonScript(String mode, Long answerId) {
        try {
            String projectRoot = new File(".").getCanonicalPath();
            String scriptPath = new File(projectRoot, "ml_service.py").getAbsolutePath();

            ProcessBuilder pb = (answerId == null)
                    ? new ProcessBuilder("python", scriptPath, "--mode", mode)
                    : new ProcessBuilder("python", scriptPath, "--mode", mode, "--answer_id", String.valueOf(answerId));

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
            return (exitCode == 0) ? new ScriptResult(output.toString(), guess) : new ScriptResult("Error: Exit code " + exitCode, null);
        } catch (Exception e) {
            return new ScriptResult("Script error: " + e.getMessage(), null);
        }
    }
}
