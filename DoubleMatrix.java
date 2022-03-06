import java.util.Arrays;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.nio.file.Paths;
import java.nio.file.Files;


public class DoubleMatrix {

  public static final String DEFAULT_DELIM = " ";

  public static void writeToFile(DoubleMatrix val, String filename, String delim)throws IOException {
    try(BufferedWriter file = Files.newBufferedWriter(Paths.get(filename))) {
      String buf = delim.equals(DEFAULT_DELIM)? val.toString() : val.toString().replace(DEFAULT_DELIM, delim);
      file.write(buf, 0, buf.length());
      file.flush();
    }
    catch(IOException ioe) {
      throw ioe;
    }
  }

  public static void writeToFile(DoubleMatrix val, String filename)throws IOException {
    writeToFile(val, filename, DEFAULT_DELIM);
  }

  public static DoubleMatrix readFromFile(String filename, String delim)throws IOException {
    ArrayList<double[]> rows = new ArrayList<double[]>();
    String line = null;

    try(BufferedReader file = Files.newBufferedReader(Paths.get(filename))) {
      while((line = file.readLine()) != null) {
        StringTokenizer tokenizer = new StringTokenizer(line, delim);
        double[] row = new double[tokenizer.countTokens()];
        for(int j = 0; j < row.length; j++) {
          row[j] = Double.parseDouble(tokenizer.nextToken());
        }
        rows.add(row);
      }
    }
    catch(NumberFormatException nfe) {
      System.err.printf("[エラー] %s:%d: %s\n", filename, rows.size() + 1, line);
      nfe.printStackTrace();
      System.exit(1);
    }
    catch(IOException ioe) {
      throw ioe;
    }

    double[][] result = rows.toArray(new double[rows.size()][]);
    return (new DoubleMatrix(result, true, false));
  }

  public static DoubleMatrix readFromFile(String filename)throws IOException {
    return readFromFile(filename, DEFAULT_DELIM);
  }

  public static DoubleMatrix createDiagonalMatrix(double... entries) {
    double[][] result = new double[entries.length][entries.length];
    for(int i = 0; i < entries.length; i++) {
      result[i][i] = entries[i];
    }
    return (new DoubleMatrix(result, false, false));
  }

  public static DoubleMatrix createIdentityMatrix(int n) {
    double[][] result = new double[n][n];
    for(int i = 0; i < n; i++) {
      result[i][i] = 1;
    }
    return (new DoubleMatrix(result, false, false));
  }

  public static boolean isSymmetricMatrix(DoubleMatrix val) {
    if(val.rows != val.columns) {
      return false;
    }

    for(int i = 0; i < val.rows; i++) {
      for(int j = 0; j < val.columns; j++) {
        if(i == j) {
          continue;
        }

        if(val.get(i, j) != val.get(j, i)) {
          return false;
        }
      }
    }

    return true;
  }

  private final double[][] matrix;
  public final int rows;
  public final int columns;
  public final int size;

  private DoubleMatrix(double[][] matrix, boolean doValidate, boolean doCopy) {
    if(doValidate) {
      for(int i = 1; i < matrix.length; i++) {
        if(matrix[i].length != matrix[0].length) {
          StringBuilder errMsgBuf = new StringBuilder("行列として解釈できません\n");
          final int loc = i;
          for(i = 0; i < matrix.length; i++) {
            errMsgBuf.append(Arrays.toString(matrix[i]));
            if(i == loc) {
              errMsgBuf.append(" <--\n");
            } else {
              errMsgBuf.append("\n");
            }
          }
          throw (new IllegalArgumentException(errMsgBuf.toString()));
        }
      }
    }

    this.rows = matrix.length;
    this.columns = matrix[0].length;
    this.size = matrix.length * matrix[0].length;

    if(doCopy) {
      this.matrix = new double[this.rows][this.columns];
      for(int i = 0; i < this.rows; i++) {
        System.arraycopy(matrix[i], 0, this.matrix[i], 0, this.columns);
      }
    } else {
      this.matrix = matrix;
    }
  }

  public DoubleMatrix(double[][] matrix) {
    this(matrix, true, true);
  }

  public DoubleMatrix(int rows, int columns) {
    this(new double[rows][columns], false, false);
  }

  public DoubleMatrix(int rows, int columns, double... entries) {
    this(new double[rows][columns], false, false);

    if(entries.length > this.size) {
      throw (new IllegalArgumentException("第3引数以降の成分の数が多すぎます"));
    } else if(entries.length < this.size) {
      throw (new IllegalArgumentException("第3引数以降の成分の数が少なすぎます"));
    }

    for(int i = 0; i < this.rows; i++) {
      for(int j = 0; j < this.columns; j++) {
        this.matrix[i][j] = entries[i * this.columns + j];
      }
    }
  }

  public boolean isTypeEqual(DoubleMatrix val) {
    return (this.rows == val.rows && this.columns == val.columns);
  }

