package by.michael.noughtsandcrosses.datasource.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
public class FieldEntity {
  public static final int FIELD_WIDTH = 3;
  public static final int FIELD_HEIGHT = 3;

  @Convert(converter = IntMatrixConverter.class)
  @Column(name = "field", nullable = false)
  @Getter
  @Setter
  private int[][] field;

  public FieldEntity(int[][] fieldMatrix) {
    this.field = new int[FIELD_HEIGHT][FIELD_WIDTH];
    for (int i = 0; i < FIELD_HEIGHT; i++) {
      System.arraycopy(fieldMatrix[i], 0, this.field[i], 0, FIELD_WIDTH);
    }
  }
}
