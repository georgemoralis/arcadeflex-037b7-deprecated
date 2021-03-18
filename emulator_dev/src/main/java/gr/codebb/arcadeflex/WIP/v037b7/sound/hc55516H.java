/*
 *  Ported to 0.37b7
 */
package gr.codebb.arcadeflex.WIP.v037b7.sound;

public class hc55516H {

    public static final int MAX_HC55516 = 4;

    public static class hc55516_interface {
        public hc55516_interface(int num, int[] volume) {
            this.num = num;
            this.volume = volume;
        }

        public int num;	/* total number of DACs */
        public int[] volume;//[ MAX_HC55516]

    }
}