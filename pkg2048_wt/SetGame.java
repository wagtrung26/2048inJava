/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2048_wt;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author WagTrung
 */
public class SetGame extends JPanel {

    Tile[] myTiles;
    Image bg = new ImageIcon("background.jpg").getImage();
    String font_name = "Arial";
    int tile_font_size = 99;
    int TILES_MARGIN = 20;
    boolean checkWin = false;
    boolean checkLose = false;
    int Score = 0;
    Tile[] un;
    Tile[] re;
    Stack undo = new Stack() ;
    Stack redo =  new Stack();
    int c=0;

    public SetGame() { //contructor

        // java swing
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    startGame();
                }
                if (!checkMove()) {// cannot move --> stop game
                    checkLose = true;
                }

                if (!checkWin && !checkLose) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            moveLeft();
                            break;
                        case KeyEvent.VK_RIGHT:
                            moveRight();
                            break;
                        case KeyEvent.VK_DOWN:
                            moveDown();
                            break;
                        case KeyEvent.VK_UP:
                            moveUp();
                            break;
                        case KeyEvent.VK_W:
                             checkWin=true;
                            break;
                        case KeyEvent.VK_L:
                             checkLose=true;
                            break;
                    }
                }

                if (!checkWin && !checkMove()) {
                    checkLose = true;
                }

                repaint();
            }
        });
        startGame();
    }

    public void startGame() {

        Score = 0;
        checkWin = false;
        checkLose = false;
        myTiles = new Tile[16];//4*4 tiles

        for (int i = 0; i < myTiles.length; i++) {// initial 16 tites with their value=0
            myTiles[i] = new Tile();
        }
        addTile();// add 2 tiles at the begin in random position on board 
        addTile();
    }

    ////----BORN TILE----////
    private void addTile() {
        List<Tile> list = checkSpace();//list of available space to add new title

        if (!checkSpace().isEmpty()) { // until all tiles on the board have value >0
            int pos = (int) (Math.random() * list.size()) + 0;
            Tile newTile = list.get(pos);
            if (Math.random() < 1) {
                newTile.value = 2;
            } else {
                newTile.value = 4;
            }
        }
    }

    private List<Tile> checkSpace() {

        final List<Tile> list = new ArrayList<Tile>(16);// add in list if value=0

        for (int k = 0; k < myTiles.length; k++) {
            if (myTiles[k].isEmpty()) {
                list.add(myTiles[k]);
            }
        }
        return list;
    }

    boolean checkMove() {
        if (checkSpace().size() != 0) { //cannot move if no space left
            return true;
        }
        for (int x = 0; x < 4; x++) {// cannot move if no same value next to each others
            for (int y = 0; y < 4; y++) {
                Tile t = new Tile();
                t = tileAt(x, y);
                if ((x < 3 && t.value == tileAt(x + 1, y).value)// check the tile next in hor 
                        || ((y < 3) && t.value == tileAt(x, y + 1).value)) { //and ver if the same value then can move
                    return true;
                }
            }
        }
        return false;
    }

    ////---PREPARE FOR LEFT-RIGHT-UP-DOWN----////
    private Tile tileAt(int x, int y) {
        return myTiles[x + y * 4];
    }

    private void Win(int val) {
        checkWin = (val == 2048) ? true : false;
    }

    private void full4Elems(List<Tile> l) { //be sure a list must have 4 elems
        while (l.size() != 4) { // will add until enough 4 in list
            l.add(new Tile()); //elem has value is 0 when added
        }
    }
/// Line in horizontal

    private Tile[] getLine(int index) { //get a line in horizon at 0,1,2,3
        Tile[] line = new Tile[4];
        for (int i = 0; i < 4; i++) {
            line[i] = tileAt(i, index);
        }
        return line;
    }

    private void setLine(int index, Tile[] fromArray) {//start coppy from 0 in fromArray
        System.arraycopy(fromArray, 0, myTiles, index * 4, 4);// take 4 elems add at head of each line
    }
/// Column in vertical

    private void setCol(int index, Tile[] a) {//start coppy merged Col array to array myTiles
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == index) {
                    myTiles[i + j * 4] = a[j];
                }
            }
        }

    }

    private Tile[] getCol(int index) { //get a col in vertical at 0,4,8,12
        Tile[] col = new Tile[4];
        for (int i = 0; i < 4; i++) {
            col[i] = tileAt(index, i);
        }
        return col;
    }
