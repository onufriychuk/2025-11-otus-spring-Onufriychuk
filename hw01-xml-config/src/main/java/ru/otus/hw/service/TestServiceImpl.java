package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        List<Question> questions = questionDao.findAll();
        if (questions.isEmpty()) {
            ioService.printLine("No questions available.");
            return;
        }
        printQuestions(questions);
    }

    private void printQuestions(List<Question> questions) {
        for (int i = 0; i < questions.size(); i++) {
            printQuestion(i + 1, questions.get(i));
            if (i < questions.size() - 1) {
                ioService.printLine("");
            }
        }
    }

    private void printQuestion(int number, Question question) {
        ioService.printFormattedLine("Question %d: %s", number, question.text());
        List<Answer> answers = question.answers();
        if (answers == null || answers.isEmpty()) {
            ioService.printLine("  No answer options available.");
            return;
        }
        for (int j = 0; j < answers.size(); j++) {
            Answer answer = answers.get(j);
            ioService.printFormattedLine("  %d. %s", j + 1, answer.text());
        }
    }
}
