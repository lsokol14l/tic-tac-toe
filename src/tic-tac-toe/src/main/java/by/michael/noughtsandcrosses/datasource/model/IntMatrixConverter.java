package by.michael.noughtsandcrosses.datasource.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Конвертер для хранения int[][] в PostgreSQL как красивую строку =).
 *
 * <p>JPA вызывает: - convertToDatabaseColumn() при СОХРАНЕНИИ в БД (int[][] → String) -
 * convertToEntityAttribute() при ЧТЕНИИ из БД (String → int[][])
 */
@Converter
public class IntMatrixConverter implements AttributeConverter<int[][], String> {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(int[][] matrix) {
    try {
      return objectMapper.writeValueAsString(matrix);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Error converting matrix to JSON", e);
    }
  }

  @Override
  public int[][] convertToEntityAttribute(String json) {
    try {
      return objectMapper.readValue(json, int[][].class);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Error converting JSON to matrix", e);
    }
  }
}