/// compare origin array with merged array

    private boolean compare2Array(Tile[] array1, Tile[] array2) {//check same array in line

        return Arrays.equals(array1, array2);
    }

    ////---LEFT----////
    public void moveLeft() {
        boolean checkToAdd = false;
        for (int i = 0; i < 4; i++) {

            Tile[] array1 = getLine(i);//get a line in horizontal incluing 4 elems
            Tile[] array2 = mergeLineL(arrangeLineL(array1));// arrange then merge elems in that array
            //after merged, add the line of array in array myTiles
            setLine(i, array2);
            //after stored 4 lines incluing 16 elems in each arrays
            if (!checkToAdd && !compare2Array(array1, array2)) {// only born a new tile when 2 arrays not same
                checkToAdd = true;
            }

        }

        if (checkToAdd) {
            addTile();
        }
    }

    private Tile[] arrangeLineL(Tile[] array) {// arrange to easy to merge
        LinkedList<Tile> l = new LinkedList<Tile>();
        //add the valued elems in list
        for (int i = 0; i < 4; i++) {
            if (!array[i].isEmpty()) {// in input array,if value of element=0 then not add into list
                l.addLast(array[i]);
            }
        }
        //check after adding
        if (l.size() == 0) {// all of 4 elems in a line are=0 then return input array of a line
            return array;
        } else {// in list has some elems 
            Tile[] newLine = new Tile[4];//will store the list in new array
            full4Elems(l);// a line must have 4 elements(incase of 1,2,3 elems in list)
            for (int i = 0; i < 4; i++) {
                newLine[i] = l.removeFirst();//pop the list then stored in new array
            }
            return newLine; // return an arranged array
        }
    }

    private Tile[] mergeLineL(Tile[] array) {// in a line, same value will be merged
        LinkedList<Tile> l = new LinkedList<Tile>();
        //check to merge 
        for (int i = 0; i <= 3 && !array[i].isEmpty(); i++) { // no check if elems have valued =0
            int num = array[i].value;
            if (i <= 2 && array[i].value == array[i + 1].value) {
                num *= 2;
                Score += num;
                Win(num);
                i++; // no check the next cause it already merged with the previous
            }
            l.add(new Tile(num));// add to list affter merging
        }
        if (l.size() == 0) {//cannot merge
            return array;
        } else {// success in merging
            full4Elems(l);// add for enough 4 elems in list
            return l.toArray(new Tile[4]);// covert list to array
        }
    }

////---RIGHT---/////
    public void moveRight() {
        boolean needAddTile = false;
        for (int i = 0; i < 4; i++) {

            Tile[] array1 = getLine(i);//get a line in horizontal incluing 4 elems
            Tile[] array2 = mergeLineR(moveLineR(array1));// arrange then merge elems in that array
            //affter merged, add the line of array in array myTiles
            setLine(i, array2);
            if(i==3){
             un[c]= undo.push(array1);
             c++;
            }
             
             
             
            if (!needAddTile && !compare2Array(array1, array2)) {
                needAddTile = true;
            }

        }

        if (needAddTile) {
            addTile();
        }

    }

    private Tile[] moveLineR(Tile[] oldLine) {
        LinkedList<Tile> l = new LinkedList<Tile>();
        for (int i = 0; i < 4; i++) {
            if (!oldLine[i].isEmpty()) {
                l.addLast(oldLine[i]);
            }
        }
        if (l.size() == 0) {
            return oldLine;
        } else {
            Tile[] newLine = new Tile[4];
            while (l.size() != 4) {
                l.addFirst(new Tile());
            }
            for (int i = 0; i < 4; i++) {
                newLine[i] = l.removeFirst();
            }
            return newLine;
        }
    }

    private Tile[] mergeLineR(Tile[] oldLine) {
        LinkedList<Tile> list = new LinkedList<Tile>();
        for (int i = 3; i >= 0 && !oldLine[i].isEmpty(); i--) {
            int num = oldLine[i].value;
            if (i > 0 && oldLine[i].value == oldLine[i - 1].value) {
                num *= 2;
                Score += num;
                Win(num);
                i--;
            }
            list.addFirst(new Tile(num));
        }
        if (list.size() == 0) {
            return oldLine;
        } else {
            while (list.size() != 4) {
                list.addFirst(new Tile());
            }

            return list.toArray(new Tile[4]);
        }
    }

    ////----UP----////
    public void moveUp() {

        boolean needAddTile = false;
        for (int i = 0; i < 4; i++) {

            Tile[] array1 = getCol(i);//get a line in horizontal incluing 4 elems
            Tile[] array2 = mergeColU(arrangeColU(array1));// arrange then merge elems in that array
            //affter merged, add the line of array in array myTiles
            setCol(i, array2);

            if (!needAddTile && !compare2Array(array1, array2)) {
                needAddTile = true;
            }

        }

        if (needAddTile) {
            addTile();
        }

    }

    private Tile[] arrangeColU(Tile[] oldLine) {//odline includ 4 elms0-3
        LinkedList<Tile> l = new LinkedList<Tile>();
        for (int i = 0; i < 4; i++) {
            if (!oldLine[i].isEmpty()) {
                l.addLast(oldLine[i]);
            }
        }
        if (l.size() == 0) {
            return oldLine;
        } else {
            Tile[] newLine = new Tile[4];
            full4Elems(l);
            for (int i = 0; i < 4; i++) {
                newLine[i] = l.removeFirst();
            }
            return newLine;
        }
    }

    private Tile[] mergeColU(Tile[] oldLine) {
        LinkedList<Tile> list = new LinkedList<Tile>();
        for (int i = 0; i < 4 && !oldLine[i].isEmpty(); i++) {
            int num = oldLine[i].value;
            if (i < 3 && oldLine[i].value == oldLine[i + 1].value) {
                num *= 2;
                Score += num;
                Win(num);
                i++;
            }
            list.add(new Tile(num));
        }
        if (list.size() == 0) {
            return oldLine;
        } else {
            full4Elems(list);
            return list.toArray(new Tile[4]);
        }
    }

