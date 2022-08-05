package progThiever;

import org.dreambot.api.Client;
import org.dreambot.api.input.mouse.destination.impl.TileDestination;
import org.dreambot.api.input.mouse.destination.impl.shape.AreaDestination;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.message.Message;

import java.awt.*;

import static org.dreambot.api.Client.getLocalPlayer;
import static org.dreambot.api.methods.MethodProvider.log;

@ScriptManifest(name = "Prog Thiever", description = "", author = "Brotato",
        version = 2.0, category = Category.THIEVING, image = "")
public class progThiever extends AbstractScript implements ChatListener {
    State state;
    Area safeSpot = new Area(3269,3412, 3270, 3412);
    String s;
    String stateForDebugging;
    int teasStolen = 0;
    //start at 25, afk every 25-50
    int actionsTillAfk = 25;

    @Override
    public void onMessage(Message m) {
        if (m.getMessage().contains("You steal a cup of tea") ) {

        teasStolen++;
            actionsTillAfk--;
        }
    }
    @Override // Infinite loop
    public int onLoop() {

        switch (getState()) {

            case LOGOUT:
                log("Target level reached -- logging out.");
                getTabs().logout();
                break;

            case THIEVING:
                stateForDebugging = "THIEVING";
                s = "Waiting for stall";
                GameObject stall = GameObjects.closest("Tea stall");
                if (actionsTillAfk <= 1){
                    log("here actions <1");
                    sleep(customSleepFunction());
                }
                if (Dialogues.inDialogue()) {
                    log("Dialogues inDialogue check hit");
                    Dialogues.spaceToContinue();
                }
                if (stall != null) {

                    s = "Found stall - looting";
                    stall.interact("Steal-from");
                }
                break;
            case DROP:
                stateForDebugging = "DROP";
                s = "Inventory full - dropping all cups of tea";
                Inventory.dropAll("Cup of tea");
                break;
            case OUTOFPOSITION:
                stateForDebugging = "OUTOFPOSITION";
                getTabs().logout();
                break;

        }
        return 2000;
    }

    private enum State {
        LOGOUT, THIEVING, DROP, OUTOFPOSITION


    }

    private State getState() {

        if (Client.isLoggedIn() && safeSpot.contains(Client.getLocalPlayer())) {
            state = State.THIEVING;
        }
        if (!safeSpot.contains(Client.getLocalPlayer())){
            state = State.OUTOFPOSITION;
        }
        if (getSkills().getRealLevel(Skill.THIEVING) >= 38) {
            state = State.LOGOUT;
        }
        if (Inventory.isFull())
            return State.DROP;
        return state;
    }

    public void onStart() {
        log("Welcome to Simple Tea Thiever by MEEEE.");
        log("If you experience any issues while running this script please report them to me on the forums.");
        log("Enjoy the script, gain some thieving levels!.");
    }

    public void onExit() {
        log("Bot ended!");
    }

    public int randomNum(int i, int k) {
        //Custom Random Number Generator
        int number = (int) (Math.random() * (k - i)) + i;
        return number;
    }
    public int customSleepFunction(){
        actionsTillAfk = randomNum(25,50);
        int randomSleepTime = randomNum(15000,45000);
        log("Bot sleeping for: " + randomSleepTime);
        return randomSleepTime;
    }
    public void cameraMovement(){
        Camera.keyboardRotateTo(20,40);
    }

    @Override
    public void onPaint(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.drawString(s, 15, 266);
        g.drawString("Teas Stolen: " + teasStolen, 15, 240);
        g.drawString("Current state: " + stateForDebugging, 15, 220);
        g.drawString("Actions till AFK: " + actionsTillAfk, 15, 200);

    }



}
