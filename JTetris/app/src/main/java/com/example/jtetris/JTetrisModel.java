package com.example.jtetris;

import java.io.Serializable;


public class JTetrisModel implements Serializable { // derived from TestMain.java in Lecture 4
    private JTetris board;
    public boolean isBlockIndex(char key) {
        int nBlks = setOfBlockArrays.length;
        int idx = key - '0';
        if (idx >= 0 && idx < nBlks)
            return true;
        else
            return false;
    }
    public Matrix getBlock(char type) { return board.setOfBlockObjects[type - '0'][0]; }
    public Matrix getScreen() { return board.get_oScreen(); }
    public JTetrisModel(int dy, int dx) throws Exception {
        JTetris.init(setOfBlockArrays);
        board = new JTetris(dy, dx);
    }
    public JTetris.TetrisState accept(char ch) throws Exception { return board.accept(ch); }
    private int[][][][] setOfBlockArrays = { // [7][4][?][?]
            {
                    {
                            { 0, 0, 1, 0 },
                            { 0, 0, 1, 0 },
                            { 0, 0, 1, 0 },
                            { 0, 0, 1, 0 },
                    },
                    {
                            { 0, 0, 0, 0 },
                            { 1, 1, 1, 1 },
                            { 0, 0, 0, 0 },
                            { 0, 0, 0, 0 }
                    },
                    {
                            { 0, 0, 1, 0 },
                            { 0, 0, 1, 0 },
                            { 0, 0, 1, 0 },
                            { 0, 0, 1, 0 },
                    },
                    {
                            { 0, 0, 0, 0 },
                            { 1, 1, 1, 1 },
                            { 0, 0, 0, 0 },
                            { 0, 0, 0, 0 }
                    }
            },
            {
                    {
                            { 1, 0, 0 },
                            { 1, 1, 1 },
                            { 0, 0, 0 }
                    },
                    {
                            { 0, 1, 1 },
                            { 0, 1, 0 },
                            { 0, 1, 0 }
                    },
                    {
                            { 0, 0, 0 },
                            { 1, 1, 1 },
                            { 0, 0, 1 }
                    },
                    {
                            { 0, 1, 0 },
                            { 0, 1, 0 },
                            { 1, 1, 0 }
                    }
            },
            {
                    {
                            { 0, 0, 1 },
                            { 1, 1, 1 },
                            { 0, 0, 0 }
                    },
                    {
                            { 0, 1, 0 },
                            { 0, 1, 0 },
                            { 0, 1, 1 }
                    },
                    {
                            { 0, 0, 0 },
                            { 1, 1, 1 },
                            { 1, 0, 0 }
                    },
                    {
                            { 1, 1, 0 },
                            { 0, 1, 0 },
                            { 0, 1, 0 }
                    }
            },
            {
                    {
                            { 0, 1, 0 },
                            { 1, 1, 1 },
                            { 0, 0, 0 }
                    },
                    {
                            { 0, 1, 0 },
                            { 0, 1, 1 },
                            { 0, 1, 0 }
                    },
                    {
                            { 0, 0, 0 },
                            { 1, 1, 1 },
                            { 0, 1, 0 }
                    },
                    {
                            { 0, 1, 0 },
                            { 1, 1, 0 },
                            { 0, 1, 0 }
                    }
            },
            {
                    {
                            { 0, 1, 0 },
                            { 1, 1, 0 },
                            { 1, 0, 0 }
                    },
                    {
                            { 1, 1, 0 },
                            { 0, 1, 1 },
                            { 0, 0, 0 }
                    },
                    {
                            { 0, 1, 0 },
                            { 1, 1, 0 },
                            { 1, 0, 0 }
                    },
                    {
                            { 1, 1, 0 },
                            { 0, 1, 1 },
                            { 0, 0, 0 }
                    }
            },
            {
                    {
                            { 0, 1, 0 },
                            { 0, 1, 1 },
                            { 0, 0, 1 }
                    },
                    {
                            { 0, 1, 1 },
                            { 1, 1, 0 },
                            { 0, 0, 0 }
                    },
                    {
                            { 0, 1, 0 },
                            { 0, 1, 1 },
                            { 0, 0, 1 }
                    },
                    {
                            { 0, 1, 1 },
                            { 1, 1, 0 },
                            { 0, 0, 0 }
                    }
            },
            {
                    {
                            { 1, 1 },
                            { 1, 1 }
                    },
                    {
                            { 1, 1 },
                            { 1, 1 }
                    },
                    {
                            { 1, 1 },
                            { 1, 1 }
                    },
                    {
                            { 1, 1 },
                            { 1, 1 }
                    }
            }
    }; // end of arrayBlock
}