////----DOWN---////
    public void moveDown() {

        boolean needAddTile = false;
        for (int i = 0; i < 4; i++) {

            Tile[] array1 = getCol(i);//get a line in horizontal incluing 4 elems
            Tile[] array2 = mergeColD(arrangeColD(array1));// arrange then merge elems in that array
            //affter merged, add the line of array in array myTiles
            setCol(i, array2);

            if (!needAddTile && !compare2Array(array1, array2)) {
                needAddTile = true;
            }

        }

        if (needAddTile) {
            addTile();
        }

    }

    private Tile[] arrangeColD(Tile[] oldLine) {//odline including 4 elms
        LinkedList<Tile> l = new LinkedList<Tile>();

        for (int i = 0; i < 4; i++) {
            if (!oldLine[i].isEmpty()) {
                l.addLast(oldLine[i]);
            }
        }
        if (l.size() == 0) {
            return oldLine;
        } else {
            Tile[] newLine = new Tile[4];
            while (l.size() != 4) {
                l.addFirst(new Tile());
            }
            for (int i = 0; i < 4; i++) {
                newLine[i] = l.removeFirst();
            }
            return newLine;
        }
    }

    private Tile[] mergeColD(Tile[] oldLine) {
        LinkedList<Tile> list = new LinkedList<Tile>();
        for (int i = 3; i >= 0 && !oldLine[i].isEmpty(); i--) {
            int num = oldLine[i].value;
            if (i > 0 && oldLine[i].value == oldLine[i - 1].value) {
                num *= 2;
                Score += num;
                Win(num);
                i--;
            }
            list.addFirst(new Tile(num));
        }
        if (list.size() == 0) {
            return oldLine;
        } else {
            while (list.size() != 4) {
                list.addFirst(new Tile());
            }
            return list.toArray(new Tile[4]);
        }
    }

    ////paint swing///////////////////////////////////////////////////////////
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        ImageIcon bg = new ImageIcon("background.png");
        Image bag = bg.getImage();
        g.drawImage(bag, 0, 0, null);

        g.fillRect(0, 0, this.getSize().width, this.getSize().height);
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                drawTile(g, myTiles[x + y * 4], x, y);
            }
        }
    }

    private void drawTile(Graphics g2, Tile tile, int x, int y) {
        Graphics2D g = ((Graphics2D) g2);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

        int value = tile.value;
        int xOffset = offsetCoors(x);
        int yOffset = offsetCoors(y);
///BOX
        g.setColor(tile.getBackground());//get background for Tiles
        g.fillRoundRect(xOffset, yOffset, tile_font_size, tile_font_size, 200, 200); //Box Tile
        g.setColor(tile.getForeground());// get color for font inside Tiles
///FONT
        Font font = new Font(font_name, Font.BOLD, 34);
        g.setFont(font);

        String s = String.valueOf(value);
        FontMetrics fm = getFontMetrics(font);

        int w = fm.stringWidth(s);
        int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];

        if (value != 0) {
            g.drawString(s, xOffset + (tile_font_size - w) / 2, yOffset + tile_font_size - (tile_font_size - h) / 2 - 2);
        }

        if (checkWin || checkLose) {
            g.setColor(new Color(0xf67c00));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(0xffffff));
            g.setFont(new Font(font_name, Font.BOLD, 48));
            if (checkWin) {
                g.setFont(new Font(font_name, Font.PLAIN, 150));
                g.drawString("WON", 150, 270);
                
                g.setColor(new Color(0xff3b25));//get background for Tiles
                g.fillRect(0, 430, 800, 70);
                
                g.setFont(new Font(font_name, Font.PLAIN, 20));
                g.setColor(new Color(0xfff0ef));
                g.drawString("Press ESC to play again", 250, getHeight() - 20);
            }
            if (checkLose) {
                g.setFont(new Font(font_name, Font.PLAIN, 150));
                g.drawString("LOSE", 150, 270);
                
                g.setColor(new Color(0xff3b25));//get background for Tiles
                g.fillRect(0, 430, 800, 70);
                
                g.setFont(new Font(font_name, Font.PLAIN, 20));
                g.setColor(new Color(0xfff0ef));
                g.drawString("Press ESC to play again", 250, getHeight() - 20);
            }

        }
        g.setFont(new Font(font_name, Font.TYPE1_FONT, 30));
        g.drawString("Score: " + Score, 520, 50);

    }

    private int offsetCoors(int index) {
        return index * (TILES_MARGIN + tile_font_size) + TILES_MARGIN;
    }

}
