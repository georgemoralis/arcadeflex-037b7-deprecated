/*
 * ported to 0.37b7 
 */
package gr.codebb.arcadeflex.v037b7.sound;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.sndintrf.*;
import static gr.codebb.arcadeflex.v037b7.mame.sndintrfH.*;

public class CustomSound extends snd_interface {

    static CustomSound_interface cust_intf;

    public CustomSound() {
        sound_num = SOUND_CUSTOM;
        name = "Custom";
    }

    @Override
    public int chips_num(MachineSound msound) {
        return 0;
    }

    @Override
    public int chips_clock(MachineSound msound) {
        return 0;
    }

    @Override
    public int start(MachineSound msound) {
        cust_intf = (CustomSound_interface) msound.sound_interface;
        if (cust_intf.sh_start != null) {
            return (cust_intf.sh_start).handler(msound);
        } else {
            return 0;
        }
    }

    @Override
    public void stop() {
        if (cust_intf.sh_stop != null) {
            (cust_intf.sh_stop).handler();
        }
    }

    @Override
    public void update() {
        if (cust_intf.sh_update != null) {
            (cust_intf.sh_update).handler();
        }
    }

    @Override
    public void reset() {
        //no functionality 
    }

}
