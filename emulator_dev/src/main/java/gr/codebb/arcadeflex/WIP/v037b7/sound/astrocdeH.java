/*
 * ported to v0.37b7 
*/
package gr.codebb.arcadeflex.WIP.v037b7.sound;

public class astrocdeH {

    public static final int MAX_ASTROCADE_CHIPS = 2;/* max number of emulated chips */

    public static class astrocade_interface {

        public astrocade_interface(int num, int baseclock, int[] volume) {
            this.num = num;
            this.baseclock = baseclock;
            this.volume = volume;
        }
        int num;/* total number of sound chips in the machine */
        int baseclock;/* astrocade clock rate  */
        int[] volume;//[MAX_ASTROCADE_CHIPS];/* master volume */
    }
}
