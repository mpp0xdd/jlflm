import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

// Usage: java -ea DoubleMatrixTest
public class DoubleMatrixTest {

  @FunctionalInterface
  interface Test {
    void test() throws Exception;

    static void assertThrows(Class<? extends Exception> expected, String code, Test test) {
      Class<? extends Exception> actual;
      String message;
      try {
        test.test();
        actual = null;
        message = null;
      } catch (Exception e) {
        actual = e.getClass();
        message = e.getMessage();
      }

      System.err.println(code);
      assert expected.equals(actual);
      System.err.printf(" => Exception %s thrown as expected.\n", expected);
      System.err.println(
          message != null && !message.isEmpty()
              ? message.replaceAll("(?m)^", "    ")
              : "    No message.");
      System.err.println("---------------------------------");
    }
  }

  public static void writeToFile(String filename, String str) {
    try (BufferedWriter file = Files.newBufferedWriter(Paths.get(filename))) {
      file.write(str, 0, str.length());
      file.flush();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  public static void main(String[] args) {

    boolean enableassertions = false;
    assert enableassertions = true;
    if (!enableassertions) {
      throw new AssertionError();
    }

    { // 引数の配列が不正な場合を確認
      Test.assertThrows(
          IllegalArgumentException.class,
          "new DoubleMatrix(new double[][] {{0}, {1, 2}})",
          () -> DoubleMatrix.from(new double[][] {{0}, {1, 2}}));
      Test.assertThrows(
          IllegalArgumentException.class,
          "new DoubleMatrix(new double[][] {{0, 1}, {1, 2}, {1, 2, 3}, {4, 5}})",
          () -> DoubleMatrix.from(new double[][] {{0, 1}, {1, 2}, {1, 2, 3}, {4, 5}}));
    } // end of block

    { // rows, columns, sizeが正しく設定されているかを確認
      DoubleMatrix a = DoubleMatrix.from(new double[][] {{0, 0, 0}});
      assert a.rows() == 1;
      assert a.columns() == 3;
      assert a.size() == 3;

      DoubleMatrix b =
          DoubleMatrix.from(
              new double[][] {
                {0}, {0}, {0}, {0},
              });
      assert b.rows() == 4;
      assert b.columns() == 1;
      assert b.size() == 4;

      DoubleMatrix c =
          DoubleMatrix.from(
              new double[][] {
                {0, 1, 5},
                {0, 2, 4},
                {0, 3, 3},
                {0, 4, 2},
                {0, 5, 1},
              });
      assert c.rows() == 5;
      assert c.columns() == 3;
      assert c.size() == 15;
    } // end of block

    { // get(), set()の動作確認
      DoubleMatrix a =
          DoubleMatrix.from(
              new double[][] {
                {0, 1, 2},
                {3, 4, 5},
                {6, 7, 8},
              });

      assert a.get(1, 1) == 4;
      assert a.get(2, 2) == 8;

      a.set(1, 1, -90);
      a.set(2, 2, 256);
      assert a.get(1, 1) == -90;
      assert a.get(2, 2) == 256;
    } // end of block

    { // 行列の元となった配列の値の変更の影響を受けないことを確認
      double[][] val = {
        {0, 1, 2},
        {3, 4, 5},
        {6, 7, 8},
      };
      DoubleMatrix a = DoubleMatrix.from(val);

      for (int i = 0; i < val.length; i++) {
        for (int j = 0; j < val[i].length; j++) {
          val[i][j] = 42;
        }
      }

      for (int i = 0; i < val.length; i++) {
        for (int j = 0; j < val[i].length; j++) {
          assert a.get(i, j) == a.rows() * i + j;
        }
      }
    } // end of block

    { // isEqual() の動作確認
      DoubleMatrix a =
          DoubleMatrix.from(
              new double[][] {
                {0, 1, 2},
                {3, 4, 5},
                {6, 7, 8},
                {9, 10, 11},
              });

      DoubleMatrix b =
          DoubleMatrix.from(
              new double[][] {
                {0, 1, 2},
                {3, 4, 5},
                {6, 7, 8},
              });

      DoubleMatrix c =
          DoubleMatrix.from(
              new double[][] {
                {0, 1},
                {3, 4},
                {6, 7},
                {9, 10},
              });

      DoubleMatrix d =
          DoubleMatrix.from(
              new double[][] {
                {0, 1, 2},
                {3, 4, 5},
                {6, 7, 8},
                {9, 10, 11},
              });

      DoubleMatrix e =
          DoubleMatrix.from(
              new double[][] {
                {0, 1, 2},
                {3, 4, 5},
                {6, 7, 8},
                {9, 10, -11},
              });

      Test.assertThrows(NullPointerException.class, "a.isEqual(null)", () -> a.isEqual(null));

      assert a.isEqual(a);

      // 行数が異なる場合
      assert !a.isEqual(b);
      assert !b.isEqual(a);

      // 列数が異なる場合
      assert !a.isEqual(c);
      assert !c.isEqual(a);

      // 同じ場合
      assert a.isEqual(d);
      assert d.isEqual(a);

      // 型は等しいが，成分の値が一部異っている場合
      assert !a.isEqual(e);
      assert !e.isEqual(a);
    } // end of block

    { // isSymmetric() の動作確認
      DoubleMatrix a =
          DoubleMatrix.from(
              new double[][] {
                {1, 7, 3},
                {7, 4, -5},
                {3, -5, 6},
              });

      assert a.isSymmetric();

      a.set(0, 1, 1);
      assert !a.isSymmetric();
    } // end of block

    { // toString() の動作確認
      DoubleMatrix a = DoubleMatrix.from(new double[][] {{0, 1, 2}});
      DoubleMatrix b =
          DoubleMatrix.from(
              new double[][] {
                {0}, {1}, {2},
              });
      DoubleMatrix c =
          DoubleMatrix.from(
              new double[][] {
                {0, 3, 6, 9},
                {1, 4, 7, 10},
                {2, 5, 8, 11},
              });

      assert a.toString().equals("0.0 1.0 2.0");
      assert a.toString(",").equals("0.0,1.0,2.0");
      assert b.toString().equals("0.0\n1.0\n2.0");
      assert b.toString(";").equals("0.0\n1.0\n2.0");
      assert c.toString().equals("0.0 3.0 6.0 9.0\n1.0 4.0 7.0 10.0\n2.0 5.0 8.0 11.0");
      assert c.toString(" | ")
          .equals("0.0 | 3.0 | 6.0 | 9.0\n1.0 | 4.0 | 7.0 | 10.0\n2.0 | 5.0 | 8.0 | 11.0");

      Test.assertThrows(IllegalArgumentException.class, "a.toString(\"\")", () -> a.toString(""));
      Test.assertThrows(IllegalArgumentException.class, "a.toString(\".\")", () -> a.toString("."));
      Test.assertThrows(
          IllegalArgumentException.class, "a.toString(\",.\")", () -> a.toString(",."));
      Test.assertThrows(
          IllegalArgumentException.class, "a.toString(\".,\")", () -> a.toString(".,"));
      Test.assertThrows(
          IllegalArgumentException.class, "a.toString(\",.,\")", () -> a.toString(",.,"));
      Test.assertThrows(IllegalArgumentException.class, "a.toString(\"6\")", () -> a.toString("6"));
      Test.assertThrows(
          IllegalArgumentException.class, "a.toString(\",6\")", () -> a.toString(",6"));
      Test.assertThrows(
          IllegalArgumentException.class, "a.toString(\"6,\")", () -> a.toString("6,"));
      Test.assertThrows(
          IllegalArgumentException.class, "a.toString(\",6,\")", () -> a.toString(",6,"));
    } // end of block

    { // add() の動作確認
      DoubleMatrix a = DoubleMatrix.from(new double[][] {{1, 2, 3}});
      DoubleMatrix b =
          DoubleMatrix.from(
              new double[][] {
                {1, 4, 5},
                {2, 5, 6},
                {3, 6, 7},
              });
      DoubleMatrix c =
          DoubleMatrix.from(
              new double[][] {
                {1, 4},
                {2, 5},
                {3, 6},
              });

      DoubleMatrix d =
          DoubleMatrix.from(
              new double[][] {
                {1, 4},
                {2, 5},
                {3, 6},
              });

      DoubleMatrix e =
          DoubleMatrix.from(
              new double[][] {
                {-1, 4},
                {-2, 5},
                {-3, 6},
              });

      DoubleMatrix f =
          DoubleMatrix.from(
              new double[][] {
                {0, 8},
                {0, 10},
                {0, 12},
              });

      Test.assertThrows(NullPointerException.class, "a.plus(null)", () -> a.plus(null));

      // 行数が異なる場合
      Test.assertThrows(ArithmeticException.class, "a.plus(b)", () -> a.plus(b));
      Test.assertThrows(ArithmeticException.class, "b.plus(a)", () -> b.plus(a));

      // 列数が異なる場合
      Test.assertThrows(ArithmeticException.class, "b.plus(c)", () -> b.plus(c));
      Test.assertThrows(ArithmeticException.class, "c.plus(b)", () -> c.plus(b));

      // 加算結果が正しいかどうか
      assert d.plus(e).isEqual(f);
      assert e.plus(d).isEqual(f);
    } // end of block

    { // sub() の動作確認
      DoubleMatrix a =
          DoubleMatrix.from(
              new double[][] {
                {1, 4, 7, 10},
                {2, 5, 8, 11},
                {3, 6, 9, 12},
              });

      DoubleMatrix b =
          DoubleMatrix.from(
              new double[][] {
                {1, 1, 1, 1},
                {-1, -1, -1, -1},
                {2, 2, 2, 2},
              });

      DoubleMatrix c =
          DoubleMatrix.from(
              new double[][] {
                {0, 3, 6, 9},
                {3, 6, 9, 12},
                {1, 4, 7, 10},
              });

      DoubleMatrix d = DoubleMatrix.from(new double[][] {{1, 2}});

      assert a.minus(b).isEqual(c);

      Test.assertThrows(ArithmeticException.class, "a.minus(d)", () -> a.minus(d));
      Test.assertThrows(ArithmeticException.class, "d.minus(a)", () -> d.minus(a));
    } // end of block

    { // 行列の定数倍の動作確認
      DoubleMatrix a =
          DoubleMatrix.from(
              new double[][] {
                {-9, -3, 6},
                {4, 5, 6},
                {1, 2, 3},
              });

      DoubleMatrix b =
          DoubleMatrix.from(
              new double[][] {
                {-18, -6, 12},
                {8, 10, 12},
                {2, 4, 6},
              });

      DoubleMatrix z =
          DoubleMatrix.from(
              new double[][] {
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0},
              });

      assert a.times(2).isEqual(b);
      assert b.times(0.5).isEqual(a);

      // 0A = O
      assert a.times(0).isEqual(z);

      // 1A = A
      assert a.times(1).isEqual(a);

      // kO = O
      assert z.times(12).isEqual(z);
    } // end of block

    { // 行列同士の掛け算の動作確認
      DoubleMatrix a = DoubleMatrix.from(new double[][] {{1, 2, 3, 4, 5}});

      DoubleMatrix b =
          DoubleMatrix.from(
              new double[][] {
                {1}, {1}, {1}, {1}, {1},
              });

      DoubleMatrix c =
          DoubleMatrix.from(
              new double[][] {
                {1}, {2}, {3}, {4}, {5},
              });

      DoubleMatrix d = DoubleMatrix.from(new double[][] {{1, 1, 1, 1, 1}});

      DoubleMatrix e = DoubleMatrix.from(new double[][] {{15}});

      DoubleMatrix f =
          DoubleMatrix.from(
              new double[][] {
                {1, 2, 3, 4, 5},
                {1, 2, 3, 4, 5},
                {1, 2, 3, 4, 5},
                {1, 2, 3, 4, 5},
                {1, 2, 3, 4, 5},
              });

      DoubleMatrix g =
          DoubleMatrix.from(
              new double[][] {
                {1, 1, 1, 1, 1},
                {2, 2, 2, 2, 2},
                {3, 3, 3, 3, 3},
                {4, 4, 4, 4, 4},
                {5, 5, 5, 5, 5},
              });

      assert a.times(b).isEqual(e);
      assert d.times(c).isEqual(e);

      assert b.times(a).isEqual(f);
      assert c.times(d).isEqual(g);

      Test.assertThrows(ArithmeticException.class, "d.times(e)", () -> d.times(e));
    } // end of block

    { // createDiagonalMatrix() の動作確認
      DoubleMatrix a = DoubleMatrix.createDiagonalMatrix(1, 2, 3);

      DoubleMatrix b =
          DoubleMatrix.from(
              new double[][] {
                {1, 0, 0},
                {0, 2, 0},
                {0, 0, 3},
              });

      DoubleMatrix c =
          DoubleMatrix.from(
              new double[][] {
                {0, 1, 2},
                {3, 4, 5},
                {6, 7, 8},
              });

      DoubleMatrix d = DoubleMatrix.createDiagonalMatrix(2, 3, 4);

      DoubleMatrix e =
          DoubleMatrix.from(
              new double[][] {
                {0, 3, 8},
                {6, 12, 20},
                {12, 21, 32},
              });

      DoubleMatrix f = DoubleMatrix.createDiagonalMatrix(2, 1);

      DoubleMatrix g =
          DoubleMatrix.from(
              new double[][] {
                {1, 2, 3},
                {4, 5, 6},
              });

      DoubleMatrix h =
          DoubleMatrix.from(
              new double[][] {
                {2, 4, 6},
                {4, 5, 6},
              });

      DoubleMatrix z =
          DoubleMatrix.from(
              new double[][] {
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0},
              });

      assert a.isEqual(b);
      assert c.times(d).isEqual(e);
      assert f.times(g).isEqual(h);
      assert z.isEqual(DoubleMatrix.createDiagonalMatrix(new double[] {0, 0, 0}));
      assert z.isEqual(DoubleMatrix.createDiagonalMatrix(0, 0, 0));
    } // end of block

    { // createIdentityMatrix() の動作確認
      DoubleMatrix a = DoubleMatrix.createIdentityMatrix(5);

      DoubleMatrix b =
          DoubleMatrix.from(
              new double[][] {
                {1, 0, 0, 0, 0},
                {0, 1, 0, 0, 0},
                {0, 0, 1, 0, 0},
                {0, 0, 0, 1, 0},
                {0, 0, 0, 0, 1},
              });

      assert a.isEqual(b);
    } // end of block

    { // 行列の転置の動作確認
      DoubleMatrix a =
          DoubleMatrix.from(
              new double[][] {
                {1, 2, 3},
                {4, 5, 6},
              });

      DoubleMatrix b =
          DoubleMatrix.from(
              new double[][] {
                {1, 4},
                {2, 5},
                {3, 6},
              });

      DoubleMatrix c = DoubleMatrix.from(new double[][] {{1, 2, 3}});

      DoubleMatrix d =
          DoubleMatrix.from(
              new double[][] {
                {1}, {2}, {3},
              });

      DoubleMatrix e = DoubleMatrix.createDiagonalMatrix(1, 2, 3);

      assert a.trs().isEqual(b);
      assert a.trs().trs().isEqual(a);

      assert c.trs().isEqual(d);
      assert e.trs().isEqual(e);
    } // end of block

    { // 行列同士の型の比較の動作確認
      DoubleMatrix a = DoubleMatrix.from(new double[][] {{1, 2, 3}});
      DoubleMatrix b = DoubleMatrix.from(new double[][] {{0, 0, 0}});

      DoubleMatrix c =
          DoubleMatrix.from(
              new double[][] {
                {1, 2, 3},
                {1, 2, 3},
                {1, 2, 3},
              });

      DoubleMatrix d =
          DoubleMatrix.from(
              new double[][] {
                {1, 2, 3},
                {1, 2, 3},
                {1, 0, 3},
              });

      assert !a.isEqual(b);
      assert !b.isEqual(a);
      assert a.isTypeEqual(b);
      assert b.isTypeEqual(a);

      assert !c.isEqual(d);
      assert !d.isEqual(c);
      assert c.isTypeEqual(d);
      assert d.isTypeEqual(c);
    } // end of block

    { // 行と列の交換の動作確認
      double[][] val = {
        {1, 1, 1, 1},
        {2, 2, 2, 2},
        {3, 3, 3, 3},
        {4, 4, 4, 4},
        {5, 5, 5, 5},
      };

      DoubleMatrix a = DoubleMatrix.from(val);

      DoubleMatrix b = DoubleMatrix.from(val);

      DoubleMatrix c =
          DoubleMatrix.from(
              new double[][] {
                {0, 0, 0, 1, 0},
                {0, 1, 0, 0, 0},
                {0, 0, 1, 0, 0},
                {1, 0, 0, 0, 0},
                {0, 0, 0, 0, 1},
              });

      double[][] val2 = {
        {1, 2, 3},
        {4, 5, 6},
        {7, 8, 9},
        {3, 2, 1},
      };

      DoubleMatrix d = DoubleMatrix.from(val2);

      DoubleMatrix e = DoubleMatrix.from(val2);

      DoubleMatrix f =
          DoubleMatrix.from(
              new double[][] {
                {0, 0, 1},
                {0, 1, 0},
                {1, 0, 0},
              });

      Test.assertThrows(
          ArrayIndexOutOfBoundsException.class, "a.swapRows(-1, -1)", () -> a.swapRows(-1, -1));
      Test.assertThrows(
          ArrayIndexOutOfBoundsException.class, "a.swapRows(100, 100)", () -> a.swapRows(100, 100));
      Test.assertThrows(
          ArrayIndexOutOfBoundsException.class, "a.swapColumns(-1, -1)", () -> a.swapRows(-1, -1));
      Test.assertThrows(
          ArrayIndexOutOfBoundsException.class,
          "a.swapColumns(100, 100)",
          () -> a.swapRows(100, 100));

      assert a.isEqual(b);
      assert b.isEqual(a);
      a.swapRows(0, 3);
      assert a.isEqual(c.times(b));

      assert d.isEqual(e);
      assert e.isEqual(d);
      d.swapColumns(0, 2);
      assert d.isEqual(e.times(f));
    } // end of block

    { // 自分自身に対する加算と減算の動作確認
      double[][] val = {
        {1, 2, 3},
        {4, 5, 6},
      };

      DoubleMatrix a = DoubleMatrix.from(val);

      DoubleMatrix b = DoubleMatrix.from(val);

      DoubleMatrix c =
          DoubleMatrix.from(
              new double[][] {
                {2, 2, 2},
                {2, 2, 2},
              });

      DoubleMatrix d =
          DoubleMatrix.from(
              new double[][] {
                {3, 4, 5},
                {6, 7, 8},
              });

      DoubleMatrix e = DoubleMatrix.from(new double[][] {{1, 2, 3}});

      assert !a.isEqual(d);
      a.add(c);
      assert a.isEqual(d);

      a.sub(c);
      assert !a.isEqual(d);
      assert a.isEqual(b);

      Test.assertThrows(ArithmeticException.class, "a.add(e)", () -> a.add(e));
      Test.assertThrows(ArithmeticException.class, "e.add(a)", () -> e.add(a));
      Test.assertThrows(ArithmeticException.class, "a.sub(e)", () -> a.sub(e));
      Test.assertThrows(ArithmeticException.class, "e.sub(a)", () -> e.sub(a));
    } // end of block

    { // 自分自身の定数倍の動作確認
      DoubleMatrix a = DoubleMatrix.from(new double[][] {{1, 2, 3}});

      DoubleMatrix b = DoubleMatrix.from(new double[][] {{12, 24, 36}});

      assert !a.isEqual(b);
      a.mul(12);
      assert a.isEqual(b);
    } // end of block

    { // ファイル入出力の動作確認
      DoubleMatrix a = DoubleMatrix.from(new double[][] {{1, 2, 3}});

      DoubleMatrix b =
          DoubleMatrix.from(
              new double[][] {
                {-1}, {-2}, {-3},
              });

      DoubleMatrix c =
          DoubleMatrix.from(
              new double[][] {
                {-1, -4},
                {-2, -5},
                {-3, -6},
              });

      try {
        DoubleMatrix.writeToFile(a, "tmp/tmp1.dat");
        DoubleMatrix.writeToFile(b, "tmp/tmp2.dat");
        DoubleMatrix.writeToFile(c, "tmp/tmp3.dat", ",");
      } catch (IOException ioe) {
        ioe.printStackTrace();
        System.exit(1);
      }

      DoubleMatrix d, e, f;
      d = e = f = null;
      try {
        d = DoubleMatrix.readFromFile("tmp/tmp1.dat");
        e = DoubleMatrix.readFromFile("tmp/tmp2.dat");
        f = DoubleMatrix.readFromFile("tmp/tmp3.dat", ",");
      } catch (IOException ioe) {
        ioe.printStackTrace();
        System.exit(1);
      }

      assert a.isEqual(d);
      assert b.isEqual(e);
      assert c.isEqual(f);
    } // end of block

    { // DoubleMatrix(int, int), DoubleMatrix(int, int, double...) の動作確認
      DoubleMatrix a = DoubleMatrix.createZeroMatrix(5, 1);

      DoubleMatrix b =
          DoubleMatrix.from(
              new double[][] {
                {0}, {0}, {0}, {0}, {0},
              });

      DoubleMatrix c = DoubleMatrix.createZeroMatrix(4, 5);

      DoubleMatrix d =
          DoubleMatrix.from(
              new double[][] {
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
              });

      DoubleMatrix e = DoubleMatrix.of(2, 2, 1, 2, 3, 4);

      DoubleMatrix f =
          DoubleMatrix.from(
              new double[][] {
                {1, 2},
                {3, 4},
              });

      Test.assertThrows(
          IllegalArgumentException.class,
          "new DoubleMatrix(2, 2, 1, 2, 3)",
          () -> DoubleMatrix.of(2, 2, 1, 2, 3));
      Test.assertThrows(
          IllegalArgumentException.class,
          "new DoubleMatrix(2, 2, 1, 2, 3, 4, 5)",
          () -> DoubleMatrix.of(2, 2, 1, 2, 3, 4, 5));

      assert a.isEqual(b);
      assert c.isEqual(d);
      assert e.isEqual(f);
    } // end of block

    { // createRowVector(), createColumnVector() の動作確認
      DoubleMatrix a = DoubleMatrix.createRowVector(-3, -2, -1, 0);

      DoubleMatrix b = DoubleMatrix.from(new double[][] {{-3, -2, -1, 0}});

      DoubleMatrix c = DoubleMatrix.createColumnVector(4, 5, 6, -9);

      DoubleMatrix d =
          DoubleMatrix.from(
              new double[][] {
                {4}, {5}, {6}, {-9},
              });

      assert a.isEqual(b);
      assert c.isEqual(d);
    } // end of block

    { // 行列の水平方向，垂直方向への結合の動作確認
      DoubleMatrix a = DoubleMatrix.createColumnVector(-1, 2, 3);
      DoubleMatrix b = DoubleMatrix.createColumnVector(1, -2, 3);
      DoubleMatrix c = DoubleMatrix.createColumnVector(1, 2, -3);

      DoubleMatrix d =
          DoubleMatrix.from(
              new double[][] {
                {-1, 1, 1},
                {2, -2, 2},
                {3, 3, -3},
              });

      DoubleMatrix e =
          DoubleMatrix.from(
              new double[][] {
                {1, 2, 3},
                {4, 5, 6},
              });

      DoubleMatrix f =
          DoubleMatrix.from(
              new double[][] {
                {1, 2, 3, 1, 2, 3},
                {4, 5, 6, 4, 5, 6},
              });

      DoubleMatrix g =
          DoubleMatrix.from(
              new double[][] {
                {1, 2, 3, 1, 2, 3},
                {4, 5, 6, 4, 5, 6},
                {1, 2, 3, 1, 2, 3},
                {4, 5, 6, 4, 5, 6},
                {1, 2, 3, 1, 2, 3},
                {4, 5, 6, 4, 5, 6},
              });

      Test.assertThrows(
          IllegalArgumentException.class,
          "DoubleMatrix.combineHorizontally(f, g)",
          () -> DoubleMatrix.combineHorizontally(f, g));
      Test.assertThrows(
          IllegalArgumentException.class,
          "DoubleMatrix.combineVertically(e, f, e)",
          () -> DoubleMatrix.combineVertically(e, f, e));

      assert d.isEqual(DoubleMatrix.combineHorizontally(a, b, c));
      assert d.trs().isEqual(DoubleMatrix.combineVertically(a.trs(), b.trs(), c.trs()));

      assert f.isEqual(DoubleMatrix.combineHorizontally(e, e));
      assert g.isEqual(DoubleMatrix.combineVertically(f, f, f));
    } // end of block

    { // ファイルの形式に誤りがあった場合の例外を確認
      writeToFile("tmp/tmpe1.dat", "2 3 4\n6 u 9\n");
      Test.assertThrows(
          IOException.class,
          "DoubleMatrix.readFromFile(\"tmpe1.dat\")",
          () -> DoubleMatrix.readFromFile("tmp/tmpe1.dat"));
    } // end of block

    { // コピーコンストラクタの動作確認
      DoubleMatrix a =
          DoubleMatrix.from(
              new double[][] {
                {0, 1, 2},
                {3, 4, 5},
                {6, 7, 8},
              });

      DoubleMatrix b = DoubleMatrix.from(a); // aの内容でbを作成

      assert a.isEqual(b);
      a.set(1, 1, -9);
      assert !a.isEqual(b);
    } // end of block

    System.err.println();
    System.err.println("テスト完了");
  } // end of main()
} // end of class DoubleMatrixTest