  public boolean isEqual(DoubleMatrix val) {
    if(this == val) {
      return true;
    }

    if(!this.isTypeEqual(val)) {
      return false;
    }

    for(int i = 0; i < this.rows; i++) {
      for(int j = 0; j < this.columns; j++) {
        if(this.get(i, j) != val.get(i, j)) {
          return false;
        }
      }
    }

    return true;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();

    for(int i = 0; i < this.rows; i++) {
      result.append(this.matrix[i][0]);
      for(int j = 1; j < this.columns; j++) {
        result.append(DEFAULT_DELIM);
        result.append(this.matrix[i][j]);
      }
      result.append("\n");
    }

    return result.toString();
  }

  public double get(int i, int j) {
    return this.matrix[i][j];
  }

  public void set(int i, int j, double entry) {
    this.matrix[i][j] = entry;
  }

  public void swapRows(int i1, int i2) {
    // 引数が同じなら交換処理を行う必要がないので何もせずreturnする
    if(i1 == i2) {
      // ただし，引数が配列の添え字範囲を逸脱していない場合に限る
      // 逸脱している場合はそのまま交換処理を実行させ，例外を発生させる
      if((0 <= i1 && i1 < this.rows) && (0 <= i2 && i2 < this.rows)) {
        return;
      }
    }

    double[] tmp = this.matrix[i1];
    this.matrix[i1] = this.matrix[i2];
    this.matrix[i2] = tmp;
  }

  public void swapColumns(int j1, int j2) {
    // 引数が同じなら交換処理を行う必要がないので何もせずreturnする
    if(j1 == j2) {
      // ただし，引数が配列の添え字範囲を逸脱していない場合に限る
      // 逸脱している場合はそのまま交換処理を実行させ，例外を発生させる
      if((0 <= j1 && j1 < this.columns) && (0 <= j2 && j2 < this.columns)) {
        return;
      }
    }

    for(int i = 0; i < this.rows; i++) {
      double tmp = this.matrix[i][j1];
      this.matrix[i][j1] = this.matrix[i][j2];
      this.matrix[i][j2] = tmp;
    }
  }

  public DoubleMatrix add(DoubleMatrix val) {
    if(!this.isTypeEqual(val)) {
      return null;
    }

    double[][] result = new double[this.rows][this.columns];
    for(int i = 0; i < this.rows; i++) {
      for(int j = 0; j < this.columns; j++) {
        result[i][j] = this.get(i, j) + val.get(i, j);
      }
    }

    return (new DoubleMatrix(result, false, false));
  }

  public DoubleMatrix addeq(DoubleMatrix val) {
    if(!this.isTypeEqual(val)) {
      return null;
    }

    for(int i = 0; i < this.rows; i++) {
      for(int j = 0; j < this.columns; j++) {
        this.matrix[i][j] += val.get(i, j);
      }
    }

    return this;
  }

  public DoubleMatrix sub(DoubleMatrix val) {
    if(!this.isTypeEqual(val)) {
      return null;
    }

    double[][] result = new double[this.rows][this.columns];
    for(int i = 0; i < this.rows; i++) {
      for(int j = 0; j < this.columns; j++) {
        result[i][j] = this.get(i, j) - val.get(i, j);
      }
    }

    return (new DoubleMatrix(result, false, false));
  }

  public DoubleMatrix subeq(DoubleMatrix val) {
    if(!this.isTypeEqual(val)) {
      return null;
    }

    for(int i = 0; i < this.rows; i++) {
      for(int j = 0; j < this.columns; j++) {
        this.matrix[i][j] -= val.get(i, j);
      }
    }

    return this;
  }

  public DoubleMatrix mul(double k) {
    double[][] result = new double[this.rows][this.columns];
    for(int i = 0; i < this.rows; i++) {
      for(int j = 0; j < this.columns; j++) {
        result[i][j] = k * this.get(i, j);
      }
    }

    return (new DoubleMatrix(result, false, false));
  }

  public DoubleMatrix mul(DoubleMatrix val) {
    if(this.columns != val.rows) {
      return null;
    }

    double[][] result = new double[this.rows][val.columns];
    for(int i = 0; i < this.rows; i++) {
      for(int j = 0; j < val.columns; j++) {
        for(int k = 0; k < this.columns; k++) { // or (k < val.rows)
          result[i][j] += this.get(i, k) * val.get(k, j);
        }
      }
    }

    return (new DoubleMatrix(result, false, false));
  }

  public DoubleMatrix muleq(double k) {
    for(int i = 0; i < this.rows; i++) {
      for(int j = 0; j < this.columns; j++) {
        this.matrix[i][j] *= k;
      }
    }
    return this;
  }

  public DoubleMatrix trs() {
    double[][] result = new double[this.columns][this.rows];
    for(int i = 0; i < this.rows; i++) {
      for(int j = 0; j < this.columns; j++) {
        result[j][i] = this.matrix[i][j];
      }
    }

    return (new DoubleMatrix(result, false, false));
  }
}
