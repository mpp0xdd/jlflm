import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

/**
 * double型2次元配列をラップし，行列として扱えるようにするクラスです。<br>
 * つまり，このクラスにラップされたdouble型2次元配列は，a.length * a[0].lengthの計算結果の値が<br>
 * 2次元配列の全ての要素数に等しいことが保証されます。<br>
 * <br>
 * また，このクラスはラップ元のdouble型2次元配列の完全なコピーを内部に保持するという仕様のため，<br>
 * ラップ元配列の値に変更があっても，その影響を受けません。
 *
 * @author mpp
 */
public class DoubleMatrix {

  /**
   * この行列を文字列として表現するとき(オーバライドされたtoString()の呼び出し時)に各成分の間に挿入される区切り文字を表します。<br>
   * オーバライドされたtoString()は，ここで指定された区切り文字を使用して，行列の内容を文字列に変換します。<br>
   * また，この値を変更した場合，toString()のjavadocに記載されている実行例の変更も同時に行う必要があることに注意してください。
   *
   * @see #toString()
   */
  private static final String DEFAULT_DELIM = " ";

  /**
   * 行列の文字列表現を指定された区切り文字を使用してファイルに書き込みます。<br>
   * 以下は，行列をCSVファイルとして書き出す例です。
   *
   * <pre>{@code
   * DoubleMatrix a = DoubleMatrix.from(
   *   new double[][]{
   *     {1, 2},
   *     {1, 2},
   *     {1, 2},
   *   }
   * );
   * DoubleMatrix.writeToFile(a, "mat.csv", ",");
   * }</pre>
   *
   * @param matrix 行列
   * @param filename ファイル名
   * @param delim 各要素間の区切り文字
   * @throws IOException 入出力エラーが発生した場合
   */
  public static void writeToFile(DoubleMatrix matrix, String filename, String delim)
      throws IOException {
    try (BufferedWriter file = Files.newBufferedWriter(Paths.get(filename))) {
      file.write(matrix.toString(delim));
      file.flush();
    } catch (IOException ioe) {
      throw ioe;
    }
  }

  /**
   * 行列の文字列表現(toString()の実行結果)をファイルに書き込みます。
   *
   * @param matrix 行列
   * @param filename ファイル名
   * @throws IOException 入出力エラーが発生した場合
   * @see #toString()
   */
  public static void writeToFile(DoubleMatrix matrix, String filename) throws IOException {
    writeToFile(matrix, filename, DEFAULT_DELIM);
  }

  /**
   * 行列の文字列表現が書き込まれたファイルを，各成分の間の区切り（正規表現）を指定して読み込み，行列を生成します。<br>
   * 以下は，CSVファイルに書き込まれた行列を読み込む例です。
   *
   * <pre>{@code
   * // mat.csvが以下の内容でカレントディレクトリ内に存在するとする
   * // 1.0,2.0
   * // 1.0,2.0
   * // 1.0,2.0
   * DoubleMatrix a = DoubleMatrix.readFromFile("mat.csv", ",");
   * }</pre>
   *
   * @param filename ファイル名
   * @param regex 正規表現の区切り
   * @return ファイルから読み込んだ行列
   * @throws IOException 入出力エラーが発生した場合
   * @throws IllegalArgumentException ファイルの内容を行列として解釈できない場合
   */
  public static DoubleMatrix readFromFile(String filename, String regex) throws IOException {
    ArrayList<double[]> rows = new ArrayList<double[]>();
    String line = null;

    try (BufferedReader file = Files.newBufferedReader(Paths.get(filename))) {
      while (Objects.nonNull(line = file.readLine())) {
        String[] tokens = line.split(regex);
        double[] row = new double[tokens.length];
        for (int j = 0; j < row.length; j++) {
          row[j] = Double.parseDouble(tokens[j]);
        }
        rows.add(row);
      }
    } catch (NumberFormatException nfe) {
      IOException ioe =
          new IOException(String.format("%s:%d: %s", filename, rows.size() + 1, line));
      ioe.initCause(nfe);
      throw ioe;
    } catch (IOException ioe) {
      throw ioe;
    }

    double[][] result = rows.toArray(new double[rows.size()][]);
    return (new DoubleMatrix(result, true, false));
  }

