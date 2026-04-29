package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        try (InputStream inputStream = openTestFileAsStream()) {
            InputStreamReader reader = new InputStreamReader(inputStream);
            return parseCsvToQuestions(reader);
        } catch (IOException e) {
            throw new QuestionReadException("Failed to read questions file", e);
        }
    }

    private InputStream openTestFileAsStream() {
        String fileName = fileNameProvider.getTestFileName();
        InputStream inputStream = getClass().getResourceAsStream("/" + fileName);
        if (inputStream == null) {
            throw new QuestionReadException("Questions file not found: " + fileName);
        }
        return inputStream;
    }

    private List<Question> parseCsvToQuestions(InputStreamReader reader) {
        try {
            return new CsvToBeanBuilder<QuestionDto>(reader)
                    .withType(QuestionDto.class)
                    .withSkipLines(1)
                    .withSeparator(';')
                    .build()
                    .parse()
                    .stream()
                    .map(QuestionDto::toDomainObject)
                    .toList();
        } catch (RuntimeException e) {
            throw new QuestionReadException("Error parsing CSV data", e);
        }
    }
}
