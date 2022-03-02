// Usage: java -ea TestDoubleMatrix
public class TestDoubleMatrix {
  public static void main(String[] args) {

    if(false)
    { // 引数の配列が不正な場合を確認
      DoubleMatrix a = new DoubleMatrix(new double[][]{{0}, {1, 2}});

      // 以下のメッセージが出力された後，プログラムが終了する
      // - - - - - - - - - - - - - - - - -
      // [エラー] 行列として解釈できません
      // [[0.0], [1.0, 2.0]]
      // - - - - - - - - - - - - - - - - -

    } // end of block


    { // rows, columns, sizeが正しく設定されているかを確認
      DoubleMatrix a = new DoubleMatrix(new double[][]{{0, 0, 0}});
      assert a.rows == 1;
      assert a.columns == 3;
      assert a.size == 3;

      DoubleMatrix b = new DoubleMatrix(
        new double[][] {
          {0},
          {0},
          {0},
          {0},
        }
      );
      assert b.rows == 4;
      assert b.columns == 1;
      assert b.size == 4;

      DoubleMatrix c = new DoubleMatrix(
        new double[][] {
          {0, 1, 5},
          {0, 2, 4},
          {0, 3, 3},
          {0, 4, 2},
          {0, 5, 1},
        }
      );
      assert c.rows == 5;
      assert c.columns == 3;
      assert c.size == 15;

    } // end of block


    { // get(), set()の動作確認
      DoubleMatrix a = new DoubleMatrix(
        new double[][] {
          {0, 1, 2},
          {3, 4, 5},
          {6, 7, 8},
        }
      );

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
      DoubleMatrix a = new DoubleMatrix(val);

      for(int i = 0; i < val.length; i++) {
        for(int j = 0; j < val[i].length; j++) {
          val[i][j] = 42;
        }
      }

      for(int i = 0; i < val.length; i++) {
        for(int j = 0; j < val[i].length; j++) {
          assert a.get(i, j) == a.rows * i + j;
        }
      }

    } // end of block


    { // isEqual() の動作確認
      DoubleMatrix a = new DoubleMatrix(
        new double[][] {
          {0,  1,  2},
          {3,  4,  5},
          {6,  7,  8},
          {9, 10, 11},
        }
      );

      DoubleMatrix b = new DoubleMatrix(
        new double[][] {
          {0,  1,  2},
          {3,  4,  5},
          {6,  7,  8},
        }
      );

      DoubleMatrix c = new DoubleMatrix(
        new double[][] {
          {0,  1},
          {3,  4},
          {6,  7},
          {9, 10},
        }
      );

      DoubleMatrix d = new DoubleMatrix(
        new double[][] {
          {0,  1,  2},
          {3,  4,  5},
          {6,  7,  8},
          {9, 10, 11},
        }
      );

      DoubleMatrix e = new DoubleMatrix(
        new double[][] {
          {0,  1,  2},
          {3,  4,  5},
          {6,  7,  8},
          {9, 10, -11},
        }
      );

      assert !a.isEqual(null);
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


    if(false)
    { // toString() の呼び出し
      DoubleMatrix a = new DoubleMatrix(new double[][]{{0,  1,  2}});
      System.out.println(a);

      DoubleMatrix b = new DoubleMatrix(
        new double[][] {
          {0},
          {1},
          {2},
        }
      );
      System.out.println(b);

      DoubleMatrix c = new DoubleMatrix(
        new double[][] {
          {0, 3, 6,  9},
          {1, 4, 7, 10},
          {2, 5, 8, 11},
        }
      );
      System.out.print(c);
    } // end of block


    { // add() の動作確認
      DoubleMatrix a = new DoubleMatrix(new double[][]{{1,  2,  3}});
      DoubleMatrix b = new DoubleMatrix(
        new double[][] {
          {1, 4, 5},
          {2, 5, 6},
          {3, 6, 7},
        }
      );
      DoubleMatrix c = new DoubleMatrix(
        new double[][] {
          {1, 4},
          {2, 5},
          {3, 6},
        }
      );

      DoubleMatrix d = new DoubleMatrix(
        new double[][] {
          {1, 4},
          {2, 5},
          {3, 6},
        }
      );

      DoubleMatrix e = new DoubleMatrix(
        new double[][] {
          {-1, 4},
          {-2, 5},
          {-3, 6},
        }
      );

      DoubleMatrix f = new DoubleMatrix(
        new double[][] {
          {0, 8},
          {0, 10},
          {0, 12},
        }
      );


      // 実引数がnullなら例外
      try {
        a.add(null);
      }
      catch(NullPointerException npe) {
        System.err.println("a.add(null) => " + npe);
      }

      // 行数が異なる場合
      assert a.add(b) == null;
      assert b.add(a) == null;

      // 列数が異なる場合
      assert b.add(c) == null;
      assert c.add(b) == null;

      // 加算結果が正しいかどうか
      assert d.add(e).isEqual(f);
      assert e.add(d).isEqual(f);

    } // end of block


    { // sub() の動作確認
      DoubleMatrix a = new DoubleMatrix(
        new double[][] {
          {1, 4, 7, 10},
          {2, 5, 8, 11},
          {3, 6, 9, 12},
        }
      );

      DoubleMatrix b = new DoubleMatrix(
        new double[][] {
          { 1,  1,  1,  1},
          {-1, -1, -1, -1},
          { 2,  2,  2,  2},
        }
      );

      DoubleMatrix c = new DoubleMatrix(
        new double[][] {
          {0, 3, 6,  9},
          {3, 6, 9, 12},
          {1, 4, 7, 10},
        }
      );

      assert a.sub(b).isEqual(c);

    } // end of block


    { // 行列の定数倍の動作確認
      DoubleMatrix a = new DoubleMatrix(
        new double[][] {
          {-9, -3, 6},
          { 4,  5, 6},
          { 1,  2, 3},
        }
      );

      DoubleMatrix b = new DoubleMatrix(
        new double[][] {
          {-18, -6, 12},
          {  8, 10, 12},
          {  2,  4,  6},
        }
      );

      DoubleMatrix z = new DoubleMatrix(
        new double[][] {
          {0, 0, 0},
          {0, 0, 0},
          {0, 0, 0},
        }
      );

      assert a.mul(2).isEqual(b);
      assert b.mul(0.5).isEqual(a);

      // 0A = O
      assert a.mul(0).isEqual(z);

      // 1A = A
      assert a.mul(1).isEqual(a);

      // kO = O
      assert z.mul(12).isEqual(z);
    } // end of block


    { // 行列同士の掛け算の動作確認
      DoubleMatrix a = new DoubleMatrix(new double[][] {{1, 2, 3, 4, 5}});

      DoubleMatrix b = new DoubleMatrix(
        new double[][] {
          {1},
          {1},
          {1},
          {1},
          {1},
        }
      );

      DoubleMatrix c = new DoubleMatrix(
        new double[][] {
          {1},
          {2},
          {3},
          {4},
          {5},
        }
      );

      DoubleMatrix d = new DoubleMatrix(new double[][] {{1, 1, 1, 1, 1}});

      DoubleMatrix e = new DoubleMatrix(new double[][] {{15}});

      DoubleMatrix f = new DoubleMatrix(
        new double[][] {
          {1, 2, 3, 4, 5},
          {1, 2, 3, 4, 5},
          {1, 2, 3, 4, 5},
          {1, 2, 3, 4, 5},
          {1, 2, 3, 4, 5},
        }
      );

      DoubleMatrix g = new DoubleMatrix(
        new double[][] {
          {1, 1, 1, 1, 1},
          {2, 2, 2, 2, 2},
          {3, 3, 3, 3, 3},
          {4, 4, 4, 4, 4},
          {5, 5, 5, 5, 5},
        }
      );

      assert a.mul(b).isEqual(e);
      assert d.mul(c).isEqual(e);

      assert b.mul(a).isEqual(f);
      assert c.mul(d).isEqual(g);
    } // end of block


    { // createDiagonalMatrix() の動作確認
      DoubleMatrix a = DoubleMatrix.createDiagonalMatrix(new double[]{1, 2, 3});

      DoubleMatrix b = new DoubleMatrix(
        new double[][] {
          {1, 0, 0},
          {0, 2, 0},
          {0, 0, 3},
        }
      );

      DoubleMatrix c = new DoubleMatrix(
        new double[][] {
          {0, 1, 2},
          {3, 4, 5},
          {6, 7, 8},
        }
      );

      DoubleMatrix d = DoubleMatrix.createDiagonalMatrix(new double[]{2, 3, 4});

      DoubleMatrix e = new DoubleMatrix(
        new double[][] {
          { 0,  3,  8},
          { 6, 12, 20},
          {12, 21, 32},
        }
      );

      DoubleMatrix f = DoubleMatrix.createDiagonalMatrix(new double[] {2, 1});

      DoubleMatrix g = new DoubleMatrix(
        new double[][] {
          {1, 2, 3},
          {4, 5, 6},
        }
      );

      DoubleMatrix h = new DoubleMatrix(
        new double[][] {
          {2, 4, 6},
          {4, 5, 6},
        }
      );

      DoubleMatrix z = new DoubleMatrix(
        new double[][] {
          {0, 0, 0},
          {0, 0, 0},
          {0, 0, 0},
        }
      );

      assert a.isEqual(b);
      assert c.mul(d).isEqual(e);
      assert f.mul(g).isEqual(h);
      assert z.isEqual(DoubleMatrix.createDiagonalMatrix(new double[]{0, 0, 0}));
    } // end of block


    { // createIdentityMatrix() の動作確認
      DoubleMatrix a = DoubleMatrix.createIdentityMatrix(5);

      DoubleMatrix b = new DoubleMatrix(
        new double[][] {
          {1, 0, 0, 0, 0},
          {0, 1, 0, 0, 0},
          {0, 0, 1, 0, 0},
          {0, 0, 0, 1, 0},
          {0, 0, 0, 0, 1},
        }
      );

      assert a.isEqual(b);
    } // end of block


    { // 行列の転置の動作確認
      DoubleMatrix a = new DoubleMatrix(
        new double[][] {
          {1, 2, 3},
          {4, 5, 6},
        }
      );

      DoubleMatrix b = new DoubleMatrix(
        new double[][] {
          {1, 4},
          {2, 5},
          {3, 6},
        }
      );

      DoubleMatrix c = new DoubleMatrix(new double[][]{{1, 2, 3}});

      DoubleMatrix d = new DoubleMatrix(
        new double[][]{
          {1},
          {2},
          {3},
        }
      );

      DoubleMatrix e = DoubleMatrix.createDiagonalMatrix(new double[]{1, 2, 3});

      assert a.trs().isEqual(b);
      assert a.trs().trs().isEqual(a);

      assert c.trs().isEqual(d);
      assert e.trs().isEqual(e);
    } // end of block

    System.err.println();
    System.err.println("テスト完了");
  } // end of main()
} // end of class TestDoubleMatrix