  /**
   * 行列の文字列表現(toString()の実行結果)が書き込まれたファイルを読み込み，行列を生成します。<br>
   *
   * @param filename ファイル名
   * @return ファイルから読み込んだ行列
   * @throws IOException 入出力エラーが発生した場合
   * @throws IllegalArgumentException ファイルの内容を行列として解釈できない場合
   * @see #toString()
   */
  public static DoubleMatrix readFromFile(String filename) throws IOException {
    return readFromFile(filename, DEFAULT_DELIM);
  }

  /**
   * 任意の個数の行列を水平方向に連結した行列を生成し，それを返します。<br>
   * 以下は列ベクトルを並べて行列を生成する例です。
   *
   * <pre>{@code
   * DoubleMatrix x = DoubleMatrix.createColumnVector(1, 2, 3);
   * DoubleMatrix y = DoubleMatrix.createColumnVector(4, 5, 6);
   * DoubleMatrix z = DoubleMatrix.createColumnVector(7, 8, 9);
   * DoubleMatrix A = DoubleMatrix.combineHorizontally(x, y, z);
   * }</pre>
   *
   * @param matrices 任意の個数の行列
   * @return 連結結果の行列
   * @throws IllegalArgumentException 行列の連結が出来ない(行数が異なっている)場合
   * @see #createColumnVector(double...)
   */
  public static DoubleMatrix combineHorizontally(DoubleMatrix... matrices) {
    final int rows = matrices[0].rows;
    int columns = matrices[0].columns;

    for (int k = 1; k < matrices.length; k++) {
      if (matrices[k].rows != rows) {
        throw (new IllegalArgumentException("行列の行数が揃っていません。水平方向への結合に失敗しました"));
      }
      columns += matrices[k].columns;
    }

    double[][] result = new double[rows][columns];
    int pos = 0;
    for (int k = 0; k < matrices.length; k++) {
      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < matrices[k].columns; j++) {
          result[i][j + pos] = matrices[k].get(i, j);
        }
      }
      pos += matrices[k].columns;
    }

    return (new DoubleMatrix(result, false, false));
  }

  /**
   * 任意の個数の行列を垂直方向に連結した行列を生成し，それを返します。<br>
   * 以下は行ベクトルを並べて行列を生成する例です。
   *
   * <pre>{@code
   * DoubleMatrix x = DoubleMatrix.createRowVector(1, 2, 3);
   * DoubleMatrix y = DoubleMatrix.createRowVector(4, 5, 6);
   * DoubleMatrix z = DoubleMatrix.createRowVector(7, 8, 9);
   * DoubleMatrix A = DoubleMatrix.combineVertically(x, y, z);
   * }</pre>
   *
   * @param matrices 任意の個数の行列
   * @return 連結結果の行列
   * @throws IllegalArgumentException 行列の連結が出来ない(列数が異なっている)場合
   * @see #createRowVector(double...)
   */
  public static DoubleMatrix combineVertically(DoubleMatrix... matrices) {
    int rows = matrices[0].rows;
    final int columns = matrices[0].columns;

    for (int k = 1; k < matrices.length; k++) {
      if (matrices[k].columns != columns) {
        throw (new IllegalArgumentException("行列の列数が揃っていません。垂直方向への結合に失敗しました"));
      }
      rows += matrices[k].rows;
    }

    double[][] result = new double[rows][columns];
    int pos = 0;
    for (int k = 0; k < matrices.length; k++) {
      for (int i = 0; i < matrices[k].rows; i++) {
        for (int j = 0; j < columns; j++) {
          result[i + pos][j] = matrices[k].get(i, j);
        }
      }
      pos += matrices[k].rows;
    }

    return (new DoubleMatrix(result, false, false));
  }

  /**
   * 行ベクトルを生成して，それを返します。
   *
   * @param entries 行ベクトルの成分
   * @return 行ベクトル
   */
  public static DoubleMatrix createRowVector(double... entries) {
    return (new DoubleMatrix(1, entries.length, entries));
  }

  /**
   * 列ベクトルを生成して，それを返します。
   *
   * @param entries 列ベクトルの成分
   * @return 列ベクトル
   */
  public static DoubleMatrix createColumnVector(double... entries) {
    return (new DoubleMatrix(entries.length, 1, entries));
  }

  /**
   * 対角行列を生成して，それを返します。
   *
   * @param entries 対角成分
   * @return 対角行列
   */
  public static DoubleMatrix createDiagonalMatrix(double... entries) {
    double[][] result = new double[entries.length][entries.length];
    for (int i = 0; i < entries.length; i++) {
      result[i][i] = entries[i];
    }
    return (new DoubleMatrix(result, false, false));
  }

  /**
   * 単位行列を生成して，それを返します。
   *
   * @param n 行列の次数
   * @return 単位行列
   */
  public static DoubleMatrix createIdentityMatrix(int n) {
    double[][] result = new double[n][n];
    for (int i = 0; i < n; i++) {
      result[i][i] = 1;
    }
    return (new DoubleMatrix(result, false, false));
  }

  /**
   * 型がrows * columnsで成分の値が全て0dの行列（零行列）を生成します。
   *
   * @param rows 行列の行数
   * @param columns 行列の列数
   * @return 零行列
   */
  public static DoubleMatrix createZeroMatrix(int rows, int columns) {
    return (new DoubleMatrix(rows, columns));
  }

  /**
   * 引数で渡されたdouble型2次元配列の内容で行列を生成します。
   *
   * @param matrix 行列を表すdouble型2次元配列
   * @return 行列
   * @throws IllegalArgumentException matrixを行列として解釈できない場合
   */
  public static DoubleMatrix from(double[][] matrix) {
    return (new DoubleMatrix(matrix));
  }

  /**
   * 行列のコピーを生成します。
   *
   * @param matrix コピー元の行列
   * @return コピーされた行列
   */
  public static DoubleMatrix copyOf(DoubleMatrix matrix) {
    return (new DoubleMatrix(matrix));
  }

  /**
   * 型がrows * columnsの行列を生成し，各成分の値を左上から右下にかけて順に初期化します。
   *
   * @param rows 行列の行数
   * @param columns 行列の列数
   * @param entries 初期化に使用するrows * columns個のdouble値
   * @return 行列
   * @throws IllegalArgumentException entries.length != rows * columnsの場合
   */
  public static DoubleMatrix of(int rows, int columns, double... entries) {
    return (new DoubleMatrix(rows, columns, entries));
  }

  /** 行列を表すdouble型2次元配列です。 */
  private final double[][] matrix;

  /** この行列の行数を表します。 */
  private final int rows;

  /** この行列の列数を表します。 */
  private final int columns;

  /** この行列のサイズ(rows * columnsの計算結果)を表します。 */
  private final int size;

  /**
   * このクラスのコードを直接触るプログラマのために用意された，privateなコンストラクタです。<br>
   * ラップ元のmatrixを行列として解釈してもよいかどうか検証する機能や，matrixの完全なコピーを取る機能を提供します。<br>
   * なお，ここでの検証とは，matrix.length * matrix[0].lengthの計算結果の値がmatrixの全要素数と等しいかどうか確認するプロセスのことです。<br>
   *
   * @param matrix ラップ元のdouble型2次元配列への参照。
   * @param doValidate trueなら検証を行います。
   * @param doCopy trueならmatrixの完全なコピーを作成し，それをthis.matrixに保持します。
   *     falseの場合はmatrixへの参照をそのままthis.matrixに保持します。
   * @throws IllegalArgumentException 検証した結果，matrixを行列として解釈できない場合
   */
  private DoubleMatrix(double[][] matrix, boolean doValidate, boolean doCopy) {
    if (doValidate) {
      for (int i = 1; i < matrix.length; i++) {
        if (matrix[i].length != matrix[0].length) {
          throw (new IllegalArgumentException("行列として解釈できません"));
        }
      }
    }

    this.rows = matrix.length;
    this.columns = matrix[0].length;
    this.size = matrix.length * matrix[0].length;

    if (doCopy) {
      this.matrix = new double[this.rows][this.columns];
      for (int i = 0; i < this.rows; i++) {
        System.arraycopy(matrix[i], 0, this.matrix[i], 0, this.columns);
      }
    } else {
      this.matrix = matrix;
    }
  }

  /**
   * 引数で渡されたdouble型2次元配列の内容で行列を生成します。
   *
   * @param matrix 行列を表すdouble型2次元配列
   * @throws IllegalArgumentException matrixを行列として解釈できない場合
   */
  private DoubleMatrix(double[][] matrix) {
    this(matrix, true, true);
  }

  /**
   * 型がrows * columnsで成分の値が全て0dの行列を生成します。
   *
   * @param rows 行列の行数
   * @param columns 行列の列数
   */
  private DoubleMatrix(int rows, int columns) {
    this(new double[rows][columns], false, false);
  }

  /**
   * 型がrows * columnsの行列を生成し，各成分の値を左上から右下にかけて順に初期化します。<br>
   *
   * @param rows 行列の行数
   * @param columns 行列の列数
   * @param entries 初期化に使用するrows * columns個のdouble値
   * @throws IllegalArgumentException entries.length != rows * columnsの場合
   */
  private DoubleMatrix(int rows, int columns, double... entries) {
    this(new double[rows][columns], false, false);

    if (entries.length > this.size) {
      throw (new IllegalArgumentException("第3引数以降の成分の数が多すぎます"));
    } else if (entries.length < this.size) {
      throw (new IllegalArgumentException("第3引数以降の成分の数が少なすぎます"));
    }

    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < this.columns; j++) {
        this.matrix[i][j] = entries[i * this.columns + j];
      }
    }
  }

  /**
   * コピーコンストラクタです。
   *
   * @param matrix コピー元の行列
   */
  private DoubleMatrix(DoubleMatrix matrix) {
    this(matrix.matrix, false, true);
  }

  /**
   * この行列の行数を返します。
   *
   * @return 行数
   */
  public int rows() {
    return this.rows;
  }

  /**
   * この行列の列数を返します。
   *
   * @return 列数
   */
  public int columns() {
    return this.columns;
  }

  /**
   * この行列のサイズ(rows * columnsの計算結果)を返します。
   *
   * @return サイズ
   */
  public int size() {
    return this.size;
  }

  /**
   * thisの型(rows * columns)とthatの型が等しいなら真を返します。
   *
   * @param that 任意の行列
   * @return 型が等しいならtrue
   */
  public boolean isTypeEqual(DoubleMatrix that) {
    return (this.rows == that.rows && this.columns == that.columns);
  }

  /**
   * thisとthatが等価な行列なら真を返します。
   *
   * @param that 任意の行列
   * @return this = thatならtrue
   */
  public boolean isEqual(DoubleMatrix that) {
    if (this == that) {
      return true;
    }

    if (!this.isTypeEqual(that)) {
      return false;
    }

    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < this.columns; j++) {
        if (this.get(i, j) != that.get(i, j)) {
          return false;
        }
      }
    }

    return true;
  }

  /**
   * この行列が対称かどうか判定して，結果の真偽値を返します。
   *
   * @return 対称行列なら，true
   */
  public boolean isSymmetric() {
    if (this.rows != this.columns) {
      return false;
    }

    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < this.columns; j++) {
        if (i == j) {
          continue;
        }

        if (this.get(i, j) != this.get(j, i)) {
          return false;
        }
      }
    }

    return true;
  }

  /**
   * この行列の文字列表現を返します。<br>
   * <br>
   * 実行例
   *
   * <pre>{@code
   * DoubleMatrix a = DoubleMatrix.createZeroMatrix(3, 4);
   * System.out.print(a);
   * }</pre>
   *
   * 実行結果
   *
   * <pre>{@code
   * 0.0 0.0 0.0 0.0
   * 0.0 0.0 0.0 0.0
   * 0.0 0.0 0.0 0.0
   * }</pre>
   *
   * @return この行列の文字列表現
   * @see #createZeroMatrix(int, int)
   */
  @Override
  public String toString() {
    return toString(DEFAULT_DELIM);
  }

  /**
   * この行列の文字列表現を指定された区切り文字を使用して生成し，それを返します。<br>
   * <br>
   * 実行例
   *
   * <pre>{@code
   * DoubleMatrix a = DoubleMatrix.createZeroMatrix(3, 4);
   * System.out.print(a.toString("|"));
   * }</pre>
   *
   * 実行結果
   *
   * <pre>{@code
   * 0.0|0.0|0.0|0.0
   * 0.0|0.0|0.0|0.0
   * 0.0|0.0|0.0|0.0
   * }</pre>
   *
   * @param delim 各要素間の区切り文字
   * @return この行列の文字列表現
   */
  public String toString(String delim) {
    if (delim.isEmpty() || delim.contains(".") || delim.matches(".*\\d.*")) {
      throw (new IllegalArgumentException("区切り文字が不正です: " + delim));
    }

    StringBuilder result = new StringBuilder();

    for (int i = 0; i < this.rows; i++) {
      result.append(this.matrix[i][0]);
      for (int j = 1; j < this.columns; j++) {
        result.append(delim);
        result.append(this.matrix[i][j]);
      }
      if (i < this.rows - 1) {
        result.append(System.lineSeparator());
      }
    }

    return result.toString();
  }

  /**
   * 行列の(i, j)成分を取得します。
   *
   * @param i i
   * @param j j
   * @return (i, j)成分の値
   * @throws ArrayIndexOutOfBoundsException iまたはjの値が不正な添え字の場合
   */
  public double get(int i, int j) {
    return this.matrix[i][j];
  }

  /**
   * 行列の(i, j)成分を指定された値に置き換えます。
   *
   * @param i i
   * @param j j
   * @param entry 格納される値
   * @return this
   * @throws ArrayIndexOutOfBoundsException iまたはjの値が不正な添え字の場合
   */
  public DoubleMatrix set(int i, int j, double entry) {
    this.matrix[i][j] = entry;
    return this;
  }

  /**
   * 行の入れ替えを行います。ただし，i1 == i2なら何も行いません。<br>
   * なお，i1 == i2 でも，i1またはi2の値が行列の添え字の範囲を逸脱している場合は例外をスローします。
   *
   * @param i1 任意の行番号
   * @param i2 任意の行番号
   * @return this
   * @throws ArrayIndexOutOfBoundsException i1またはi2の値が不正な添え字の場合
   */
  public DoubleMatrix swapRows(int i1, int i2) {
    // 引数が同じなら交換処理を行う必要がないので何もせずreturnする
    if (i1 == i2) {
      // ただし，引数が配列の添え字範囲を逸脱していない場合に限る
      // 逸脱している場合はそのまま交換処理を実行させ，例外を発生させる
      if ((0 <= i1 && i1 < this.rows) && (0 <= i2 && i2 < this.rows)) {
        return this;
      }
    }

    double[] tmp = this.matrix[i1];
    this.matrix[i1] = this.matrix[i2];
    this.matrix[i2] = tmp;

    return this;
  }

  /**
   * 列の入れ替えを行います。ただし，j1 == j2なら何も行いません。<br>
   * なお，j1 == j2 でも，j1またはj2の値が行列の添え字の範囲を逸脱している場合は例外をスローします。
   *
   * @param j1 任意の列番号
   * @param j2 任意の列番号
   * @return this
   * @throws ArrayIndexOutOfBoundsException j1またはj2の値が不正な添え字の場合
   */
  public DoubleMatrix swapColumns(int j1, int j2) {
    // 引数が同じなら交換処理を行う必要がないので何もせずreturnする
    if (j1 == j2) {
      // ただし，引数が配列の添え字範囲を逸脱していない場合に限る
      // 逸脱している場合はそのまま交換処理を実行させ，例外を発生させる
      if ((0 <= j1 && j1 < this.columns) && (0 <= j2 && j2 < this.columns)) {
        return this;
      }
    }

    for (int i = 0; i < this.rows; i++) {
      double tmp = this.matrix[i][j1];
      this.matrix[i][j1] = this.matrix[i][j2];
      this.matrix[i][j2] = tmp;
    }

    return this;
  }

  /**
   * this + thatを計算し，結果の行列を返します。<br>
   * ただし，thisとthatの型が異なり，計算を実行できない場合は，例外をスローします。
   *
   * @param that この行列に加算する行列。
   * @return this + that
   * @throws ArithmeticException thisとthatの型が異なり，計算を実行できない場合
   */
  public DoubleMatrix plus(DoubleMatrix that) {
    if (!this.isTypeEqual(that)) {
      throw (new ArithmeticException(
          String.format(
              "行列の型が異なるため，計算できません: (%d,%d) != (%d,%d)",
              this.rows, this.columns, that.rows, that.columns)));
    }

    double[][] result = new double[this.rows][this.columns];
    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < this.columns; j++) {
        result[i][j] = this.get(i, j) + that.get(i, j);
      }
    }

    return (new DoubleMatrix(result, false, false));
  }

  /**
   * this += thatを計算し，thisを返します。<br>
   * ただし，thisとthatの型が異なり，計算を実行できない場合は，例外をスローします。
   *
   * @param that この行列に加算する行列。
   * @return this
   * @throws ArithmeticException thisとthatの型が異なり，計算を実行できない場合
   */
  public DoubleMatrix add(DoubleMatrix that) {
    if (!this.isTypeEqual(that)) {
      throw (new ArithmeticException(
          String.format(
              "行列の型が異なるため，計算できません: (%d,%d) != (%d,%d)",
              this.rows, this.columns, that.rows, that.columns)));
    }

    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < this.columns; j++) {
        this.matrix[i][j] += that.get(i, j);
      }
    }

    return this;
  }

  /**
   * this - thatを計算し，結果の行列を返します。<br>
   * ただし，thisとthatの型が異なり，計算を実行できない場合は，例外をスローします。
   *
   * @param that この行列から減算する行列。
   * @return this - that
   * @throws ArithmeticException thisとthatの型が異なり，計算を実行できない場合
   */
  public DoubleMatrix minus(DoubleMatrix that) {
    if (!this.isTypeEqual(that)) {
      throw (new ArithmeticException(
          String.format(
              "行列の型が異なるため，計算できません: (%d,%d) != (%d,%d)",
              this.rows, this.columns, that.rows, that.columns)));
    }

    double[][] result = new double[this.rows][this.columns];
    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < this.columns; j++) {
        result[i][j] = this.get(i, j) - that.get(i, j);
      }
    }

    return (new DoubleMatrix(result, false, false));
  }

  /**
   * this -= thatを計算し，thisを返します。<br>
   * ただし，thisとthatの型が異なり，計算を実行できない場合は，例外をスローします。
   *
   * @param that この行列から減算する行列。
   * @return this
   * @throws ArithmeticException thisとthatの型が異なり，計算を実行できない場合
   */
  public DoubleMatrix sub(DoubleMatrix that) {
    if (!this.isTypeEqual(that)) {
      throw (new ArithmeticException(
          String.format(
              "行列の型が異なるため，計算できません: (%d,%d) != (%d,%d)",
              this.rows, this.columns, that.rows, that.columns)));
    }

    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < this.columns; j++) {
        this.matrix[i][j] -= that.get(i, j);
      }
    }

    return this;
  }

  /**
   * thisをk倍した行列を返します。
   *
   * @param k この行列に乗算する値。
   * @return this * k
   */
  public DoubleMatrix times(double k) {
    double[][] result = new double[this.rows][this.columns];
    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < this.columns; j++) {
        result[i][j] = k * this.get(i, j);
      }
    }

    return (new DoubleMatrix(result, false, false));
  }

  /**
   * this * thatを計算し，結果の行列を返します。<br>
   * ただし，thisの列数とthatの行数が異なり，計算を実行できない場合は，例外をスローします。
   *
   * @param that この行列に乗算する行列。
   * @return this * that
   * @throws ArithmeticException thisの列数とthatの行数が異なり，計算を実行できない場合
   */
  public DoubleMatrix times(DoubleMatrix that) {
    if (this.columns != that.rows) {
      throw (new ArithmeticException(
          String.format("列数と行数が異なるため，計算できません: %d != %d", this.columns, that.rows)));
    }

    double[][] result = new double[this.rows][that.columns];
    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < that.columns; j++) {
        for (int k = 0; k < this.columns; k++) { // or (k < that.rows)
          result[i][j] += this.get(i, k) * that.get(k, j);
        }
      }
    }

    return (new DoubleMatrix(result, false, false));
  }

  /**
   * this *= kを計算し，thisを返します。
   *
   * @param k この行列に乗算する値。
   * @return this
   */
  public DoubleMatrix mul(double k) {
    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < this.columns; j++) {
        this.matrix[i][j] *= k;
      }
    }
    return this;
  }

  /**
   * thisを転置した行列を返します。
   *
   * @return t^this
   */
  public DoubleMatrix trs() {
    double[][] result = new double[this.columns][this.rows];
    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < this.columns; j++) {
        result[j][i] = this.matrix[i][j];
      }
    }

    return (new DoubleMatrix(result, false, false));
  }
}
