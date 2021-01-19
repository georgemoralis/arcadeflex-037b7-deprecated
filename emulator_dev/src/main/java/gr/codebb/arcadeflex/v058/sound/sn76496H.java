/*
 * ported to v0.58
 * ported to v0.37b7
 */
package gr.codebb.arcadeflex.v058.sound;

public class sn76496H {

    public static final int MAX_76496 = 4;

    public static class SN76496interface {

        public SN76496interface(int num, int[] baseclock, int[] volume) {
            this.num = num;
            this.baseclock = baseclock;
            this.volume = volume;
        }

        public int num;/* total number of 76496 in the machine */
        public int[] baseclock;//[MAX_76496];
        public int[] volume;//[MAX_76496];
    }
}