package GUI;

import Calculate.Force.CollisionDetector;
import Calculate.Vector2;
import Graphical.Constants;
import Graphical.GraphicsEngine;
import Graphical.ObjectOnDisplay;
import Graphical.Rect;
import java.awt.Color;
import java.util.ArrayList;

public class ObjectsDisplay {

    public ArrayList<ObjectOnDisplay> objects;
    // numbers needed for selection and scrolling of the object register
    private int selected = -1;
    private int shift = 0;
    private int changeShift = 0;
    // panel to remove the object
    private Panel options;
    // boolean that shows that the highlight around object should run
    private boolean onMenu = false;

    public ObjectsDisplay() {
        //list of Icons on display that are used to simplify some processes
        this.objects = new ArrayList<ObjectOnDisplay>();
        // prepares remove button panel
        this.initButtons();
    }

    public void initButtons() {
        // instantiates a panel and adds remove button there
        this.options = new Panel(new Vector2(0, 0));
        this.options.add(new Button(new Vector2(0, 0), new Vector2(100, 25),
                "Remove", new ButtonResponse()));
    }

    public void draw(GraphicsEngine ge) {
        // fills a background on the object display
        ge.setColor(Color.gray);
        ge.drawRectangle(new Vector2(0, Constants.SCREEN_HEIGHT - 150)
            , new Vector2(Constants.SCREEN_WIDTH, 150), true);
        //gets coordinates for the next icon including the scrolled amount
        int posX = 20 + this.shift;
        int posY = Constants.SCREEN_HEIGHT - 100;
        // draws each object and its name
        for (ObjectOnDisplay object : this.objects) {
           ge.setColor(Color.black);
           ge.drawText(object.getRegion().getId(), new Vector2(posX, posY));
           // shifts the object's coordinate down
           posY += 10;
           ge.setColor(object.getRegion().getColor());
           ge.drawRectangle(new Vector2(posX, posY), new Vector2(30, 30), false);
           posX += 80;
           posY -= 10;
           object.getRegion().origin.add(new Vector2(this.changeShift, 0));
           //checks should the icon be highlighted
           if (object.isHighlighted()) {
             Rect region = new Rect(object.getRegion());
             // gets correct highlight color
             ge.setColor(object.getColor());
             // draws a rectangle around an icon
             ge.drawRectangle(
                 new Vector2((int) region.getPosition().getX() + this.shift,
                     (int) region.getPosition().getY()),
                 new Vector2(region.getWidth(), region.getHeight()), false);
           }
      }
        // draws a menu to the left of the selected objects
        if (this.onMenu && this.selected != -1) {
            // draws a panel on position above the icon clicked
            Vector2 pos = new Vector2(this.objects.get(this.selected).getRegion().getPosition());
            pos.add(new Vector2(this.shift, -this.options.size.getY()));
            this.options.setPosition(pos);
            this.options.draw(ge);
        }
    }

    public void add(Rect rect) {
        // if the object has a name
        // creates a rectangle on a display
        Rect Region = new Rect(rect);
        // prepares so it is placed on the
        Region.setRotationCurrent(0);
        Region.setPosition(
                new Vector2(10 + 80 * this.objects.size(), Constants.SCREEN_HEIGHT - 115));
        Region.setSize(new Vector2(70, 70));
        this.objects.add(new ObjectOnDisplay(Region));
    }

    public void update(Vector2 point, boolean click, int shiftV) {
        this.changeShift = shiftV * 5 - this.shift;
        this.shift = shiftV * 5;
        this.checkClick(point, click);
    }

    public void checkClick(Vector2 point, boolean click) {
        boolean isOne = false;
        CollisionDetector c = new CollisionDetector();
        if (this.onMenu) {
            this.options.onMenu(point, click, "a");
            this.onMenu = this.options.getOnMenu(point);
        }
        for (int i = 0; i < this.objects.size(); i++) {
            this.objects.get(i).checkClick(point, click, c);
            if (this.objects.get(i).isSelected()) {
                this.selected = i;
                isOne = true;
            }
        }
        if (isOne) {
            this.onMenu = isOne;
        }
    }

    public boolean getRemove() {
        return this.options.buttons.get(0).response.getActive();
    }

    public int getSelectedIndex() {
        return this.selected;
    }

    //gets the name of the selected object
    // nothing is selected then null is passed
    public String getSelected() {
        if (this.objects.size() == 0) {
            return null;
        }
        if (this.selected != -1) {
            return this.objects.get(this.selected).getRegion().getId();
        }
        return null;
    }

    public void isRemoved() {
        // shifts all object icons to the left
        if (this.selected != this.objects.size() && this.selected != -1) {
            //swaps all elements
            for (int i = this.selected; i < this.objects.size(); i++) {
                Vector2 temp = new Vector2(this.objects.get(i).getRegion().getPosition());
                this.objects.get(i).getRegion().setPosition(
                        this.objects.get(this.selected).getRegion().getPosition()
                );
                this.objects.get(this.selected).getRegion().setPosition(temp);
                // swap them in the array
                // swap positions of two objects on the display and then
                this.objects.get(i).getRegion().origin.add(new Vector2(-80, 0));
            }
        }
        // removes if there is selected
        if (this.selected != -1) {
            this.objects.remove(this.selected);
        }
        // renews selected object
        this.selected = -1;
        this.options.buttons.get(0).response.activate();
    }

    public void clear() {
        this.objects.clear();
    }

    public Panel getOptions() {
        return this.options;
    }
}
