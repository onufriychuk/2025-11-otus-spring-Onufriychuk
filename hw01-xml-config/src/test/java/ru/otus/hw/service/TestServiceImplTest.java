package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {
    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;

    private TestServiceImpl testService;

    @BeforeEach
    void setUp() {
        testService = new TestServiceImpl(ioService, questionDao);
    }
    @Test
    void executeTest_ShouldPrintHeaderAndNoQuestionsMessage_WhenDaoReturnsEmptyList() {
        when(questionDao.findAll()).thenReturn(List.of());

        testService.executeTest();

        verify(ioService).printLine("");
        verify(ioService).printFormattedLine("Please answer the questions below%n");
        verify(ioService).printLine("No questions available.");
        verifyNoMoreInteractions(ioService);
    }

    @Test
    void executeTest_ShouldPrintQuestionsWithAnswers_WhenDaoReturnsSingleQuestion() {
        List<Answer> answers = List.of(
                new Answer("Answer 1", true),
                new Answer("Answer 2", false)
        );
        Question question = new Question("What is Java?", answers);
        when(questionDao.findAll()).thenReturn(List.of(question));

        testService.executeTest();

        InOrder inOrder = inOrder(ioService);
        inOrder.verify(ioService).printLine("");
        inOrder.verify(ioService).printFormattedLine("Please answer the questions below%n");
        inOrder.verify(ioService).printFormattedLine("Question %d: %s", 1, "What is Java?");
        inOrder.verify(ioService).printFormattedLine("  %d. %s", 1, "Answer 1");
        inOrder.verify(ioService).printFormattedLine("  %d. %s", 2, "Answer 2");
        verifyNoMoreInteractions(ioService);
    }

    @Test
    void executeTest_ShouldPrintMultipleQuestionsWithSeparator_WhenDaoReturnsMultipleQuestions() {
        List<Answer> answers1 = List.of(
                new Answer("Yes", true),
                new Answer("No", false)
        );
        List<Answer> answers2 = List.of(
                new Answer("Option A", false),
                new Answer("Option B", true),
                new Answer("Option C", false)
        );
        List<Question> questions = List.of(
                new Question("Question 1?", answers1),
                new Question("Question 2?", answers2)
        );
        when(questionDao.findAll()).thenReturn(questions);

        testService.executeTest();

        InOrder inOrder = inOrder(ioService);
        inOrder.verify(ioService).printLine("");
        inOrder.verify(ioService).printFormattedLine("Please answer the questions below%n");

        // First question
        inOrder.verify(ioService).printFormattedLine("Question %d: %s", 1, "Question 1?");
        inOrder.verify(ioService).printFormattedLine("  %d. %s", 1, "Yes");
        inOrder.verify(ioService).printFormattedLine("  %d. %s", 2, "No");
        inOrder.verify(ioService).printLine(""); // separator

        // Second question
        inOrder.verify(ioService).printFormattedLine("Question %d: %s", 2, "Question 2?");
        inOrder.verify(ioService).printFormattedLine("  %d. %s", 1, "Option A");
        inOrder.verify(ioService).printFormattedLine("  %d. %s", 2, "Option B");
        inOrder.verify(ioService).printFormattedLine("  %d. %s", 3, "Option C");

        verifyNoMoreInteractions(ioService);
    }

    @Test
    void executeTest_ShouldPrintNoAnswerMessage_WhenQuestionHasNoAnswers() {
        Question question = new Question("Question without answers?", List.of());
        when(questionDao.findAll()).thenReturn(List.of(question));

        testService.executeTest();

        InOrder inOrder = inOrder(ioService);
        inOrder.verify(ioService).printLine("");
        inOrder.verify(ioService).printFormattedLine("Please answer the questions below%n");
        inOrder.verify(ioService).printFormattedLine("Question %d: %s", 1, "Question without answers?");
        inOrder.verify(ioService).printLine("  No answer options available.");
        verifyNoMoreInteractions(ioService);
    }

    @Test
    void executeTest_ShouldHandleNullAnswerList() {
        Question question = new Question("Question with null answers?", null);
        when(questionDao.findAll()).thenReturn(List.of(question));

        testService.executeTest();

        InOrder inOrder = inOrder(ioService);
        inOrder.verify(ioService).printLine("");
        inOrder.verify(ioService).printFormattedLine("Please answer the questions below%n");
        inOrder.verify(ioService).printFormattedLine("Question %d: %s", 1, "Question with null answers?");
        inOrder.verify(ioService).printLine("  No answer options available.");
        verifyNoMoreInteractions(ioService);
    }
}